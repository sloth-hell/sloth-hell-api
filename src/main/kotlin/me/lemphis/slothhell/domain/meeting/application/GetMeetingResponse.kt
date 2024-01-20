package me.lemphis.slothhell.domain.meeting.application

import me.lemphis.slothhell.domain.meeting.domain.Meeting
import me.lemphis.slothhell.domain.user.domain.Gender
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
