package com.template.auth.dto

/**
 * 인증 토큰 응답 DTO
 */
data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "Bearer",
    val expiresIn: Long // 액세스 토큰 만료 시간 (초)
)
