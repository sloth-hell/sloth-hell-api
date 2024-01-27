package me.lemphis.slothhell.domain.meeting.application

import jakarta.validation.constraints.Future
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
)