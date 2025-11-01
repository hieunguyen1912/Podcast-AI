package com.hieunguyen.podcastai.util;

import com.hieunguyen.podcastai.dto.response.PaginatedResponse;
import org.springframework.data.domain.Page;

public class PaginationHelper {

    public static <T> PaginatedResponse<T> toPaginatedResponse(Page<T> page) {
        return PaginatedResponse.<T>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }
}
