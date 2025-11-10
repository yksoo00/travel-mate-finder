package com.multi.ouigo.domain.member.repository;

import com.multi.ouigo.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemRepository extends JpaRepository<Member, Long> {


    // 회원 번호로 회원 조회

    Optional<Member> findByNo(Long no);



}
