package com.slothhell.api.member.application

import com.slothhell.api.member.domain.Gender
import com.slothhell.api.member.domain.OAuth2Provider
import java.time.LocalDate
import java.time.LocalDateTime

data class GetMemberResponse(
	val memberId: Long,
	val nickname: String,
	val provider: OAuth2Provider,
	val birthday: LocalDate,
	val gender: Gender,
	val isPushNotificationEnabled: Boolean,
	val createdAt: LocalDateTime,
)
