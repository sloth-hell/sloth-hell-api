package com.slothhell.api.member.application

import com.slothhell.api.config.validation.Enum
import com.slothhell.api.member.domain.Gender
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate

data class RegisterMemberRequest(
	@field:NotBlank(message = "subject 값은 null 이거나 빈 문자열일 수 없습니다.")
	val subject: String?,
	@field:Enum(enumClass = Gender::class, message = "gender 값은 MALE, FEMALE 중 하나여야 합니다.")
	val gender: String?,
	@field:NotNull(message = "birthday 값은 null일 수 없습니다.")
	@field:DateTimeFormat(pattern = "yyyy-MM-dd")
	val birthday: LocalDate?,
	@field:NotBlank(message = "nickname 값은 null 이거나 빈 문자열일 수 없습니다.")
	val nickname: String?,
)
