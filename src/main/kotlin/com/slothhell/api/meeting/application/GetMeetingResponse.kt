package com.slothhell.api.meeting.application

import com.slothhell.api.meeting.domain.ConversationType
import com.slothhell.api.member.domain.Gender
import java.time.LocalDateTime

data class GetMeetingResponse(
	val meetingId: Long,
	val creatorMemberId: Long,
	val creatorMemberNickname: String,
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
