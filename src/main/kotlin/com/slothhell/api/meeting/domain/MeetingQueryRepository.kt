package com.slothhell.api.meeting.domain

import com.slothhell.api.meeting.application.MeetingsQueryDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDateTime

interface MeetingQueryRepository {
	fun findMeetingsWithCreatorUserCount(dateTime: LocalDateTime, pageable: Pageable): Page<MeetingsQueryDto>
}
