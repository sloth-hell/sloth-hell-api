package com.slothhell.api.member.application

import com.slothhell.api.member.domain.MemberQueryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class MemberService(
	private val memberQueryRepository: MemberQueryRepository,
) {
	fun getMember(memberId: Long): GetMemberResponse {
		return memberQueryRepository.findMemberById(memberId) ?: throw MemberNotExistException(
			memberId,
			"memberId: ${memberId}에 해당하는 회원이 존재하지 않거나 탈퇴한 회원입니다.",
		)
	}
}
