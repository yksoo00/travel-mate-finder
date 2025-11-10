package com.multi.ouigo.domain.tourist.specification;

import com.multi.ouigo.domain.tourist.entity.TouristSpotEntity;
import org.springframework.data.jpa.domain.Specification;

public class TouristSpotSpecification {


    public static Specification<TouristSpotEntity> titleContains(String keyword){
        return ((root, query, criteriaBuilder)
                -> criteriaBuilder.like(root.get("title"), "%"+keyword+"%"));
    }
    public static Specification<TouristSpotEntity> descriptionContains(String keyword) {
        return (root, query, cb) -> cb.like(root.get("description"), "%" + keyword + "%");

    }
    public static Specification<TouristSpotEntity> addressContains(String keyword) {
        return (root, query, cb) -> cb.like(root.get("address"), "%" + keyword + "%");

    }
    public static Specification<TouristSpotEntity> phoneContains(String keyword) {
        return (root, query, cb) -> cb.like(root.get("phone"), "%" + keyword + "%");

    }
    public static Specification<TouristSpotEntity> districtContains(String keyword) {
        return (root, query, cb) -> cb.like(root.get("district"), "%" + keyword + "%");

    }

}
