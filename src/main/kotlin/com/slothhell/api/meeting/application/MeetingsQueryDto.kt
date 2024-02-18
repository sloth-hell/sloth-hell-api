package com.slothhell.api.meeting.application

import com.slothhell.api.meeting.domain.ConversationType
import com.slothhell.api.user.domain.Gender
import java.time.LocalDateTime

data class MeetingsQueryDto(
	val meetingId: Long,
	val title: String,
	val location: String,
	val startedAt: LocalDateTime,
	val description: String? = null,
	val allowedGender: Gender? = null,
	val minAge: Byte? = null,
	val maxAge: Byte? = null,
	val conversationType: ConversationType,
	val memberCount: Long,
)
