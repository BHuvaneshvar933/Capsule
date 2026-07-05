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
            String skills = app.getExtractedSkills() != null ? String.join(", ", app.getExtractedSkills()) : "None";
            String desc = app.getJobDescription() != null ? app.getJobDescription() : "None";
            String content = String.format("Job Application:\n- Role: %s\n- Company: %s\n- Status: %s\n- Date: %s\n- Location: %s\n- Salary: %s %s-%s\n- Skills: %s\n- Description: %s",
                    app.getRole(), app.getCompany(), app.getStatus(), app.getAppliedDate(),
                    app.getLocation(), app.getCurrency(), app.getSalaryMin(), app.getSalaryMax(),
                    skills, desc);
            
            // Use app.getId() to prevent duplication on re-indexing
            return new Document(app.getId(), content, Map.of(
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

    public void indexApplication(Application app) {
        String skills = app.getExtractedSkills() != null ? String.join(", ", app.getExtractedSkills()) : "None";
        String desc = app.getJobDescription() != null ? app.getJobDescription() : "None";
        String content = String.format("Job Application:\n- Role: %s\n- Company: %s\n- Status: %s\n- Date: %s\n- Location: %s\n- Salary: %s %s-%s\n- Skills: %s\n- Description: %s",
                app.getRole(), app.getCompany(), app.getStatus(), app.getAppliedDate(),
                app.getLocation(), app.getCurrency(), app.getSalaryMin(), app.getSalaryMax(),
                skills, desc);
        
        Document doc = new Document(app.getId(), content, Map.of(
                "userId", app.getUserId(),
                "type", "APPLICATION",
                "appId", app.getId()
        ));
        
        vectorStore.add(List.of(doc));
        vectorStore.save(new File(VectorStoreConfig.VECTOR_STORE_FILE));
    }

    public void removeApplication(String appId) {
        vectorStore.delete(List.of(appId));
        vectorStore.save(new File(VectorStoreConfig.VECTOR_STORE_FILE));
    }
}
