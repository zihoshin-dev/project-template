package com.template.common.exception

/**
 * 비즈니스 로직 예외
 * - 예상된 비즈니스 규칙 위반을 나타냄
 */
class BusinessException(
    val errorCode: ErrorCode,
    override val message: String = errorCode.message
) : RuntimeException(message)
