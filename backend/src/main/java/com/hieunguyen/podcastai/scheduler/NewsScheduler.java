package com.hieunguyen.podcastai.scheduler;

import com.hieunguyen.podcastai.service.NewsAggregationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NewsScheduler {

    private final NewsAggregationService newsAggregationService;

    @Scheduled(fixedRate = 30 * 60 * 1000)
    public void fetchNewsScheduled() {
        log.info("Starting scheduled news fetch");
        long startTime = System.currentTimeMillis();

        try {
            int articlesCount = newsAggregationService.fetchAllNews();
            long duration = System.currentTimeMillis() - startTime;

            log.info("News fetch completed successfully. " +
                            "Articles: {}, Duration: {}ms",
                    articlesCount, duration);

        } catch (Exception e) {
            log.error("News fetch failed after {}ms",
                    System.currentTimeMillis() - startTime, e);
            // Optional: Gửi alert nếu cần
            // alertService.notifySchedulerFailure("fetchNews", e);
        }
    }

    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void healthCheckScheduled() {
        log.info("Starting health check");

        try {
            boolean isHealthy = newsAggregationService.checkAllSourcesHealth();

            if (isHealthy) {
                log.info("Health check: All sources OK");
            } else {
                log.warn("Health check: Some sources unhealthy");
                // alertService.notifyUnhealthySources();
            }

        } catch (Exception e) {
            log.error("Health check failed", e);
        }
    }
}