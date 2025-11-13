package com.multi.ouigo.common.config;

import com.multi.ouigo.common.jwt.exception.JwtAccessDeniedHandler;
import com.multi.ouigo.common.jwt.exception.JwtAuthenticationEntryPoint;
import com.multi.ouigo.common.jwt.filter.JwtFilter;
import com.multi.ouigo.common.jwt.provider.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class JwtConfig {

    private final TokenProvider tokenProvider;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final RedisTemplate<String, String> redisTemplate;


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .sessionManagement(
                sesstion -> sesstion.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            ).authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/*").permitAll()
                .requestMatchers("/path/**").permitAll()
                .requestMatchers("/api/v1/products/**").permitAll()
                .requestMatchers("/api/v1/review/**").permitAll()
                .requestMatchers("/recruit/**").permitAll()
                .requestMatchers("/layout/**").permitAll()
                .requestMatchers("/navbar/**", "/loginForm/**").permitAll()
                .requestMatchers("/css/**", "/js/**", "/images/**", "/fonts/**",
                    "/static/**",
                    "/images/**")
                .permitAll()
                .requestMatchers("/favicon.ico").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/tourist-spots", "/api/v1/tourist-spots/*")
                .permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/tourist-spots").hasAnyRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/v1/tourist-spots/*").hasAnyRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/tourist-spots/*").hasAnyRole("ADMIN")
                .requestMatchers("/api/**").hasAnyRole("ADMIN", "USER")
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .anyRequest().authenticated()

            ).addFilterBefore(new JwtFilter(tokenProvider, redisTemplate),
                UsernamePasswordAuthenticationFilter.class).exceptionHandling(
                exception -> exception.authenticationEntryPoint(jwtAuthenticationEntryPoint)
                    .accessDeniedHandler(jwtAccessDeniedHandler));

        return http.build();

    }

}
