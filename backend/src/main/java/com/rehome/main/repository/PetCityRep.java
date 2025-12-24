/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */

package com.rehome.main.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rehome.main.entity.City;

/**
 *
 * @author user
 */
public interface PetCityRep extends JpaRepository<City, Long> {
     Optional<City> findByName(String name);
     List<City> findAllByOrderByNameAsc(); // 依名稱升序排序


}
