package com.example.tracker1.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import com.example.tracker1.model.entity.Application;
import com.example.tracker1.model.entity.User;
import com.example.tracker1.repository.ApplicationRepository;
import com.example.tracker1.repository.UserRepository;
import org.springframework.data.domain.PageRequest;

/**
 * Service responsible for orchestrating interactions with the Joi AI assistant.
 * Utilizes Spring AI to interface with the underlying LLM (e.g., Ollama) and applies
 * Retrieval-Augmented Generation (RAG) to ensure responses are context-aware.
 */
@Service
public class JoiService {
    private static final Logger logger = LoggerFactory.getLogger(JoiService.class);

    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;
    private final ChatMemory chatMemory = new InMemoryChatMemory();

    /**
     * Constructs a new JoiService.
     *
     * @param chatClientBuilder the builder used to configure the ChatClient.
     * @param vectorStore       the vector store used for document retrieval (RAG).
     * @param userRepository    the repository used to fetch user details.
     */
    public JoiService(ChatClient.Builder chatClientBuilder, VectorStore vectorStore, UserRepository userRepository, ApplicationRepository applicationRepository) {
        this.chatClient = chatClientBuilder.build();
        this.vectorStore = vectorStore;
        this.userRepository = userRepository;
        this.applicationRepository = applicationRepository;
    }

    /**
     * Processes a user's chat message and returns the AI's response.
     * Retrieves relevant domain data from the vector store scoped to the specific user.
     *
     * @param message the prompt provided by the user.
     * @param userId  the unique identifier of the authenticated user, used for data isolation.
     * @return the generated response from the AI model.
     */
    public String chat(String message, String userId) {
        // SimpleVectorStore does not support metadata filtering natively yet.
        // We fetch a larger pool of documents and manually filter them by userId to ensure data isolation.
        SearchRequest searchRequest = SearchRequest.query(message)
                .withTopK(10000)
                .withSimilarityThreshold(0.0);
        
        java.util.List<org.springframework.ai.document.Document> rawDocs = vectorStore.similaritySearch(searchRequest);
        logger.info("JoiService found {} raw docs in vector store for user '{}'", rawDocs.size(), userId);
        
        String context = rawDocs.stream()
                .filter(doc -> {
                    boolean match = userId.equals(String.valueOf(doc.getMetadata().get("userId")));
                    logger.info("Doc ID: {}, Meta userId: {}, Match: {}", doc.getId(), doc.getMetadata().get("userId"), match);
                    return match;
                })
                .limit(10)
                .map(org.springframework.ai.document.Document::getContent)
                .collect(java.util.stream.Collectors.joining("\n\n"));
        
        // HYBRID INJECTION: To prevent the "I had to specifically ask for it" issue, we always inject the user's 
        // most recent 10 applications directly from MongoDB. This guarantees Joi is always "aware" of their current active work,
        // even if their message (e.g. "hello") has zero semantic similarity to the application.
        java.util.List<Application> recentApps = applicationRepository.findByUserId(userId, PageRequest.of(0, 10)).getContent();
        String recentAppsContext = recentApps.stream()
                .map(app -> {
                    String skills = app.getExtractedSkills() != null ? String.join(", ", app.getExtractedSkills()) : "None";
                    String desc = app.getJobDescription() != null ? app.getJobDescription() : "None";
                    return String.format("Job Application:\n- Role: %s\n- Company: %s\n- Status: %s\n- Date: %s\n- Location: %s\n- Salary: %s %s-%s\n- Skills: %s\n- Description: %s", 
                            app.getRole(), app.getCompany(), app.getStatus(), app.getAppliedDate(),
                            app.getLocation(), app.getCurrency(), app.getSalaryMin(), app.getSalaryMax(),
                            skills, desc);
                })
                .collect(java.util.stream.Collectors.joining("\n\n"));

        String finalContext = "--- RECENT ACTIVE APPLICATIONS ---\n" + 
                              (recentAppsContext.isBlank() ? "None" : recentAppsContext) + 
                              "\n\n--- ADDITIONAL RELEVANT DATA ---\n" + 
                              (context.isBlank() ? "None" : context);

        logger.info("JoiService final context for user '{}' is empty? {}", userId, finalContext.isBlank());

        String userName = userRepository.findById(userId)
                .map(User::getName)
                .orElse("the user");

        String systemPrompt = "You are Joi, a highly intelligent and professional AI career assistant. " +
                "You are talking to a user named " + userName + ". " +
                "Your job is to answer the user's questions strictly based on the provided context (their job applications and tasks). " +
                "If the answer is not in the context, politely say you don't know. Do not hallucinate. " +
                "Format your responses cleanly. IMPORTANT: Do NOT use phrases like 'from the given context' or 'I will answer in bullet points'. Just answer naturally.\n\n" +
                "--- PROVIDED CONTEXT ---\n" +
                finalContext;

        return chatClient.prompt()
                .system(systemPrompt)
                .user(message)
                .advisors(new MessageChatMemoryAdvisor(chatMemory, userId, 10))
                .call()
                .content();
    }
}
