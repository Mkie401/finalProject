/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.rehome.main.dto.response;

import lombok.Data;

/**
 *
 * @author user
 */
@Data
public class CityOptionResponse {
 private Long id;
    private String name;
    
    public CityOptionResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
