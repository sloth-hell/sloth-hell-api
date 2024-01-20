package me.lemphis.slothhell.domain.meeting.application

import me.lemphis.slothhell.domain.meeting.domain.Meeting
import me.lemphis.slothhell.domain.meeting.domain.MeetingRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class MeetingService(
	private val meetingRepository: MeetingRepository,
) {

	fun getMeetings(pageable: Pageable): Page<GetMeetingResponse> {
		val meetings = meetingRepository.findByStartedAtAfterOrderByMeetingIdDesc(LocalDateTime.now(), pageable)
		return meetings.map { GetMeetingResponse.from(it) }
	}

	fun getMeeting(meetingId: Long): GetMeetingResponse {
		val meeting = meetingRepository.findById(meetingId)
			.orElseThrow { MeetingNotExistException("meetingId: ${meetingId}에 해당하는 모임이 존재하지 않습니다.") }
		return GetMeetingResponse.from(meeting)
	}

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
