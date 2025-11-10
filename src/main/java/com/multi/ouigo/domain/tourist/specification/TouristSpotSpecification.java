package com.multi.ouigo.domain.tourist.specification;

import com.multi.ouigo.domain.tourist.entity.TouristSpot;
import org.springframework.data.jpa.domain.Specification;

public class TouristSpotSpecification {


    public static Specification<TouristSpot> titleContains(String keyword){
        return ((root, query, criteriaBuilder)
                -> criteriaBuilder.like(root.get("title"), "%"+keyword+"%"));
    }
    public static Specification<TouristSpot> descriptionContains(String keyword) {
        return (root, query, cb) -> cb.like(root.get("description"), "%" + keyword + "%");

    }
    public static Specification<TouristSpot> addressContains(String keyword) {
        return (root, query, cb) -> cb.like(root.get("address"), "%" + keyword + "%");

    }
    public static Specification<TouristSpot> phoneContains(String keyword) {
        return (root, query, cb) -> cb.like(root.get("phone"), "%" + keyword + "%");

    }
    public static Specification<TouristSpot> districtContains(String keyword) {
        return (root, query, cb) -> cb.like(root.get("district"), "%" + keyword + "%");

    }

}
