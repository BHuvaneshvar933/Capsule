package com.example.tracker1.config;

import com.example.tracker1.model.entity.*;
import com.example.tracker1.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DemoSeedRunner implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;
    private final TodoRepository todoRepository;
    private final ReminderRepository reminderRepository;
    private final PushSubscriptionRepository pushSubscriptionRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!isEnabled()) return;

        String email = getenvTrimmed("DEMO_SEED_EMAIL");
        String password = getenvTrimmed("DEMO_SEED_PASSWORD");
        if (email == null || password == null) {
            // If seeding is enabled but creds are missing, fail fast so deploys don't silently skip.
            throw new IllegalStateException("DEMO_SEED_ENABLED is true, but DEMO_SEED_EMAIL/DEMO_SEED_PASSWORD are not set");
        }

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(User.builder()
                        .email(email)
                        .password(passwordEncoder.encode(password))
                        .role("ROLE_USER")
                        .build()));

        seedApplications(user);
        seedTodos(user);
        cleanupVolatileData(user);
    }

    private void seedApplications(User user) {
        if (applicationRepository.findByUserId(user.getId()).size() >= 10) return;

        LocalDate today = LocalDate.now();
        List<Application> apps = new ArrayList<>();

        apps.add(app(user, "Nimbus Systems", "Software Engineer (Backend)", today.minusDays(18), ApplicationSource.LINKEDIN, ApplicationStatus.INTERVIEW));
        apps.add(app(user, "Acorn AI", "ML Engineer", today.minusDays(15), ApplicationSource.JOB_BOARD, ApplicationStatus.OA));
        apps.add(app(user, "Capsule Labs", "Full Stack Engineer", today.minusDays(13), ApplicationSource.COMPANY_SITE, ApplicationStatus.APPLIED));
        apps.add(app(user, "Orbit Finance", "Java Developer", today.minusDays(12), ApplicationSource.RECRUITER, ApplicationStatus.APPLIED));
        apps.add(app(user, "Helio Health", "Platform Engineer", today.minusDays(10), ApplicationSource.REFERRAL, ApplicationStatus.INTERVIEW));
        apps.add(app(user, "ByteWorks", "Frontend Engineer", today.minusDays(9), ApplicationSource.LINKEDIN, ApplicationStatus.REJECTED));
        apps.add(app(user, "Stonebridge", "SRE", today.minusDays(7), ApplicationSource.OTHER, ApplicationStatus.APPLIED));
        apps.add(app(user, "Quanta Retail", "Data Engineer", today.minusDays(6), ApplicationSource.JOB_BOARD, ApplicationStatus.APPLIED));
        apps.add(app(user, "VectorCloud", "Cloud Engineer", today.minusDays(4), ApplicationSource.COMPANY_SITE, ApplicationStatus.APPLIED));
        apps.add(app(user, "Lumen Media", "Software Engineer", today.minusDays(2), ApplicationSource.LINKEDIN, ApplicationStatus.APPLIED));

        applicationRepository.saveAll(apps);
    }

    private Application app(User user, String company, String role, LocalDate appliedDate, ApplicationSource source, ApplicationStatus status) {
        List<ApplicationStatusEvent> history = List.of(
                ApplicationStatusEvent.builder()
                        .status(ApplicationStatus.APPLIED)
                        .date(appliedDate)
                        .note("seed")
                        .build()
        );

        // If we seed a later status, add a second event on a later date.
        if (status != null && status != ApplicationStatus.APPLIED) {
            history = new ArrayList<>(history);
            history.add(ApplicationStatusEvent.builder()
                    .status(status)
                    .date(appliedDate.plusDays(3))
                    .note("seed")
                    .build());
        }

        return Application.builder()
                .userId(user.getId())
                .company(company)
                .role(role)
                .jobDescription("Seeded demo application. Replace with your own job description.")
                .extractedSkills(List.of("JAVA", "SPRING", "MONGODB"))
                .source(source)
                .jobUrl("https://example.com")
                .location("Remote")
                .currency("USD")
                .salaryMin(90000)
                .salaryMax(140000)
                .status(status == null ? ApplicationStatus.APPLIED : status)
                .statusHistory(history)
                .appliedDate(appliedDate)
                .lastUpdated(LocalDate.now())
                .interviews(List.of(
                        Interview.builder()
                                .roundName("Recruiter Screen")
                                .interviewDate(appliedDate.plusDays(5))
                                .result(null)
                                .notes("Seeded demo interview")
                                .build()
                ))
                .build();
    }

    private void seedTodos(User user) {
        if (todoRepository.findByUserIdOrderByCompletedAscDueDateAscCreatedAtDesc(user.getId()).size() >= 6) return;

        Instant now = Instant.now();
        List<Todo> todos = List.of(
                todo(user, "Send follow-up email to Nimbus", "Job Search", TodoPriority.HIGH, LocalDate.now().plusDays(1), false, now),
                todo(user, "Complete OA for Acorn AI", "Interview Prep", TodoPriority.HIGH, LocalDate.now().plusDays(2), false, now),
                todo(user, "Update resume keywords for Platform roles", "Career", TodoPriority.MEDIUM, LocalDate.now().plusDays(3), false, now),
                todo(user, "Schedule mock interview", "Interview Prep", TodoPriority.MEDIUM, LocalDate.now().plusDays(4), false, now),
                todo(user, "Review Spring Security basics", "Study", TodoPriority.LOW, LocalDate.now().plusDays(5), false, now),
                todo(user, "Clean up job tracker tags", "Admin", TodoPriority.LOW, LocalDate.now().minusDays(3), true, now)
        );

        todoRepository.saveAll(todos);
    }

    private Todo todo(User user, String title, String category, TodoPriority priority, LocalDate dueDate, boolean completed, Instant now) {
        Instant completedAt = completed ? now.minusSeconds(3600) : null;
        return Todo.builder()
                .userId(user.getId())
                .title(title)
                .description("Seeded demo todo")
                .category(category)
                .priority(priority)
                .dueDate(dueDate)
                .completed(completed)
                .completedAt(completedAt)
                .createdAt(now.minusSeconds(86400))
                .updatedAt(now)
                .build();
    }

    private void cleanupVolatileData(User user) {
        // These are user/browser specific; keep the demo seed idempotent by not creating them.
        reminderRepository.deleteAll(reminderRepository.findByUserIdOrderByRemindAtAsc(user.getId()));
        pushSubscriptionRepository.deleteAll(pushSubscriptionRepository.findByUserId(user.getId()));
    }

    private static boolean isEnabled() {
        String v = System.getenv("DEMO_SEED_ENABLED");
        if (v == null) return false;
        String t = v.trim().toLowerCase();
        return t.equals("1") || t.equals("true") || t.equals("yes") || t.equals("on");
    }

    private static String getenvTrimmed(String key) {
        String v = System.getenv(key);
        if (v == null) return null;
        String t = v.trim();
        return t.isBlank() ? null : t;
    }
}
