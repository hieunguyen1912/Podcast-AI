package com.hieunguyen.podcastai.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@Slf4j
@RequiredArgsConstructor
public class NewsSchedulerConfig {

    // This will be injected by NewsAggregationService
    // private final NewsAggregationService newsAggregationService;

    /**
     * Fetch news every 30 minutes
     */
    @Scheduled(fixedRate = 30 * 60 * 1000) // 30 minutes
    public void fetchNewsScheduled() {
        log.info("Starting scheduled news fetch...");
        // newsAggregationService.fetchAllNews();
        log.info("Scheduled news fetch completed");
    }

    /**
     * Health check every 5 minutes
     */
    @Scheduled(fixedRate = 5 * 60 * 1000) // 5 minutes
    public void healthCheckScheduled() {
        log.info("Starting scheduled health check...");
        // newsAggregationService.checkAllSourcesHealth();
        log.info("Scheduled health check completed");
    }

    /**
     * Cleanup old articles every 24 hours
     */
    @Scheduled(cron = "0 0 2 * * ?") // 2 AM daily
    public void cleanupOldArticlesScheduled() {
        log.info("Starting scheduled cleanup of old articles...");
        // newsArticleService.cleanupOldArticles();
        log.info("Scheduled cleanup completed");
    }
}
