package com.multi.ouigo.domain.condition.mapper;

import com.multi.ouigo.domain.condition.entity.Condition;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConditionRepository extends JpaRepository<Condition, Long> {

    List<Condition> findAllByRecruitId(Long recruitId);
}
