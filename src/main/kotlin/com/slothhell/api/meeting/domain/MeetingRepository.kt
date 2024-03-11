package com.slothhell.api.meeting.domain

import org.springframework.data.repository.CrudRepository

interface MeetingRepository : CrudRepository<Meeting, Long>
