package com.example.tracker1.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

/**
 * Service responsible for orchestrating interactions with the Joi AI assistant.
 * Utilizes Spring AI to interface with the underlying LLM (e.g., Ollama) and applies
 * Retrieval-Augmented Generation (RAG) to ensure responses are context-aware.
 */
@Service
public class JoiService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final ChatMemory chatMemory = new InMemoryChatMemory();

    /**
     * Constructs a new JoiService.
     *
     * @param chatClientBuilder the builder used to configure the ChatClient.
     * @param vectorStore       the vector store used for document retrieval (RAG).
     */
    public JoiService(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
        this.chatClient = chatClientBuilder.build();
        this.vectorStore = vectorStore;
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
        // Increase topK to ensure we grab enough context for specific queries.
        // Set similarity threshold to 0.0 to prevent specific queries from being dropped due to low semantic match scores.
        SearchRequest searchRequest = SearchRequest.defaults()
                .withFilterExpression("userId == '" + userId + "'")
                .withTopK(5)
                .withSimilarityThreshold(0.0);

        String systemPrompt = "You are Joi, a highly intelligent and professional AI career assistant. " +
                "You have been provided with internal context information containing the user's job applications and tasks. " +
                "Your job is to answer the user's questions based ONLY on this provided context. " +
                "If the answer is not in the context, politely say that you do not have that information recorded. " +
                "Do not hallucinate. Do not ask the user to provide context, because the system has already provided it to you silently.";

        return chatClient.prompt()
                .system(systemPrompt)
                .user(message)
                .advisors(
                        new MessageChatMemoryAdvisor(chatMemory, userId, 10),
                        new QuestionAnswerAdvisor(vectorStore, searchRequest)
                )
                .call()
                .content();
    }
}
