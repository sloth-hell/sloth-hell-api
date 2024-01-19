package me.lemphis.slothhell.domain.meeting.application

import me.lemphis.slothhell.domain.meeting.domain.Meeting
import me.lemphis.slothhell.domain.meeting.domain.MeetingRepository
import me.lemphis.slothhell.domain.meeting.web.CreateMeetingRequest
import org.springframework.stereotype.Service

@Service
class MeetingService(
	private val meetingRepository: MeetingRepository,
) {

	fun createMeeting(request: CreateMeetingRequest): Long {
		val newMeeting = Meeting(
			title = request.title!!,
			location = request.location!!,
			startedAt = request.startedAt!!,
			kakaoChatUrl = request.kakaoChatUrl!!,
		)
		return meetingRepository.save(newMeeting).meetingId!!
	}

}
