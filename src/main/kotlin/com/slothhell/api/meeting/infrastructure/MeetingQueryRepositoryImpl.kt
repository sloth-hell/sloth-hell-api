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
		where m.activated = true
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
			where m.activated = true
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
				m.meetingId,
				u.userId,
				u.nickname,
				m.title,
				m.location,
				m.startedAt,
				m.kakaoChatUrl,
				m.description,
				m.allowedGender,
				m.minAge,
				m.maxAge,
				m.conversationType,
				m.createdAt
			)
		from Meeting m
		join User u
			on m.creatorUserId = u.userId
		where m.meetingId = :meetingId
			and m.activated = true
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
