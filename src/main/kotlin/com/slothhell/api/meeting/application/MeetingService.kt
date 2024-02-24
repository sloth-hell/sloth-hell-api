package com.slothhell.api.meeting.application

import com.slothhell.api.meeting.domain.Meeting
import com.slothhell.api.meeting.domain.MeetingQueryRepository
import com.slothhell.api.meeting.domain.MeetingRepository
import com.slothhell.api.participant.domain.Participant
import com.slothhell.api.participant.domain.ParticipantRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class MeetingService(
	private val meetingRepository: MeetingRepository,
	private val participantRepository: ParticipantRepository,
	private val meetingQueryRepository: MeetingQueryRepository,
) {

	fun getMeetings(pageable: Pageable): Page<GetMeetingsResponse> {
		return meetingQueryRepository.findMeetingsWithCreatorUserCount(LocalDateTime.now(), pageable)
	}

	fun getMeeting(meetingId: Long): GetMeetingResponse {
		val meeting = meetingQueryRepository.findMeetingAndCreatorUserById(meetingId)
			?: throw MeetingNotExistException(meetingId, "meetingId: ${meetingId}에 해당하는 모임이 존재하지 않습니다.")
		val masterMembers = meetingQueryRepository.findMasterUserByMeetingId(meetingId)
		meeting.masterMembers = masterMembers
		return meeting
	}

	fun createMeeting(request: CreateMeetingRequest, memberId: Long): Long {
		val meeting = Meeting(
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
		val newMeeting = meetingRepository.save(meeting)
		val newMeetingId = newMeeting.meetingId!!
		val participant = Participant(
			memberId,
			newMeetingId,
			true,
		)
		participantRepository.save(participant)
		return newMeeting.meetingId!!
	}

}
