package com.slothhell.api.member.application

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class CreateTokenFromProviderRequest(
	@field:NotBlank(message = "provider 값은 null 이거나 빈 문자열일 수 없습니다.")
	@field:Pattern(
		regexp = "^(GOOGLE|KAKAO|NAVER|APPLE)$",
		message = "provider 값은 GOOGLE, KAKAO, NAVER, APPLE 중 하나여야 합니다.",
	)
	val provider: String?,
	@field:NotBlank(message = "providerAccessToken 값은 null 이거나 빈 문자열일 수 없습니다.")
	val providerAccessToken: String?,
	@field:NotBlank(message = "subject 값은 null 이거나 빈 문자열일 수 없습니다.")
	val subject: String?,
)
