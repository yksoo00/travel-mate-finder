package com.multi.ouigo.domain.approval.repository;

import com.multi.ouigo.domain.approval.entity.Approval;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApprovalRepository extends JpaRepository<Approval, Long> {

}
