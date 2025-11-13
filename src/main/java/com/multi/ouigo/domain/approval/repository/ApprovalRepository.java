package com.multi.ouigo.domain.approval.repository;

import com.multi.ouigo.domain.approval.constant.ApprovalStatus;
import com.multi.ouigo.domain.approval.entity.Approval;
import com.multi.ouigo.domain.member.entity.Member;
import com.multi.ouigo.domain.recruit.entity.Recruit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApprovalRepository extends JpaRepository<Approval, Long> {

    boolean existsByRecruitAndMember(Recruit recruit, Member member);


    Page<Approval> findAllByRecruitId(Long id, Pageable pageable);

    Page<Approval> findAllByMemberNo(Long no, Pageable pageable);

    Page<Approval> findAllByRecruit_Member_NoAndStatus(
        Long memberNo,
        ApprovalStatus status,
        Pageable pageable
    );
}
