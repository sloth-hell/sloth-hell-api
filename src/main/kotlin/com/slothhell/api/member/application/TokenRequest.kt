package com.slothhell.api.member.application

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class TokenRequest(
	@field:NotBlank(message = "refreshToken 값은 null 이거나 빈 문자열일 수 없습니다.")
	@field:Pattern(regexp = "^eyJhbGciOiJIUzUxMiJ9\\..*?\\..*", message = "올바른 JWT token의 형태가 아닙니다.")
	val refreshToken: String?,
)
