package com.slothhell.api.meeting.application

import com.slothhell.api.meeting.domain.Meeting
import com.slothhell.api.meeting.domain.MeetingQueryRepository
import com.slothhell.api.meeting.domain.MeetingRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class MeetingService(
	private val meetingRepository: MeetingRepository,
	private val meetingQueryRepository: MeetingQueryRepository,
) {

	fun getMeetings(pageable: Pageable): Page<MeetingsQueryDto> {
		return meetingQueryRepository.findMeetingsWithCreatorUserCount(LocalDateTime.now(), pageable)
	}

	fun getMeeting(meetingId: Long): GetMeetingResponse {
		val meeting = meetingRepository.findById(meetingId)
			.orElseThrow { MeetingNotExistException("meetingId: ${meetingId}에 해당하는 모임이 존재하지 않습니다.") }
		return GetMeetingResponse.from(meeting)
	}

	fun createMeeting(request: CreateMeetingRequest, userId: Long): Long {
		val newMeeting = Meeting(
			creatorUserId = userId,
			title = request.title!!,
			location = request.location!!,
			startedAt = request.startedAt!!,
			kakaoChatUrl = request.kakaoChatUrl!!,
			description = request.description,
			allowedGender = request.allowedGender,
			minAge = request.minAge,
			maxAge = request.maxAge,
			conversationType = request.conversationType,
		)
		return meetingRepository.save(newMeeting).meetingId!!
	}

}
