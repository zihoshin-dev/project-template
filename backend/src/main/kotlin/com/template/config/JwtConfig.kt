package com.template.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * JWT 설정 속성 클래스
 */
@Component
@ConfigurationProperties(prefix = "jwt")
class JwtConfig {
    /**
     * JWT 서명에 사용할 비밀 키 (최소 256비트 = 32자 이상 권장)
     */
    lateinit var secret: String

    /**
     * 액세스 토큰 만료 시간 (밀리초) - 기본값: 15분
     */
    var accessTokenExpiration: Long = 900_000L // 15 minutes

    /**
     * 리프레시 토큰 만료 시간 (밀리초) - 기본값: 7일
     */
    var refreshTokenExpiration: Long = 604_800_000L // 7 days
}
