package com.template.domain.user

import com.template.common.ApiResponse
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import java.util.UUID

/**
 * 사용자 API 컨트롤러
 */
@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {
    /**
     * 현재 로그인한 사용자 정보 조회
     */
    @GetMapping("/me")
    fun getCurrentUser(
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<ApiResponse<UserResponse>> {
        val user = userService.getUserByEmail(userDetails.username)
        return ResponseEntity.ok(ApiResponse.success(user))
    }

    /**
     * 사용자 정보 조회 (관리자 전용)
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun getUserById(
        @PathVariable id: UUID
    ): ResponseEntity<ApiResponse<UserResponse>> {
        val user = userService.getUserById(id)
        return ResponseEntity.ok(ApiResponse.success(user))
    }

    /**
     * 전체 사용자 목록 조회 (관리자 전용)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun getAllUsers(): ResponseEntity<ApiResponse<List<UserResponse>>> {
        val users = userService.getAllUsers()
        return ResponseEntity.ok(ApiResponse.success(users))
    }

    /**
     * 사용자 정보 수정
     */
    @PatchMapping("/me")
    fun updateCurrentUser(
        @AuthenticationPrincipal userDetails: UserDetails,
        @Valid @RequestBody request: UpdateUserApiRequest
    ): ResponseEntity<ApiResponse<UserResponse>> {
        val currentUser = userService.getUserByEmail(userDetails.username)
        val updatedUser = userService.updateUser(
            currentUser.id,
            UpdateUserRequest(name = request.name)
        )
        return ResponseEntity.ok(ApiResponse.success(updatedUser))
    }

    /**
     * 사용자 삭제 (관리자 전용)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun deleteUser(
        @PathVariable id: UUID
    ): ResponseEntity<ApiResponse<Unit>> {
        userService.deleteUser(id)
        return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .body(ApiResponse.success(Unit))
    }
}

// API 요청 DTO (Validation 포함)
data class UpdateUserApiRequest(
    @field:Size(min = 2, max = 50, message = "이름은 2자 이상 50자 이하여야 합니다")
    val name: String? = null
)
