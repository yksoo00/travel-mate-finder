package com.multi.ouigo.domain.recruit.repository;

import com.multi.ouigo.domain.recruit.entity.Recruit;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RecruitRepository extends JpaRepository<Recruit, Long>,
    JpaSpecificationExecutor<Recruit> {

    @EntityGraph(attributePaths = {"member"})
    Page<Recruit> findAll(Specification<Recruit> spec, Pageable pageable);

    Optional<Recruit> findByMember_No(Long memberNo);

    Page<Recruit> findAllByTouristSpotId(Long touristSpotId, Pageable pageable);

    Page<Recruit> findAllByMember_No(Long no, Pageable pageable);
}
