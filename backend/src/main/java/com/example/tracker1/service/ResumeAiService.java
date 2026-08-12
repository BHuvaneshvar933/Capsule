package com.example.tracker1.service;

import com.example.tracker1.model.dto.GenerateQuestionsRequest;
import com.example.tracker1.model.dto.GenerateQuestionsResponse;
import com.example.tracker1.model.dto.ResumeJobMatchResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

@Service
public class ResumeAiService {

    private final ChatClient chatClient;

    public ResumeAiService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    private String truncate(String text, int maxLength) {
        if (text == null) return "";
        return text.length() > maxLength ? text.substring(0, maxLength) : text;
    }

    public ResumeJobMatchResponse analyzeResume(String resumeText, String jobDescription) {
        String resume = truncate(resumeText, 12000);
        String jd = truncate(jobDescription, 12000);

        String systemPrompt = "You are an expert ATS + recruiter + career coach.\n" +
                "Rules:\n" +
                "- matchScore is 0-100 (integer).\n" +
                "- matchingSkills/missingSkills/keywordsToAdd are deduplicated, concise, uppercase where appropriate (e.g., JAVA, AWS).\n" +
                "- matchingSkills: max 15 items.\n" +
                "- missingSkills: max 15 items.\n" +
                "- keywordsToAdd: max 20 items.\n" +
                "- suggestions: max 6 actionable bullet-style strings.\n" +
                "- gapAnalysis: 2-4 short sentences.";
                
        String userPrompt = "Analyze the RESUME and JOB DESCRIPTION.\n\n" +
                "RESUME:\n" + resume + "\n\n" +
                "JOB DESCRIPTION:\n" + jd;

        long aiStart = System.currentTimeMillis();
        ResumeJobMatchResponse response = chatClient.prompt()
                .system(systemPrompt)
                .user(userPrompt)
                .call()
                .entity(new ParameterizedTypeReference<ResumeJobMatchResponse>() {});
        long aiEnd = System.currentTimeMillis();
        System.out.println("Capsule AI Generation Latency (Analyze Resume): " + (aiEnd - aiStart) + " ms");

        if (response != null) {
            int score = response.getMatchScore();
            if (score < 0) score = 0;
            if (score > 100) score = 100;
            response.setMatchScore(score);
        }
        
        return response;
    }

    public GenerateQuestionsResponse generateQuestions(String resumeText, GenerateQuestionsRequest req) {
        String resume = truncate(resumeText, 8000);
        String difficulty = (req.getDifficulty() == null || req.getDifficulty().isBlank())
                ? "mixed"
                : req.getDifficulty().trim().toLowerCase();

        String systemPrompt = "You are a senior interviewer and interview coach.\n" +
                "Rules:\n" +
                "- Provide 10-12 questions total.\n" +
                "- Ensure a mix of technical + behavioral + company-specific questions.\n" +
                "- For each question, the 'type' field MUST be EXACTLY one of: \"TECHNICAL\", \"BEHAVIORAL\", or \"COMPANY\".\n" +
                "- guidance is ONE short sentence (how to answer).\n" +
                "- Keep question text concise.";

        String userPrompt = "Generate an interview practice pack for:\n" +
                "Company: " + req.getCompany() + "\n" +
                "Role: " + req.getRole() + "\n" +
                "Difficulty: " + difficulty + "\n\n" +
                "Use the candidate RESUME to personalize topics and gaps.\n\n" +
                "RESUME:\n" + resume;

        return chatClient.prompt()
                .system(systemPrompt)
                .user(userPrompt)
                .call()
                .entity(new ParameterizedTypeReference<GenerateQuestionsResponse>() {});
    }

    public String generateCoverLetter(String resumeText, String jobDescription, String companyName) {
        String resume = truncate(resumeText, 8000);
        String jd = truncate(jobDescription, 8000);

        String systemPrompt = "You are an expert career coach and professional cover letter writer.\n" +
                "Write a highly tailored, compelling, and professional cover letter.\n" +
                "Rules:\n" +
                "- Output ONLY plain text.\n" +
                "- DO NOT use markdown formatting (no bolding, no asterisks, no headers).\n" +
                "- Keep it concise (around 3-4 paragraphs max).\n" +
                "- Tone should be confident, professional, and enthusiastic.\n" +
                "- Focus on matching the candidate's skills from the resume to the job description.";

        String userPrompt = "Write a plain text cover letter for the following job at " + 
                (companyName != null && !companyName.isBlank() ? companyName : "this company") + ".\n\n" +
                "JOB DESCRIPTION:\n" + jd + "\n\n" +
                "CANDIDATE RESUME:\n" + resume;

        return chatClient.prompt()
                .system(systemPrompt)
                .user(userPrompt)
                .call()
                .content();
    }
}
