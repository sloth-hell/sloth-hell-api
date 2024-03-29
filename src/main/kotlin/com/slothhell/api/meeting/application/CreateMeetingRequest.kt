package com.slothhell.api.meeting.application

import com.slothhell.api.config.validation.Enum
import com.slothhell.api.meeting.domain.ConversationType
import com.slothhell.api.member.domain.Gender
import jakarta.validation.constraints.Future
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class CreateMeetingRequest(
	@field:NotBlank(message = "모임명은 null 이거나 빈 문자열일 수 없습니다.")
	@field:Size(min = 5, max = 30, message = "모임명은 5자 이상 30자 이하로 작성해야 합니다.")
	val title: String?,

	@field:NotBlank(message = "모임 위치는 null 이거나 빈 문자열일 수 없습니다.")
	@field:Size(max = 200, message = "모임 위치는 200자 이하로 작성해야 합니다.")
	val location: String?,

	@field:NotNull(message = "모임 시각은 null이 아니어야 합니다.")
	@field:Future(message = "모임 시각은 미래로 설정해야 합니다.")
	val startedAt: LocalDateTime?,

	@field:NotBlank(message = "카카오톡 오픈채팅 URL은 null 이거나 빈 문자열일 수 없습니다.")
	@field:Pattern(regexp = "^https://open.kakao.com/o/[a-zA-Z\\d]{8}$", message = "올바른 카카오톡 오픈채팅 URL 형식이 아닙니다.")
	val kakaoChatUrl: String?,

	val description: String?,

	@field:Enum(enumClass = Gender::class, message = "allowedGender 값은 MALE, FEMALE 중 하나여야 합니다.", nullable = true)
	val allowedGender: String?,

	@field:NotNull(message = "minAge 값은 null일 수 없습니다.")
	@field:Min(value = 20, message = "최소 나이는 20부터 설정 가능합니다.")
	@field:Max(value = 50, message = "최소 나이는 50까지 설정 가능합니다.")
	val minAge: Int?,

	@field:NotNull(message = "maxAge 값은 null일 수 없습니다.")
	@field:Min(value = 20, message = "최대 나이는 20부터 설정 가능합니다.")
	@field:Max(value = 50, message = "최대 나이는 50까지 설정 가능합니다.")
	val maxAge: Int?,

	@field:Enum(
		enumClass = ConversationType::class,
		message = "conversationType 값은 QUIET, LIGHT_CONVERSATION, COMFORTABLE 중 하나여야 합니다.",
		nullable = false,
	)
	val conversationType: String?,

	@field:NotNull(message = "maxParticipants 값은 null일 수 없습니다.")
	@field:Min(value = 2, message = "모임 최대 참여 인원은 2부터 설정 가능합니다.")
	val maxParticipants: Int?,
) {
	fun validateAgeRange() {
		if (this.minAge != null && this.maxAge != null && this.minAge > this.maxAge) {
			throw InvalidAgeRangeException()
		}
	}
}
