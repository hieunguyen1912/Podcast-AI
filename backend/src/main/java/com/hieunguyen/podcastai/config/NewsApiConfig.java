package com.hieunguyen.podcastai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.core5.util.Timeout;


@Configuration
@ConfigurationProperties(prefix = "news.api")
@Data
public class NewsApiConfig {
    
    private String key = "591345a44bad433cbb40718ded78128d";
    private String baseUrl = "https://newsapi.org/v2";
    private int timeout = 30000; 
    private int maxArticles = 10;
    private String defaultLanguage = "en";
    private String defaultSortBy = "publishedAt";
    private int maxContentLength = 50000;
    
    @Bean
    public RestTemplate newsApiRestTemplate() {
        RequestConfig requestConfig = RequestConfig.custom()
            .setConnectionRequestTimeout(Timeout.ofMilliseconds(timeout))
            .setResponseTimeout(Timeout.ofMilliseconds(timeout))
            .build();
        
        CloseableHttpClient httpClient = HttpClients.custom()
            .setDefaultRequestConfig(requestConfig)
            .build();
        
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setHttpClient(httpClient);
        factory.setConnectTimeout(timeout);
        factory.setReadTimeout(timeout);
        
        RestTemplate restTemplate = new RestTemplate(factory);
        
        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("X-API-Key", key);
            return execution.execute(request, body);
        });
        
        return restTemplate;
    }
}
