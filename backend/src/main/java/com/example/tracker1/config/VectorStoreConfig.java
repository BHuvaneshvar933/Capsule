package com.example.tracker1.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

/**
 * Configuration class for the application's Vector Store.
 * Initializes a file-backed SimpleVectorStore for local, lightweight RAG implementations.
 */
@Configuration
public class VectorStoreConfig {

    public static final String VECTOR_STORE_FILE = "vector_store.json";

    /**
     * Configures the SimpleVectorStore bean. Attempts to load existing vector data
     * from disk if the storage file exists.
     *
     * @param embeddingModel the model used to compute vector embeddings.
     * @return an initialized instance of SimpleVectorStore.
     */
    @Bean
    public SimpleVectorStore simpleVectorStore(EmbeddingModel embeddingModel) {
        SimpleVectorStore vectorStore = new SimpleVectorStore(embeddingModel);
        
        File file = new File(VECTOR_STORE_FILE);
        if (file.exists()) {
            vectorStore.load(file);
        }
        
        return vectorStore;
    }
}
