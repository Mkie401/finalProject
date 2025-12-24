package com.rehome.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rehome.main.entity.QuestionType;

public interface QuestionTypeRepo extends JpaRepository<QuestionType, Long> {

}
