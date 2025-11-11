package com.multi.ouigo.domain.review.repository;

import com.multi.ouigo.domain.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT r FROM Review r WHERE r.tourist.id = :touristId")
    Page<Review> findByTouristIdWithPaging(Long touristId, Pageable pageable);
}

