package com.template.config

import com.template.auth.JwtAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfigurationSource

/**
 * Spring Security 설정
 * - JWT 기반 Stateless 인증
 * - CORS 지원
 * - 메서드 수준 보안 활성화
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    private val userDetailsService: UserDetailsService,
    private val corsConfigurationSource: CorsConfigurationSource
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            // CSRF 비활성화 (JWT 사용으로 불필요)
            .csrf { it.disable() }

            // CORS 설정 적용
            .cors { it.configurationSource(corsConfigurationSource) }

            // 세션 사용 안함 (Stateless)
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }

            // URL 기반 접근 제어
            .authorizeHttpRequests { auth ->
                auth
                    // 인증 API 허용
                    .requestMatchers("/api/auth/**").permitAll()

                    // Health check 허용
                    .requestMatchers("/actuator/health").permitAll()

                    // Swagger/OpenAPI 문서 허용 (필요시)
                    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                    // OPTIONS 요청 허용 (CORS preflight)
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                    // 나머지는 인증 필요
                    .anyRequest().authenticated()
            }

            // JWT 필터 추가
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun authenticationProvider(): AuthenticationProvider {
        val provider = DaoAuthenticationProvider(userDetailsService)
        provider.setPasswordEncoder(passwordEncoder())
        return provider
    }

    @Bean
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager {
        return config.authenticationManager
    }
}
