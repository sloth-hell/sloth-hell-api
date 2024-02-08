package com.slothhell.api.meeting.application

import com.slothhell.api.meeting.domain.Meeting
import com.slothhell.api.user.domain.Gender
import java.time.LocalDateTime

data class GetMeetingResponse(
	val meetingId: Long,
	val title: String,
	val location: String,
	val startedAt: LocalDateTime,
	val kakaoChatUrl: String,
	val kakaoChatPassword: String?,
	val description: String?,
	val allowedGender: Gender?,
	val minAge: Byte?,
	val maxAge: Byte?,
) {
	companion object {
		fun from(meeting: Meeting) = GetMeetingResponse(
			meetingId = meeting.meetingId!!,
			title = meeting.title,
			location = meeting.location,
			startedAt = meeting.startedAt,
			kakaoChatUrl = meeting.kakaoChatUrl,
			kakaoChatPassword = meeting.kakaoChatPassword,
			description = meeting.description,
			allowedGender = meeting.allowedGender,
			minAge = meeting.minAge,
			maxAge = meeting.maxAge,
		)
	}
}
