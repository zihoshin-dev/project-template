package com.template.auth

import com.template.config.JwtConfig
import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.*
import javax.crypto.SecretKey

/**
 * JWT 토큰 생성 및 검증 서비스
 */
@Service
class JwtService(
    private val jwtConfig: JwtConfig
) {
    private val secretKey: SecretKey by lazy {
        Keys.hmacShaKeyFor(jwtConfig.secret.toByteArray())
    }

    /**
     * 액세스 토큰 생성
     */
    fun generateAccessToken(userDetails: UserDetails): String {
        return generateToken(
            userDetails = userDetails,
            expiration = jwtConfig.accessTokenExpiration
        )
    }

    /**
     * 리프레시 토큰 생성
     */
    fun generateRefreshToken(userDetails: UserDetails): String {
        return generateToken(
            userDetails = userDetails,
            expiration = jwtConfig.refreshTokenExpiration
        )
    }

    /**
     * 토큰에서 이메일(subject) 추출
     */
    fun extractEmail(token: String): String? {
        return extractClaim(token) { it.subject }
    }

    /**
     * 토큰 유효성 검증
     */
    fun isTokenValid(token: String, userDetails: UserDetails): Boolean {
        val email = extractEmail(token)
        return email == userDetails.username && !isTokenExpired(token)
    }

    /**
     * 토큰 만료 여부 확인
     */
    fun isTokenExpired(token: String): Boolean {
        val expiration = extractClaim(token) { it.expiration }
        return expiration?.before(Date()) ?: true
    }

    /**
     * 액세스 토큰 만료 시간 (초) 반환
     */
    fun getAccessTokenExpirationSeconds(): Long {
        return jwtConfig.accessTokenExpiration / 1000
    }

    private fun generateToken(
        userDetails: UserDetails,
        expiration: Long,
        extraClaims: Map<String, Any> = emptyMap()
    ): String {
        val now = Date()
        val expiryDate = Date(now.time + expiration)

        return Jwts.builder()
            .claims(extraClaims)
            .subject(userDetails.username)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(secretKey)
            .compact()
    }

    private fun <T> extractClaim(token: String, claimsResolver: (Claims) -> T?): T? {
        val claims = extractAllClaims(token) ?: return null
        return claimsResolver(claims)
    }

    private fun extractAllClaims(token: String): Claims? {
        return try {
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .payload
        } catch (e: JwtException) {
            null
        }
    }
}
