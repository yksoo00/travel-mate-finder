package com.multi.ouigo.common.jwt.service;

import com.multi.ouigo.common.exception.RefreshTokenException;
import com.multi.ouigo.common.exception.custom.TokenException;
import com.multi.ouigo.common.jwt.dto.TokenDto;
import com.multi.ouigo.common.jwt.provider.TokenProvider;
import com.multi.ouigo.domain.member.entity.Member;
import com.multi.ouigo.domain.member.repository.MemberRepository;
import io.jsonwebtoken.Claims;
import java.time.Duration;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TokenService {

    private final TokenProvider tokenProvider;
    private final RedisTemplate<String, String> redisTemplate;
    private final MemberRepository memberRepository;


    @Transactional(noRollbackFor = RefreshTokenException.class)
    public <T> TokenDto createToken(T t) {
        String memberId;
        List<String> roles;
        String accessToken;
        String refreshToken;

        if (t instanceof String) {
            String jwt = resolveToken((String) t);
            Claims claims = tokenProvider.parseClames(jwt);

            memberId = claims.getSubject();
            String savedToken = redisTemplate.opsForValue().get(memberId);
            if (savedToken == null) {
                log.warn("[TokenService] Redis에 리프레시 토큰이 존재하지 않음 (만료 또는 로그아웃)");
                throw new RefreshTokenException("리프레시 토큰이 만료되었습니다. 다시 로그인하세요.");
            }
            if (!savedToken.equals(jwt)) {
                log.error("[TokenService] 리프레시 토큰 불일치");
                throw new RefreshTokenException("유효하지 않은 리프레시 토큰입니다.");
            }

            Optional<Member> member = memberRepository.findByMemberId(memberId);
            String role = member.get().getRole();
            roles = Arrays.asList(role.split(","));

            log.info("Map MemberId >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> {}", memberId);
            log.info("Map Roles >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> {}", roles);

            refreshToken = handleRefreshToken(memberId);

            accessToken = createAccessToken(memberId, roles);
            redisTemplate.opsForValue().set(memberId, refreshToken, Duration.ofMinutes(5));

            log.info("[TokenService] 리프레시 토큰 재발급 완료 - memberId: {}", memberId);
        } else if (t instanceof Map) {
            Map<String, Object> data = (Map<String, Object>) t;

            memberId = (String) data.get("memberId");
            roles = (List<String>) data.get("roles");

            log.info("Map MemberEmail >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> {}", memberId);
            log.info("Map Roles >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> {}", roles);
            refreshToken = handleRefreshToken(memberId);

            accessToken = createAccessToken(memberId, roles);

        } else {
            throw new IllegalArgumentException("Invalid token type !!");
        }

        return TokenDto.builder().accessToken(accessToken).refreshToken(refreshToken).build();
    }

    private String resolveToken(String token) {
        // Bearer 접두어가 있는 경우 제거하고 순수한 토큰 반환
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return token;
    }

    @Transactional(noRollbackFor = RefreshTokenException.class)
    public String handleRefreshToken(String memberId) {
        redisTemplate.delete(memberId);

        String newRefreshToken = createRefreshToken(memberId);

        redisTemplate.opsForValue().set(memberId, newRefreshToken, Duration.ofMinutes(5));

        return newRefreshToken;
    }


    private String createAccessToken(String memberId, List<String> roles) {
        return tokenProvider.generateToken(memberId, roles, "A");
    }

    private String createRefreshToken(String memberId) {
        return tokenProvider.generateToken(memberId, null, "R");
    }

    public void deleteRefreshToken(String accessToken) {
        String token = resolveToken(accessToken);
        String email = tokenProvider.getUserId(token);

        log.info("Refresh Token 삭제 완료 : 사용자 email - {}", email);
    }

    public void registBlackList(String accessToken) {
        // "Bearer " 제거 (헤더에서 받은 경우)
        String token = resolveToken(accessToken); // Bearer 제거
        if (!tokenProvider.validateToken(token)) {
            throw new TokenException("유효하지 않은 액세스 토큰입니다.");
        }

        // 토큰 만료 시간 추출
        Claims claims = tokenProvider.parseClames(token);
        Date expiration = claims.getExpiration();
        long now = System.currentTimeMillis();
        long remainTime = expiration.getTime() - now;

        if (remainTime <= 0) {
            log.warn("[TokenService] 이미 만료된 토큰입니다. 블랙리스트 등록 생략.");
            throw new TokenException("만료된 토큰 입니다. 다시 로그인 해주세요");
        }

        // Redis Key 설정: blacklist:{토큰}
        String key = "blacklist:" + token;

        // Value는 단순 표시용 (예: "logout")
        redisTemplate.opsForValue().set(key, "logout", Duration.ofMillis(remainTime));

        System.out.println("블랙리스트 등록 완료: " + key + " (TTL: " + expiration + "ms)");
    }
}
