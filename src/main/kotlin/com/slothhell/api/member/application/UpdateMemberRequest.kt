package com.slothhell.api.member.application

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class UpdateMemberRequest(
	@field:NotBlank(message = "nickname 값은 null 이거나 빈 문자열일 수 없습니다.")
	val nickname: String?,
	@field:NotNull(message = "pushNotificationEnabled 값은 null일 수 없습니다.")
	val pushNotificationEnabled: Boolean?,
)
