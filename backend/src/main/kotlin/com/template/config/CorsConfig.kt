package com.template.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

/**
 * CORS 설정
 * - 프론트엔드 개발 서버 및 프로덕션 도메인 허용
 */
@Configuration
class CorsConfig {

    @Value("\${cors.allowed-origins:http://localhost:3000}")
    private lateinit var allowedOrigins: String

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration().apply {
            // 허용할 Origin 목록 (콤마로 구분)
            allowedOrigins = this@CorsConfig.allowedOrigins.split(",").map { it.trim() }

            // 허용할 HTTP 메서드
            allowedMethods = listOf("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")

            // 허용할 헤더
            allowedHeaders = listOf("*")

            // 인증 정보 포함 허용
            allowCredentials = true

            // Preflight 요청 캐시 시간 (1시간)
            maxAge = 3600L
        }

        return UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/api/**", configuration)
        }
    }
}
