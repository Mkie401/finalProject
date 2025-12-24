package com.rehome.main.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rehome.main.entity.Qna;

public interface QnaRepo extends JpaRepository<Qna, Integer>{

    String RANDOM_SQL = "SELECT * FROM qna ORDER BY RAND() LIMIT :count";
	
    @Query("SELECT q FROM Qna q JOIN FETCH q.questionType")
    List<Qna> findAllWithQuestionType();

   // 抓隨機的 ID 
    @Query(value = RANDOM_SQL, nativeQuery = true)
    List<Integer> findRandomIds(@Param("count") Integer count);
    
    // 根據 ID 列表抓資料，並使用 JOIN FETCH (或 EntityGraph) 解決 N+1
    @Query("SELECT q FROM Qna q JOIN FETCH q.questionType WHERE q.id IN :ids")
    List<Qna> findByIdsWithFetch(@Param("ids") List<Integer> ids);

}
