package com.template.domain.user

import com.template.common.exception.BusinessException
import com.template.common.exception.ErrorCode
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.*
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*

/**
 * UserService 단위 테스트
 */
class UserServiceTest : DescribeSpec({

    val userRepository = mockk<UserRepository>()
    val passwordEncoder = mockk<PasswordEncoder>()
    val userService = UserService(userRepository, passwordEncoder)

    beforeTest {
        clearAllMocks()
    }

    describe("createUser") {
        context("새로운 이메일로 가입하면") {
            it("사용자가 성공적으로 생성된다") {
                // given
                val request = CreateUserRequest(
                    email = "test@example.com",
                    password = "password123",
                    name = "테스트"
                )
                val encodedPassword = "encoded_password"
                val savedUser = User(
                    id = UUID.randomUUID(),
                    email = request.email,
                    password = encodedPassword,
                    name = request.name
                )

                every { userRepository.existsByEmail(request.email) } returns false
                every { passwordEncoder.encode(request.password) } returns encodedPassword
                every { userRepository.save(any()) } returns savedUser

                // when
                val result = userService.createUser(request)

                // then
                result.email shouldBe request.email
                result.name shouldBe request.name
                result.id shouldNotBe null

                verify(exactly = 1) { userRepository.existsByEmail(request.email) }
                verify(exactly = 1) { passwordEncoder.encode(request.password) }
                verify(exactly = 1) { userRepository.save(any()) }
            }
        }

        context("이미 존재하는 이메일로 가입하면") {
            it("DUPLICATE_EMAIL 예외가 발생한다") {
                // given
                val request = CreateUserRequest(
                    email = "existing@example.com",
                    password = "password123",
                    name = "테스트"
                )

                every { userRepository.existsByEmail(request.email) } returns true

                // when & then
                val exception = shouldThrow<BusinessException> {
                    userService.createUser(request)
                }
                exception.errorCode shouldBe ErrorCode.DUPLICATE_EMAIL

                verify(exactly = 1) { userRepository.existsByEmail(request.email) }
                verify(exactly = 0) { userRepository.save(any()) }
            }
        }
    }

    describe("getUserById") {
        context("존재하는 사용자 ID로 조회하면") {
            it("사용자 정보가 반환된다") {
                // given
                val userId = UUID.randomUUID()
                val user = User(
                    id = userId,
                    email = "test@example.com",
                    password = "encoded_password",
                    name = "테스트"
                )

                every { userRepository.findById(userId) } returns Optional.of(user)

                // when
                val result = userService.getUserById(userId)

                // then
                result.id shouldBe userId
                result.email shouldBe user.email
                result.name shouldBe user.name
            }
        }

        context("존재하지 않는 사용자 ID로 조회하면") {
            it("USER_NOT_FOUND 예외가 발생한다") {
                // given
                val userId = UUID.randomUUID()

                every { userRepository.findById(userId) } returns Optional.empty()

                // when & then
                val exception = shouldThrow<BusinessException> {
                    userService.getUserById(userId)
                }
                exception.errorCode shouldBe ErrorCode.USER_NOT_FOUND
            }
        }
    }

    describe("updateUser") {
        context("유효한 정보로 수정하면") {
            it("사용자 정보가 업데이트된다") {
                // given
                val userId = UUID.randomUUID()
                val user = User(
                    id = userId,
                    email = "test@example.com",
                    password = "encoded_password",
                    name = "원래이름"
                )
                val updateRequest = UpdateUserRequest(name = "새이름")

                every { userRepository.findById(userId) } returns Optional.of(user)

                // when
                val result = userService.updateUser(userId, updateRequest)

                // then
                result.name shouldBe "새이름"
            }
        }
    }

    describe("deleteUser") {
        context("존재하는 사용자를 삭제하면") {
            it("성공적으로 삭제된다") {
                // given
                val userId = UUID.randomUUID()

                every { userRepository.existsById(userId) } returns true
                every { userRepository.deleteById(userId) } just runs

                // when
                userService.deleteUser(userId)

                // then
                verify(exactly = 1) { userRepository.deleteById(userId) }
            }
        }

        context("존재하지 않는 사용자를 삭제하면") {
            it("USER_NOT_FOUND 예외가 발생한다") {
                // given
                val userId = UUID.randomUUID()

                every { userRepository.existsById(userId) } returns false

                // when & then
                val exception = shouldThrow<BusinessException> {
                    userService.deleteUser(userId)
                }
                exception.errorCode shouldBe ErrorCode.USER_NOT_FOUND
            }
        }
    }
})
