package me.lemphis.slothhell.domain.meeting.domain

import org.springframework.data.jpa.repository.JpaRepository

interface MeetingRepository : JpaRepository<Meeting, Long>
