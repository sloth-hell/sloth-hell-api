package com.slothhell.api.meeting.application

import com.slothhell.api.meeting.domain.ConversationType
import com.slothhell.api.member.domain.Gender
import java.time.LocalDateTime

data class GetMeetingsResponse(
	val meetingId: Long,
	val title: String,
	val location: String,
	val startedAt: LocalDateTime,
	val description: String? = null,
	val allowedGender: Gender? = null,
	val minAge: Int,
	val maxAge: Int,
	val conversationType: ConversationType,
	val maxParticipants: Int,
	val participantsCount: Long,
)
