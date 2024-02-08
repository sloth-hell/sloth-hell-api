package com.slothhell.api.meeting.application

import com.slothhell.api.meeting.domain.Meeting
import com.slothhell.api.meeting.domain.MeetingRepository
import com.slothhell.api.user.domain.UserId
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

	fun createMeeting(request: CreateMeetingRequest, userId: String): Long {
		val newMeeting = Meeting(
			creatorId = UserId(userId),
			title = request.title!!,
			location = request.location!!,
			startedAt = request.startedAt!!,
			kakaoChatUrl = request.kakaoChatUrl!!,
		)
		return meetingRepository.save(newMeeting).meetingId!!
	}

}
