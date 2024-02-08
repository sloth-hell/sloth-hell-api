package com.slothhell.api.meeting.infrastructure

import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository

@Repository
class MeetingQueryRepositoryImpl(
	private val em: EntityManager,
) {

	fun findMeetingsWithMasterUserNickname() {
		em.createQuery(
			"""
			select new com.slothhell.api.meeting.application.MeetingsQueryDto(
				m.meetingId,
				m.title,
				m.location,
				m.startedAt,
				m.description,
				m.allowedGender,
				m.minAge,
				m.maxAge,
				m.conversationType,
				u.nickname
			)
			from Meeting m
			inner join User u on m.meetingId =
			inner join user_meeting um ON m.meeting_id = um.meeting_id
					 INNER JOIN user u ON um.user_id = u.user_id
			WHERE m.activated = 1
			  AND u.activated = 1
			  AND um.is = 1
			  AND m.startedAt > NOW()
		""".trimIndent(),
		)
	}

}
