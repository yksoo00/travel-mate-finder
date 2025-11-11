package com.multi.ouigo.domain.tourist.service;

import com.multi.ouigo.domain.tourist.dto.req.TouristSpotReqDto;
import com.multi.ouigo.domain.tourist.dto.res.TouristSpotAllResDto;
import com.multi.ouigo.domain.tourist.dto.res.TouristSpotResDto;
import com.multi.ouigo.domain.tourist.entity.TouristSpot;
import com.multi.ouigo.domain.tourist.mapper.TouristSpotMapper;
import com.multi.ouigo.domain.tourist.repository.TouristSpotRepository;
import com.multi.ouigo.domain.tourist.specification.TouristSpotSpecification;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TouristSpotServiceImpl implements TouristSpotService {

    private final TouristSpotRepository touristSpotRepository;
    private final TouristSpotMapper touristSpotMapper;
    private final RestTemplate restTemplate; //

    @Value("${tour-api.service-key}")
    private String tourApiServiceKey;

    @Override
    public Page<TouristSpotResDto> getTouristSpots(String keyword, Pageable pageable) {
        Specification<TouristSpot> spec = (root, query, cb) -> null;
        if (keyword != null && !keyword.isBlank()) {
            spec = spec.and(Specification.anyOf( // anyOf = OR 조건
                    TouristSpotSpecification.titleContains(keyword),
                    TouristSpotSpecification.descriptionContains(keyword),
                    TouristSpotSpecification.addressContains(keyword),
                    TouristSpotSpecification.districtContains(keyword),
                    TouristSpotSpecification.phoneContains(keyword)
            ));
        }
        Page<TouristSpot> page = touristSpotRepository.findAll(spec, pageable);
        return page.map(touristSpotMapper::toResDto);
    }

    @Override
    public List<TouristSpotResDto> getTouristSpots() {
        List<TouristSpot> touristSpots = touristSpotRepository.findAll();

        return touristSpots.stream()
                .map(touristSpotMapper::toResDto)
                .toList();
    }

    @Override
    public TouristSpotAllResDto getTouristSpotById(Long id) {
        TouristSpot touristSpot = touristSpotRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("조회할 관광지가 존재하지 않습니다."));
        return touristSpotMapper.toAllResDto(touristSpot);
    }

    @Transactional
    @Override
    public Long save(TouristSpotReqDto touristSpotReqDto) {

        TouristSpot touristSpot = touristSpotMapper.toEntity(touristSpotReqDto);
        return touristSpotRepository.save(touristSpot).getId();
    }

    @Transactional
    @Override
    public void updateById(Long id, @Valid TouristSpotReqDto touristSpotReqDto) {
        TouristSpot touristSpot = touristSpotRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("수정할 데이터가 없음."));

        touristSpot.update(touristSpotReqDto);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        touristSpotRepository.deleteById(id);
    }

    @Override
    public String getImages(String keyword) {
        String MobileOS = "ETC";
        String MobileApp = "OuiGoApp";
        String numOfRows = "5";
        String _type = "json";

        try {

            org.springframework.web.util.UriComponentsBuilder builder =
                    org.springframework.web.util.UriComponentsBuilder.fromHttpUrl("https://apis.data.go.kr/B551011/PhotoGalleryService1/gallerySearchList1")
                            .queryParam("serviceKey", tourApiServiceKey)
                            .queryParam("MobileOS", MobileOS)
                            .queryParam("MobileApp", MobileApp)
                            .queryParam("numOfRows", numOfRows)
                            .queryParam("_type", _type)
                            .queryParam("keyword", keyword);

            String urlString = builder.toUriString().replace("+", "%20");

            URI uri = new URI(urlString);

            System.out.println("################                Request URI = " + uri);

            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    entity,
                    String.class
            );
            return response.getBody();

        } catch (Exception e) {
            e.printStackTrace();
            return "{\"response\": {\"header\": {\"resultCode\": \"9999\", \"resultMsg\": \"" + e.getMessage() + "\"}}}";
        }
    }
}
