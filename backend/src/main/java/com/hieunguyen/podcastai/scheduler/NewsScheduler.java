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

    /**
     * Fetch news every 30 minutes
     */
    @Scheduled(fixedRate = 30 * 60 * 1000) // 30 minutes
    public void fetchNewsScheduled() {
        log.info("Starting scheduled news fetch");
        
        try {
            int articlesCount = newsAggregationService.fetchAllNews();
            log.info("Scheduled fetch completed. Articles fetched: {}", articlesCount);
            
        } catch (Exception e) {
            log.error("Scheduled news fetch failed: {}", e.getMessage(), e);
        }
    }

    /**
     * Health check every hour
     */
    @Scheduled(fixedRate = 60 * 60 * 1000) // 1 hour
    public void healthCheckScheduled() {
        log.info("Starting scheduled health check");
        
        try {
            boolean isHealthy = newsAggregationService.checkAllSourcesHealth();
            log.info("Health check completed. All sources healthy: {}", isHealthy);
            
        } catch (Exception e) {
            log.error("Scheduled health check failed: {}", e.getMessage(), e);
        }
    }
}
