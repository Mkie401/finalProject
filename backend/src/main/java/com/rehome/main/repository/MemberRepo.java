package com.rehome.main.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rehome.main.entity.Member;

public interface MemberRepo extends JpaRepository<Member, Long>{
	Optional<Member> findByEmail(String email);
}
