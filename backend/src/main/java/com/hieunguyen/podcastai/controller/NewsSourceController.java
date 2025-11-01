package com.hieunguyen.podcastai.controller;

import com.hieunguyen.podcastai.dto.request.NewsSourceRequest;
import com.hieunguyen.podcastai.dto.response.ApiResponse;
import com.hieunguyen.podcastai.dto.response.NewsSourceDto;
import com.hieunguyen.podcastai.service.NewsSourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sources")
@RequiredArgsConstructor
public class NewsSourceController {
    private final NewsSourceService newsSourceService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<NewsSourceDto>>> getAllSources() {
        List<NewsSourceDto> newsSourceDto = newsSourceService.getAllSources();
        return ResponseEntity.ok(ApiResponse.success("fetch new sources", newsSourceDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<NewsSourceDto>> getSourceById(@PathVariable Long id) {
        NewsSourceDto newsSourceDto = newsSourceService.getSourceById(id);
        return ResponseEntity.ok(ApiResponse.success("fetch new source", newsSourceDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<NewsSourceDto>> updateSourceById(
            @PathVariable Long id,
            NewsSourceRequest request) {
        NewsSourceDto response = newsSourceService.updateSource(id, request);
        return ResponseEntity.ok(ApiResponse.success("fetch new source", response));
    }

    @PostMapping("/unactive/{id}")
    public ResponseEntity<ApiResponse<Void>> unActiveSourceById(@PathVariable Long id) {
        newsSourceService.unActivateSource(id);
        return ResponseEntity.ok(ApiResponse.noContent());
    }

    @PostMapping("/active/{id}")
    public ResponseEntity<ApiResponse<Void>> activeSourceById(@PathVariable Long id) {
        newsSourceService.activateSource(id);
        return ResponseEntity.ok(ApiResponse.noContent());
    }
}
