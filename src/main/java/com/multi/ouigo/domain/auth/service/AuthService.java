package com.multi.ouigo.domain.auth.service;


import com.multi.ouigo.common.exception.custom.DuplicateUsernameException;
import com.multi.ouigo.common.jwt.dto.TokenDto;
import com.multi.ouigo.common.jwt.service.TokenService;
import com.multi.ouigo.domain.member.dto.req.MemberSignInDto;
import com.multi.ouigo.domain.member.dto.req.MemberSignUpDto;
import com.multi.ouigo.domain.member.entity.Member;
import com.multi.ouigo.domain.member.mapper.MemberMapper;
import com.multi.ouigo.domain.member.repository.MemberRepository;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, String> redisTemplate;

    private final CustomUserDetailService customUserDetailService;
    private final TokenService tokenService;

    @Transactional
    public void signUp(MemberSignUpDto memberSignUpDto) {
        if (memberRepository.findByMemberId(memberSignUpDto.getMemberId()).isPresent()) {
            throw new DuplicateUsernameException("아이디 중복 중복");
        }
        String role = memberSignUpDto.getMemberId().toLowerCase().contains("admin") ? "ROLE_ADMIN"
            : "ROLE_USER";
        memberSignUpDto.setMemberPassword(
            passwordEncoder.encode(memberSignUpDto.getMemberPassword()));
        Member member = memberMapper.toEntity(memberSignUpDto);
        member.setRole(role);
        Member savedMember = memberRepository.save(member);
        if (savedMember == null) {
            throw new RuntimeException("회원가입에 실패했습니다.");
        }
        String key = "ROLE:" + memberSignUpDto.getMemberId();
        redisTemplate.opsForValue()
            .set(key, savedMember.getRole(), Duration.ofHours(1));

        String savedRole = redisTemplate.opsForValue().get(key);
        if (savedRole == null) {
            throw new RuntimeException("Redis에 저장 실패");
        }
    }

    public TokenDto login(MemberSignInDto memberSignInDto) {
        UserDetails userDetails = customUserDetailService.loadUserByUsername(
            memberSignInDto.getMemberId());
        if (!passwordEncoder.matches(memberSignInDto.getMemberPassword(),
            userDetails.getPassword())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다");
        }
        List<String> roles = userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .toList(); // 롤이 여러개 일때 VIP,PREMIUM 등등 여러 역할이 있을 때 어차피 확장해야한다...
        Map<String, Object> loginData = new HashMap<>(); // 나중에 더 데이터가 들어갔을 때 확장성에 용이하다
        loginData.put("memberId", memberSignInDto.getMemberId());
        loginData.put("roles", roles);

        TokenDto token = tokenService.createToken(loginData);
        return token;
    }
}

