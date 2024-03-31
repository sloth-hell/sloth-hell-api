package com.slothhell.api.member.application

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate

data class RegisterMemberRequest(
	@field:NotBlank(message = "subject 값은 null 이거나 빈 문자열일 수 없습니다.")
	val subject: String?,
	@field:NotBlank(message = "nickname 값은 null 이거나 빈 문자열일 수 없습니다.")
	val nickname: String?,
	@field:NotNull(message = "birthday 값은 null일 수 없습니다.")
	@field:DateTimeFormat(pattern = "yyyy-MM-dd")
	val birthday: LocalDate?,
)
