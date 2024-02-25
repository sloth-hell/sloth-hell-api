package com.slothhell.api.member.domain

import com.slothhell.api.member.application.GetMemberResponse

interface MemberQueryRepository {
	fun findMemberById(memberId: Long): GetMemberResponse?
}
