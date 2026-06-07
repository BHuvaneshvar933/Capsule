package com.example.tracker1.controller;

import com.example.tracker1.service.MetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MetricsController {

    private final MetricsService metricsService;

    @GetMapping("/metrics")
    public MetricsResponse metrics() {
        MetricsService.Snapshot s = metricsService.snapshot();
        return MetricsResponse.from(s);
    }

    public record MetricsResponse(
            Instant startedAt,
            AiMetrics ai,
            PushMetrics push
    ) {
        static MetricsResponse from(MetricsService.Snapshot s) {
            return new MetricsResponse(
                    s.startedAt,
                    AiMetrics.from(s),
                    PushMetrics.from(s)
            );
        }
    }

    public record AiMetrics(
            Endpoint analyzeResume,
            Endpoint generateQuestions
    ) {
        static AiMetrics from(MetricsService.Snapshot s) {
            return new AiMetrics(
                    new Endpoint(s.aiAnalyzeCount, s.aiAnalyzeTotalMs, s.aiAnalyzeMaxMs),
                    new Endpoint(s.aiQuestionsCount, s.aiQuestionsTotalMs, s.aiQuestionsMaxMs)
            );
        }
    }

    public record PushMetrics(
            long attempts,
            long success,
            long failed,
            double deliveryRate
    ) {
        static PushMetrics from(MetricsService.Snapshot s) {
            long attempts = s.pushAttemptCount;
            long success = s.pushSuccessCount;
            long failed = s.pushFailCount;
            double rate = attempts <= 0 ? 0.0 : ((double) success) / attempts;
            return new PushMetrics(attempts, success, failed, rate);
        }
    }

    public record Endpoint(
            long count,
            long totalMs,
            long maxMs,
            long avgMs
    ) {
        public Endpoint(long count, long totalMs, long maxMs) {
            this(count, totalMs, maxMs, count <= 0 ? 0 : Math.round((double) totalMs / count));
        }
    }
}
