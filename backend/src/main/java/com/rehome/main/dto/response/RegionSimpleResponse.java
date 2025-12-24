package com.rehome.main.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegionSimpleResponse {
    private Long id;
    private Long cityId;
    private String name;
}
