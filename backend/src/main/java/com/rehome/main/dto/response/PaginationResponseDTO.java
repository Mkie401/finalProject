package com.rehome.main.dto.response;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaginationResponseDTO<T> {

    private List<T> content;

    private Integer page;
    private Integer limit;
    private Long total;
    private Integer totalPages;
    private Integer numberOfElements;

    private Boolean first;
    private Boolean last;
    private Boolean empty;

    public static <T> PaginationResponseDTO<T> fromPage(Page<T> page) {
        return PaginationResponseDTO.<T>builder()
                .content(page.getContent())
                .page(page.getNumber() + 1) // Page 內部是 0-based
                .limit(page.getSize())
                .total(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .numberOfElements(page.getNumberOfElements())
                .first(page.isFirst())
                .last(page.isLast())
                .empty(page.isEmpty())
                .build();
    }
}
