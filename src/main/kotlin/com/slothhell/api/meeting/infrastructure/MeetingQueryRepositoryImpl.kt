package com.slothhell.api.meeting.infrastructure

import com.slothhell.api.meeting.application.MeetingsQueryDto
import com.slothhell.api.meeting.domain.MeetingQueryRepository
import jakarta.persistence.EntityManager
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
	): Page<MeetingsQueryDto> {
		val query = em.createQuery(
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
				count(p)
			)
		from Meeting m
		join m.participants p
		where m.activated = true
			and m.startedAt > :dateTime
		group by m
		""".trimIndent(),
			MeetingsQueryDto::class.java,
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

}
