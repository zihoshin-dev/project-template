package com.template.auth

import com.template.config.JwtConfig
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldNotBeEmpty
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User

/**
 * JwtService 단위 테스트
 */
class JwtServiceTest : DescribeSpec({

    val jwtConfig = JwtConfig().apply {
        secret = "test-secret-key-for-jwt-signing-must-be-at-least-32-characters"
        accessTokenExpiration = 900_000L  // 15분
        refreshTokenExpiration = 604_800_000L  // 7일
    }
    val jwtService = JwtService(jwtConfig)

    val testUserDetails = User.builder()
        .username("test@example.com")
        .password("password")
        .authorities(SimpleGrantedAuthority("ROLE_USER"))
        .build()

    describe("generateAccessToken") {
        it("유효한 액세스 토큰을 생성한다") {
            // when
            val token = jwtService.generateAccessToken(testUserDetails)

            // then
            token.shouldNotBeEmpty()
            jwtService.extractEmail(token) shouldBe "test@example.com"
            jwtService.isTokenExpired(token) shouldBe false
        }
    }

    describe("generateRefreshToken") {
        it("유효한 리프레시 토큰을 생성한다") {
            // when
            val token = jwtService.generateRefreshToken(testUserDetails)

            // then
            token.shouldNotBeEmpty()
            jwtService.extractEmail(token) shouldBe "test@example.com"
            jwtService.isTokenExpired(token) shouldBe false
        }
    }

    describe("extractEmail") {
        context("유효한 토큰에서") {
            it("이메일을 추출한다") {
                // given
                val token = jwtService.generateAccessToken(testUserDetails)

                // when
                val email = jwtService.extractEmail(token)

                // then
                email shouldBe "test@example.com"
            }
        }

        context("유효하지 않은 토큰에서") {
            it("null을 반환한다") {
                // when
                val email = jwtService.extractEmail("invalid.token.here")

                // then
                email shouldBe null
            }
        }
    }

    describe("isTokenValid") {
        context("유효한 토큰과 일치하는 사용자로 검증하면") {
            it("true를 반환한다") {
                // given
                val token = jwtService.generateAccessToken(testUserDetails)

                // when
                val isValid = jwtService.isTokenValid(token, testUserDetails)

                // then
                isValid shouldBe true
            }
        }

        context("유효한 토큰과 다른 사용자로 검증하면") {
            it("false를 반환한다") {
                // given
                val token = jwtService.generateAccessToken(testUserDetails)
                val differentUser = User.builder()
                    .username("different@example.com")
                    .password("password")
                    .authorities(SimpleGrantedAuthority("ROLE_USER"))
                    .build()

                // when
                val isValid = jwtService.isTokenValid(token, differentUser)

                // then
                isValid shouldBe false
            }
        }
    }

    describe("isTokenExpired") {
        context("새로 생성된 토큰은") {
            it("만료되지 않았다") {
                // given
                val token = jwtService.generateAccessToken(testUserDetails)

                // when
                val isExpired = jwtService.isTokenExpired(token)

                // then
                isExpired shouldBe false
            }
        }
    }

    describe("getAccessTokenExpirationSeconds") {
        it("설정된 만료 시간을 초 단위로 반환한다") {
            // when
            val expirationSeconds = jwtService.getAccessTokenExpirationSeconds()

            // then
            expirationSeconds shouldBe 900L  // 15분 = 900초
        }
    }
})
