package com.slothhell.api.meeting.application

import com.slothhell.api.meeting.domain.ConversationType
import com.slothhell.api.user.domain.Gender
import java.time.LocalDateTime

data class GetMeetingResponse(
	val meetingId: Long,
	val creatorUserId: Long,
	val creatorUserNickname: String,
	val title: String,
	val location: String,
	val startedAt: LocalDateTime,
	val kakaoChatUrl: String,
	val description: String?,
	val allowedGender: Gender?,
	val minAge: Byte?,
	val maxAge: Byte?,
	val conversationType: ConversationType,
	val createdAt: LocalDateTime,
)
