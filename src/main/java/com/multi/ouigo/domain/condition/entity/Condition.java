package com.multi.ouigo.domain.condition.entity;

import com.multi.ouigo.domain.recruit.entity.Recruit;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "conditions")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Condition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @JoinColumn(name = "recruit_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Recruit recruit;


    @Column(name = "code")
    private String code;

    public void setRecruit(Recruit recruit) {
        this.recruit = recruit;
        if (recruit != null && !recruit.getConditions().contains(this)) {
            recruit.getConditions().add(this);
        }
    }

}
