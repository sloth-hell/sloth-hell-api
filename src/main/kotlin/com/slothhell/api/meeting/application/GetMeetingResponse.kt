package com.slothhell.api.meeting.application

import com.slothhell.api.meeting.domain.ConversationType
import com.slothhell.api.member.domain.Gender
import java.time.LocalDateTime

data class GetMeetingResponse(
	val meetingId: Long,
	val title: String,
	val location: String,
	val startedAt: LocalDateTime,
	val kakaoChatUrl: String,
	val description: String?,
	val allowedGender: Gender?,
	val minAge: Int,
	val maxAge: Int,
	val conversationType: ConversationType,
	val maxParticipants: Int,
	val createdAt: LocalDateTime,
) {
	var masterMembers: List<MeetingMasterMember> = listOf()
}

data class MeetingMasterMember(
	val memberId: Long,
	val nickname: String,
)
