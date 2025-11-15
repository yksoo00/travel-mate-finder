package com.multi.ouigo.domain.tourist.entity;


import com.multi.ouigo.common.entitiy.BaseEntity;
import com.multi.ouigo.domain.review.entity.Review;
import com.multi.ouigo.domain.tourist.dto.req.TouristSpotReqDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "touristspots")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Where(clause = "del_yn = 0")
public class TouristSpot extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "dstc", nullable = false)
    private String district;
    @Column(name = "ttl", nullable = false)
    private String title;
    @Column(length = 10000, name = "dsc", nullable = false)
    private String description;
    @Column(name = "addr", nullable = false)
    private String address;
    @Column(name = "phn", nullable = false)
    private String phone;
    @OneToMany
    @JoinColumn(name = "reviews")
    @Where(clause = "del_yn = 0")
    private List<Review> reviews = new ArrayList<>();

    public void update(@Valid TouristSpotReqDto touristSpotReqDto) {
        this.district = touristSpotReqDto.getDistrict();
        this.title = touristSpotReqDto.getTitle();
        this.description = touristSpotReqDto.getDescription();
        this.address = touristSpotReqDto.getAddress();
        this.phone = touristSpotReqDto.getPhone();
    }

    public void addReview(Review review) {    // 양방향 참조 연결
        this.reviews.add(review);
        review.setTourist(this);
    }
}
