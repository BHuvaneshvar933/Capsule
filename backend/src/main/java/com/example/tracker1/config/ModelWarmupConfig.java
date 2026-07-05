package com.example.tracker1.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Configuration
public class ModelWarmupConfig {

    private static final Logger logger = LoggerFactory.getLogger(ModelWarmupConfig.class);
    private final EmbeddingModel embeddingModel;

    public ModelWarmupConfig(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void warmup() {
        logger.info("Warming up the Embedding Model (this will trigger PyTorch/ONNX downloads if not already cached)...");
        try {
            embeddingModel.embed("warmup");
            logger.info("Embedding Model warmup complete! Ready for user requests.");
        } catch (Exception e) {
            logger.error("Failed to warmup embedding model: " + e.getMessage());
        }
    }
}
