package com.example.jwtdemo2.member.repository;

import com.example.jwtdemo2.core.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByMemberId(String userEmail);

}
