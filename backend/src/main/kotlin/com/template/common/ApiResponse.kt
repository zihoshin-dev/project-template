package com.template.common

import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime

/**
 * 표준 API 응답 래퍼
 * - 일관된 응답 형식 제공
 * - 성공/실패 여부, 메시지, 데이터 포함
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApiResponse<T>(
    val success: Boolean,
    val message: String? = null,
    val data: T? = null,
    val timestamp: String = LocalDateTime.now().toString()
) {
    companion object {
        /**
         * 성공 응답 생성
         */
        fun <T> success(data: T, message: String? = null): ApiResponse<T> {
            return ApiResponse(
                success = true,
                message = message,
                data = data
            )
        }

        /**
         * 데이터 없는 성공 응답
         */
        fun success(message: String? = null): ApiResponse<Unit> {
            return ApiResponse(
                success = true,
                message = message,
                data = null
            )
        }

        /**
         * 실패 응답 생성
         */
        fun <T> error(message: String): ApiResponse<T> {
            return ApiResponse(
                success = false,
                message = message,
                data = null
            )
        }
    }
}
