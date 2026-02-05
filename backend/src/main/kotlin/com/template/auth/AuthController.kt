package com.template.auth

import com.template.auth.dto.LoginRequest
import com.template.auth.dto.RefreshTokenRequest
import com.template.auth.dto.SignupRequest
import com.template.auth.dto.TokenResponse
import com.template.common.ApiResponse
import com.template.common.exception.BusinessException
import com.template.common.exception.ErrorCode
import com.template.domain.user.CreateUserRequest
import com.template.domain.user.UserResponse
import com.template.domain.user.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.web.bind.annotation.*

/**
 * 인증 관련 API 컨트롤러
 */
@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authenticationManager: AuthenticationManager,
    private val userDetailsService: UserDetailsService,
    private val jwtService: JwtService,
    private val userService: UserService
) {
    /**
     * 회원가입
     */
    @PostMapping("/signup")
    fun signup(
        @Valid @RequestBody request: SignupRequest
    ): ResponseEntity<ApiResponse<UserResponse>> {
        val user = userService.createUser(
            CreateUserRequest(
                email = request.email,
                password = request.password,
                name = request.name
            )
        )
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success(user, "회원가입이 완료되었습니다"))
    }

    /**
     * 로그인
     */
    @PostMapping("/login")
    fun login(
        @Valid @RequestBody request: LoginRequest
    ): ResponseEntity<ApiResponse<TokenResponse>> {
        try {
            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(request.email, request.password)
            )
        } catch (e: BadCredentialsException) {
            throw BusinessException(ErrorCode.INVALID_CREDENTIALS)
        }

        val userDetails = userDetailsService.loadUserByUsername(request.email)
        val accessToken = jwtService.generateAccessToken(userDetails)
        val refreshToken = jwtService.generateRefreshToken(userDetails)

        val tokenResponse = TokenResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            expiresIn = jwtService.getAccessTokenExpirationSeconds()
        )

        return ResponseEntity.ok(ApiResponse.success(tokenResponse))
    }

    /**
     * 토큰 갱신
     */
    @PostMapping("/refresh")
    fun refreshToken(
        @Valid @RequestBody request: RefreshTokenRequest
    ): ResponseEntity<ApiResponse<TokenResponse>> {
        val email = jwtService.extractEmail(request.refreshToken)
            ?: throw BusinessException(ErrorCode.INVALID_TOKEN)

        val userDetails = userDetailsService.loadUserByUsername(email)

        if (!jwtService.isTokenValid(request.refreshToken, userDetails)) {
            throw BusinessException(ErrorCode.INVALID_TOKEN)
        }

        val accessToken = jwtService.generateAccessToken(userDetails)
        val refreshToken = jwtService.generateRefreshToken(userDetails)

        val tokenResponse = TokenResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            expiresIn = jwtService.getAccessTokenExpirationSeconds()
        )

        return ResponseEntity.ok(ApiResponse.success(tokenResponse))
    }
}
