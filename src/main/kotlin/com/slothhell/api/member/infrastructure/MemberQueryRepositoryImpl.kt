package com.slothhell.api.member.infrastructure

import com.linecorp.kotlinjdsl.querydsl.expression.col
import com.linecorp.kotlinjdsl.spring.data.SpringDataQueryFactory
import com.slothhell.api.member.application.GetMemberResponse
import com.slothhell.api.member.domain.Member
import com.slothhell.api.member.domain.MemberQueryRepository
import jakarta.persistence.NoResultException
import org.springframework.stereotype.Repository

@Repository
class MemberQueryRepositoryImpl(
	private val queryFactory: SpringDataQueryFactory,
) : MemberQueryRepository {

	override fun findMemberById(memberId: Long): GetMemberResponse? {
		val query = queryFactory.selectQuery(GetMemberResponse::class.java) {
			selectMulti(
				col(Member::memberId),
				col(Member::email),
				col(Member::profileUrl),
				col(Member::nickname),
				col(Member::provider),
				col(Member::birthday),
				col(Member::gender),
				col(Member::isPushNotificationEnabled),
				col(Member::createdAt),
			)
			from(Member::class)
			whereAnd(
				col(Member::memberId).equal(memberId),
				col(Member::isActive).equal(true),
			)
		}
		return try {
			query.singleResult
		} catch (ignored: NoResultException) {
			null
		}
	}

}
