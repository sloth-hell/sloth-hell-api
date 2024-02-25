package com.slothhell.api.member.infrastructure

import com.slothhell.api.member.application.GetMemberResponse
import com.slothhell.api.member.domain.MemberQueryRepository
import jakarta.persistence.EntityManager
import jakarta.persistence.NoResultException
import org.springframework.stereotype.Repository

@Repository
class MemberQueryRepositoryImpl(
	private val em: EntityManager,
) : MemberQueryRepository {

	override fun findMemberById(memberId: Long): GetMemberResponse? {
		val query = em.createQuery(
			"""
			select new com.slothhell.api.member.application.GetMemberResponse(
				m.memberId,
				m.email,
				m.profileUrl,
				m.nickname,
				m.provider,
				m.birthday,
				m.gender,
				m.isPushNotificationEnabled,
				m.createdAt
			)
			from Member m
			where m.memberId = :memberId
				and m.isActive = true
		""".trimIndent(),
			GetMemberResponse::class.java,
		).setParameter("memberId", memberId)

		return try {
			query.singleResult
		} catch (ignored: NoResultException) {
			null
		}
	}

}
