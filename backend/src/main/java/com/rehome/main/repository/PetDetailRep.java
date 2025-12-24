/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */

package com.rehome.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rehome.main.entity.PetDetail;

/**
 *
 * @author user
 */
public interface PetDetailRep extends JpaRepository<PetDetail, Long> {

}
