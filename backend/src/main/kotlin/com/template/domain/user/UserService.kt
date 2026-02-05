package com.template.domain.user

import com.template.common.exception.BusinessException
import com.template.common.exception.ErrorCode
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

/**
 * 사용자 관련 비즈니스 로직 서비스
 */
@Service
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    /**
     * 회원가입
     * @throws BusinessException 이미 존재하는 이메일인 경우
     */
    @Transactional
    fun createUser(request: CreateUserRequest): UserResponse {
        // 이메일 중복 검사
        if (userRepository.existsByEmail(request.email)) {
            throw BusinessException(ErrorCode.DUPLICATE_EMAIL)
        }

        val user = User(
            email = request.email,
            password = passwordEncoder.encode(request.password) ?: throw IllegalStateException("Password encoding failed"),
            name = request.name
        )

        val savedUser = userRepository.save(user)
        return UserResponse.from(savedUser)
    }

    /**
     * ID로 사용자 조회
     */
    fun getUserById(id: UUID): UserResponse {
        val user = userRepository.findById(id)
            .orElseThrow { BusinessException(ErrorCode.USER_NOT_FOUND) }
        return UserResponse.from(user)
    }

    /**
     * 이메일로 사용자 조회
     */
    fun getUserByEmail(email: String): UserResponse {
        val user = userRepository.findByEmail(email)
            ?: throw BusinessException(ErrorCode.USER_NOT_FOUND)
        return UserResponse.from(user)
    }

    /**
     * 사용자 정보 수정
     */
    @Transactional
    fun updateUser(id: UUID, request: UpdateUserRequest): UserResponse {
        val user = userRepository.findById(id)
            .orElseThrow { BusinessException(ErrorCode.USER_NOT_FOUND) }

        request.name?.let { user.name = it }

        return UserResponse.from(user)
    }

    /**
     * 사용자 삭제
     */
    @Transactional
    fun deleteUser(id: UUID) {
        if (!userRepository.existsById(id)) {
            throw BusinessException(ErrorCode.USER_NOT_FOUND)
        }
        userRepository.deleteById(id)
    }

    /**
     * 모든 사용자 조회 (관리자용)
     */
    fun getAllUsers(): List<UserResponse> {
        return userRepository.findAll().map { UserResponse.from(it) }
    }
}

// DTO 클래스들
data class CreateUserRequest(
    val email: String,
    val password: String,
    val name: String
)

data class UpdateUserRequest(
    val name: String? = null
)

data class UserResponse(
    val id: UUID,
    val email: String,
    val name: String,
    val role: UserRole,
    val createdAt: String
) {
    companion object {
        fun from(user: User): UserResponse {
            return UserResponse(
                id = user.id!!,
                email = user.email,
                name = user.name,
                role = user.role,
                createdAt = user.createdAt.toString()
            )
        }
    }
}
