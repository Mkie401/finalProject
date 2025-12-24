/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */

package com.rehome.main.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rehome.main.entity.Region;

/**
 *
 * @author user
 */
public interface PetRegionsRep extends JpaRepository<Region, Long>{
    List<Region> findByCityIdOrderByNameAsc(Long cityId);
}
