package com.rehome.main.dto.request;

import java.util.List;

import lombok.Data;

@Data
public class CaseFormRequestDTO {
    private Filters filters;
    private Pagination pagination;

    @Data
    public static class Filters {
        private String keyword;
        private List<Integer> cities;
        private Integer source;
        private List<Integer> shelters;
        private List<Integer> species;
        private String gender;
        private List<String> sizes;
        private List<String> ages;
        private Integer status;
        private Integer neuteredStatus;
        private Integer hasChip;
        private List<Integer> adoptionAreas;
        
    }

    @Data
    public static class Pagination {
        private Integer page;
        private Integer limit;
        private String sortOrder;
        
    }
}
