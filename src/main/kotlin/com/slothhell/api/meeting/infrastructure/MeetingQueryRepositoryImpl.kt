package com.slothhell.api.meeting.infrastructure

import com.slothhell.api.meeting.application.GetMeetingResponse
import com.slothhell.api.meeting.application.GetMeetingsResponse
import com.slothhell.api.meeting.domain.MeetingQueryRepository
import jakarta.persistence.EntityManager
import jakarta.persistence.NoResultException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class MeetingQueryRepositoryImpl(
	private val em: EntityManager,
) : MeetingQueryRepository {

	override fun findMeetingsWithCreatorUserCount(
		dateTime: LocalDateTime,
		pageable: Pageable,
	): Page<GetMeetingsResponse> {
		val query = em.createQuery(
			"""
			select new com.slothhell.api.meeting.application.GetMeetingsResponse(
				m.meetingId,
				m.title,
				m.location,
				m.startedAt,
				m.description,
				m.allowedGender,
				m.minAge,
				m.maxAge,
				m.conversationType,
				count(p)
			)
		from Meeting m
		join m.participants p
		where m.isActive = true
			and m.startedAt > :dateTime
		group by m
		""".trimIndent(),
			GetMeetingsResponse::class.java,
		).setFirstResult(pageable.offset.toInt())
			.setMaxResults(pageable.pageSize)
			.setParameter("dateTime", dateTime)

		val totalCount = findMeetingsWithCreatorUserTotal(dateTime)
		return PageImpl(query.resultList, pageable, totalCount)
	}

	private fun findMeetingsWithCreatorUserTotal(dateTime: LocalDateTime): Long {
		val countQuery = em.createQuery(
			"""
			select count(m)
			from Meeting m
			where m.isActive = true
				and m.startedAt > :dateTime
		""".trimIndent(),
			Long::class.java,
		).setParameter("dateTime", dateTime)
		return countQuery.singleResult
	}

	override fun findMeetingAndCreatorUserById(meetingId: Long): GetMeetingResponse? {
		val query = em.createQuery(
			"""
			select new com.slothhell.api.meeting.application.GetMeetingResponse(
				meeting.meetingId,
				member.memberId,
				member.nickname,
				meeting.title,
				meeting.location,
				meeting.startedAt,
				meeting.kakaoChatUrl,
				meeting.description,
				meeting.allowedGender,
				meeting.minAge,
				meeting.maxAge,
				meeting.conversationType,
				meeting.createdAt
			)
		from Meeting meeting
		join fetch Participant participant
			on meeting.meetingId = participant.meetingId
		join fetch Member member
			on participant.memberId = member.memberId
		where meeting.meetingId = :meetingId
			and meeting.isActive = true
			and participant.isActive = true
			and participant.isMaster = true
		""".trimIndent(),
			GetMeetingResponse::class.java,
		).setParameter("meetingId", meetingId)

		return try {
			query.singleResult
		} catch (ignored: NoResultException) {
			null
		}
	}

}
