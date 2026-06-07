package com.example.tracker1.service;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class MetricsService {

    private final Instant startedAt = Instant.now();

    private final AtomicLong aiAnalyzeCount = new AtomicLong(0);
    private final AtomicLong aiAnalyzeTotalMs = new AtomicLong(0);
    private final AtomicLong aiAnalyzeMaxMs = new AtomicLong(0);

    private final AtomicLong aiQuestionsCount = new AtomicLong(0);
    private final AtomicLong aiQuestionsTotalMs = new AtomicLong(0);
    private final AtomicLong aiQuestionsMaxMs = new AtomicLong(0);

    private final AtomicLong pushAttemptCount = new AtomicLong(0);
    private final AtomicLong pushSuccessCount = new AtomicLong(0);
    private final AtomicLong pushFailCount = new AtomicLong(0);

    public Instant getStartedAt() {
        return startedAt;
    }

    public void recordAiAnalyze(long durationMs) {
        aiAnalyzeCount.incrementAndGet();
        aiAnalyzeTotalMs.addAndGet(Math.max(0, durationMs));
        updateMax(aiAnalyzeMaxMs, durationMs);
    }

    public void recordAiQuestions(long durationMs) {
        aiQuestionsCount.incrementAndGet();
        aiQuestionsTotalMs.addAndGet(Math.max(0, durationMs));
        updateMax(aiQuestionsMaxMs, durationMs);
    }

    public void recordPushAttempt() {
        pushAttemptCount.incrementAndGet();
    }

    public void recordPushSuccess() {
        pushSuccessCount.incrementAndGet();
    }

    public void recordPushFailure() {
        pushFailCount.incrementAndGet();
    }

    public Snapshot snapshot() {
        Snapshot s = new Snapshot();
        s.startedAt = startedAt;

        s.aiAnalyzeCount = aiAnalyzeCount.get();
        s.aiAnalyzeTotalMs = aiAnalyzeTotalMs.get();
        s.aiAnalyzeMaxMs = aiAnalyzeMaxMs.get();

        s.aiQuestionsCount = aiQuestionsCount.get();
        s.aiQuestionsTotalMs = aiQuestionsTotalMs.get();
        s.aiQuestionsMaxMs = aiQuestionsMaxMs.get();

        s.pushAttemptCount = pushAttemptCount.get();
        s.pushSuccessCount = pushSuccessCount.get();
        s.pushFailCount = pushFailCount.get();
        return s;
    }

    private static void updateMax(AtomicLong max, long value) {
        long v = Math.max(0, value);
        long prev;
        do {
            prev = max.get();
            if (v <= prev) return;
        } while (!max.compareAndSet(prev, v));
    }

    public static class Snapshot {
        public Instant startedAt;

        public long aiAnalyzeCount;
        public long aiAnalyzeTotalMs;
        public long aiAnalyzeMaxMs;

        public long aiQuestionsCount;
        public long aiQuestionsTotalMs;
        public long aiQuestionsMaxMs;

        public long pushAttemptCount;
        public long pushSuccessCount;
        public long pushFailCount;
    }
}
