package com.slothhell.api.meeting.infrastructure

import com.linecorp.kotlinjdsl.querydsl.expression.col
import com.linecorp.kotlinjdsl.spring.data.SpringDataQueryFactory
import com.linecorp.kotlinjdsl.spring.data.listQuery
import com.linecorp.kotlinjdsl.spring.data.singleQuery
import com.slothhell.api.meeting.application.GetMeetingResponse
import com.slothhell.api.meeting.application.GetMeetingsResponse
import com.slothhell.api.meeting.application.MeetingMasterMember
import com.slothhell.api.meeting.domain.Meeting
import com.slothhell.api.meeting.domain.MeetingQueryRepository
import com.slothhell.api.member.domain.Member
import com.slothhell.api.participant.domain.Participant
import jakarta.persistence.NoResultException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class MeetingQueryRepositoryImpl(
	private val queryFactory: SpringDataQueryFactory,
) : MeetingQueryRepository {

	override fun findMeetingsWithParticipantCount(
		dateTime: LocalDateTime,
		pageable: Pageable,
	): Page<GetMeetingsResponse> {
		val meetings = queryFactory.listQuery<GetMeetingsResponse> {
			selectMulti(
				col(Meeting::meetingId),
				col(Meeting::title),
				col(Meeting::location),
				col(Meeting::startedAt),
				col(Meeting::description),
				col(Meeting::allowedGender),
				col(Meeting::minAge),
				col(Meeting::maxAge),
				col(Meeting::conversationType),
				count(col(Participant::participantId)),
			)
			from(Meeting::class)
			join(Participant::class, on { col(Meeting::meetingId).equal(col(Participant::meetingId)) })
			whereAnd(
				col(Meeting::isActive).equal(true),
				col(Meeting::startedAt).greaterThan(dateTime),
			)
			groupBy(col(Meeting::meetingId))
			limit(pageable.offset.toInt(), pageable.pageSize)
		}
		val totalCount = findMeetingTotalCount(dateTime)
		return PageImpl(meetings, pageable, totalCount)
	}

	private fun findMeetingTotalCount(dateTime: LocalDateTime): Long {
		return queryFactory.singleQuery<Long> {
			select(count(col(Meeting::meetingId)))
			from(Meeting::class)
			whereAnd(
				col(Meeting::isActive).equal(true),
				col(Meeting::startedAt).greaterThan(dateTime),
			)
		}
	}

	override fun findMeetingAndCreatorUserById(meetingId: Long): GetMeetingResponse? {
		val meetingResponse = queryFactory.singleQuery<GetMeetingResponse> {
			selectMulti(
				col(Meeting::meetingId),
				col(Meeting::title),
				col(Meeting::location),
				col(Meeting::startedAt),
				col(Meeting::kakaoChatUrl),
				col(Meeting::description),
				col(Meeting::allowedGender),
				col(Meeting::minAge),
				col(Meeting::maxAge),
				col(Meeting::conversationType),
				col(Meeting::createdAt),
			)
			from(Meeting::class)
			whereAnd(
				col(Meeting::meetingId).equal(meetingId),
				col(Meeting::isActive).equal(true),
			)
		}

		return try {
			meetingResponse
		} catch (ignored: NoResultException) {
			null
		}
	}

	override fun findMasterUserByMeetingId(meetingId: Long): List<MeetingMasterMember> {
		return queryFactory.listQuery<MeetingMasterMember> {
			selectMulti(
				col(Member::memberId),
				col(Member::nickname),
			)
			from(Participant::class)
			join(Member::class, on { col(Member::memberId).equal(col(Participant::memberId)) })
			whereAnd(
				col(Participant::isActive).equal(true),
				col(Participant::isMaster).equal(true),
				col(Member::isActive).equal(true),
			)
		}
	}

}
