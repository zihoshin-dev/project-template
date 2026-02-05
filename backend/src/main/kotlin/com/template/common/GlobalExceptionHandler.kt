package com.template.common

import com.template.common.exception.BusinessException
import com.template.common.exception.ErrorCode
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * 전역 예외 처리 핸들러
 * - 모든 컨트롤러에서 발생하는 예외를 일관된 형식으로 처리
 */
@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * 비즈니스 예외 처리
     */
    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(ex: BusinessException): ResponseEntity<ErrorResponse> {
        logger.warn("Business exception: ${ex.errorCode.code} - ${ex.message}")

        return ResponseEntity
            .status(ex.errorCode.status)
            .body(ErrorResponse.of(ex.errorCode, ex.message))
    }

    /**
     * Validation 예외 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val errors = ex.bindingResult.allErrors
            .filterIsInstance<FieldError>()
            .associate { it.field to (it.defaultMessage ?: "유효하지 않은 값") }

        val message = errors.entries.joinToString(", ") { "${it.key}: ${it.value}" }

        logger.warn("Validation error: $message")

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse.of(ErrorCode.INVALID_INPUT, message, errors))
    }

    /**
     * 인증 실패 예외 처리
     */
    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentialsException(ex: BadCredentialsException): ResponseEntity<ErrorResponse> {
        logger.warn("Authentication failed: ${ex.message}")

        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(ErrorResponse.of(ErrorCode.INVALID_CREDENTIALS))
    }

    /**
     * 접근 거부 예외 처리
     */
    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDeniedException(ex: AccessDeniedException): ResponseEntity<ErrorResponse> {
        logger.warn("Access denied: ${ex.message}")

        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(ErrorResponse.of(ErrorCode.ACCESS_DENIED))
    }

    /**
     * 기타 예외 처리
     */
    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ResponseEntity<ErrorResponse> {
        logger.error("Unexpected error occurred", ex)

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse.of(ErrorCode.INTERNAL_ERROR))
    }
}

/**
 * 에러 응답 DTO
 */
data class ErrorResponse(
    val success: Boolean = false,
    val code: String,
    val message: String,
    val errors: Map<String, String>? = null,
    val timestamp: String = java.time.LocalDateTime.now().toString()
) {
    companion object {
        fun of(errorCode: ErrorCode, message: String? = null, errors: Map<String, String>? = null): ErrorResponse {
            return ErrorResponse(
                code = errorCode.code,
                message = message ?: errorCode.message,
                errors = errors
            )
        }
    }
}
