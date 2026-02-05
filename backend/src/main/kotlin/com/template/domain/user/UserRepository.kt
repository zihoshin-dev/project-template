package com.template.domain.user

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserRepository : JpaRepository<User, UUID> {

    /**
     * 이메일로 사용자 조회
     */
    fun findByEmail(email: String): User?

    /**
     * 이메일 존재 여부 확인
     */
    fun existsByEmail(email: String): Boolean
}
