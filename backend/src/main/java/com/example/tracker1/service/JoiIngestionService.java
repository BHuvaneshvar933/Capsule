package com.example.tracker1.service;

import com.example.tracker1.model.entity.Application;
import com.example.tracker1.repository.ApplicationRepository;
import com.example.tracker1.config.VectorStoreConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service responsible for ingesting domain entities into the vector store.
 * Converts application data into vectorized documents for Semantic Search and RAG pipelines.
 */
@Service
public class JoiIngestionService {

    private final SimpleVectorStore vectorStore;
    private final ApplicationRepository applicationRepository;
    private final com.example.tracker1.repository.ResumeRepository resumeRepository;

    public JoiIngestionService(SimpleVectorStore vectorStore, ApplicationRepository applicationRepository, com.example.tracker1.repository.ResumeRepository resumeRepository) {
        this.vectorStore = vectorStore;
        this.applicationRepository = applicationRepository;
        this.resumeRepository = resumeRepository;
    }

    /**
     * Retrieves all job applications for a specified user, converts them into text documents,
     * and indexes them in the local vector store.
     *
     * @param userId the unique identifier of the user whose data should be indexed.
     * @param userEmail the email of the user to fetch resumes.
     */
    public void indexUserData(String userId, String userEmail) {
        List<Application> applications = applicationRepository.findByUserId(userId);
        
        List<Document> documents = applications.stream().map(app -> {
            String content = String.format("Job Application: Role: %s, Company: %s, Status: %s, Applied Date: %s",
                    app.getRole(), app.getCompany(), app.getStatus(), app.getAppliedDate());
            
            return new Document(content, Map.of(
                    "userId", userId,
                    "type", "APPLICATION",
                    "appId", app.getId()
            ));
        }).collect(Collectors.toList());

        if (!documents.isEmpty()) {
            vectorStore.add(documents);
        }
        
        List<com.example.tracker1.model.entity.ResumeDocument> resumes = resumeRepository.findAllByUserEmailOrderByCreatedAtDesc(userEmail);
        TokenTextSplitter splitter = new TokenTextSplitter();
        
        for (com.example.tracker1.model.entity.ResumeDocument resume : resumes) {
            if (resume.getExtractedText() != null && !resume.getExtractedText().isBlank()) {
                Document resumeDoc = new Document(
                        "Candidate Resume:\n" + resume.getExtractedText(),
                        Map.of(
                                "userId", userId,
                                "type", "RESUME",
                                "resumeId", resume.getId()
                        )
                );
                List<Document> splitDocs = splitter.apply(List.of(resumeDoc));
                vectorStore.add(splitDocs);
            }
        }
        
        // Save at the very end
        vectorStore.save(new File(VectorStoreConfig.VECTOR_STORE_FILE));
    }
}
