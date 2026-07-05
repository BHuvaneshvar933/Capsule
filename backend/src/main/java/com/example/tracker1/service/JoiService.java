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
import com.example.tracker1.model.entity.User;
import com.example.tracker1.repository.UserRepository;

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
    private final ChatMemory chatMemory = new InMemoryChatMemory();

    /**
     * Constructs a new JoiService.
     *
     * @param chatClientBuilder the builder used to configure the ChatClient.
     * @param vectorStore       the vector store used for document retrieval (RAG).
     * @param userRepository    the repository used to fetch user details.
     */
    public JoiService(ChatClient.Builder chatClientBuilder, VectorStore vectorStore, UserRepository userRepository) {
        this.chatClient = chatClientBuilder.build();
        this.vectorStore = vectorStore;
        this.userRepository = userRepository;
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
                .withTopK(100)
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
        
        logger.info("JoiService final context for user '{}' is empty? {}", userId, context.isBlank());

        String userName = userRepository.findById(userId)
                .map(User::getName)
                .orElse("the user");

        String systemPrompt = "You are Joi, a highly intelligent and professional AI career assistant. " +
                "You are talking to a user named " + userName + ". " +
                "Your job is to answer the user's questions strictly based on the provided context (their job applications and tasks). " +
                "If the answer is not in the context, politely say you don't know. Do not hallucinate. " +
                "Format your responses cleanly. IMPORTANT: Do NOT use phrases like 'from the given context' or 'I will answer in bullet points'. Just answer naturally.\n\n" +
                "--- PROVIDED CONTEXT ---\n" +
                (context.isBlank() ? "No context available." : context);

        return chatClient.prompt()
                .system(systemPrompt)
                .user(message)
                .advisors(new MessageChatMemoryAdvisor(chatMemory, userId, 10))
                .call()
                .content();
    }
}
