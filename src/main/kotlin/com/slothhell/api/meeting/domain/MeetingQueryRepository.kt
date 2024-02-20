package com.slothhell.api.meeting.domain

import com.slothhell.api.meeting.application.GetMeetingResponse
import com.slothhell.api.meeting.application.GetMeetingsResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDateTime

interface MeetingQueryRepository {
	fun findMeetingsWithCreatorUserCount(dateTime: LocalDateTime, pageable: Pageable): Page<GetMeetingsResponse>
	fun findMeetingAndCreatorUserById(meetingId: Long): GetMeetingResponse?
}
