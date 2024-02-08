package com.slothhell.api.meeting.domain

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime


interface MeetingRepository : JpaRepository<Meeting, Long> {

	fun findByStartedAtAfterOrderByMeetingIdDesc(currentDateTime: LocalDateTime, pageable: Pageable): Page<Meeting>

}
