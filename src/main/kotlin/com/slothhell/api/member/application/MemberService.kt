package com.slothhell.api.member.application

import com.slothhell.api.config.security.JwtAuthenticationProvider
import com.slothhell.api.member.domain.MemberQueryRepository
import com.slothhell.api.member.domain.MemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MemberService(
	private val memberRepository: MemberRepository,
	private val memberQueryRepository: MemberQueryRepository,
	private val jwtAuthenticationProvider: JwtAuthenticationProvider,
) {

	@Transactional(readOnly = true)
	fun getMember(memberId: Long): GetMemberResponse {
		return memberQueryRepository.findMemberById(memberId) ?: throw MemberNotExistException(
			memberId,
			"memberId: ${memberId}에 해당하는 회원이 존재하지 않거나 탈퇴한 회원입니다.",
		)
	}

//	@Transactional
//	fun updateMemberInfo(memberId: Long) {
//
//	}

	@Transactional
	fun withdrawMember(memberId: Long) {
		val member = memberRepository.findById(memberId).get()
		member.withdraw()
	}

	@Transactional
	fun publishNewToken(memberId: Long, refreshToken: String): TokenResponse {
		jwtAuthenticationProvider.isTokenValid(refreshToken)
		val member = memberRepository.findById(memberId).get()
		member.compareRefreshTokenWithSession(refreshToken)
		val newAccessToken = jwtAuthenticationProvider.generateAccessToken(member.memberId!!)
		val newRefreshToken = jwtAuthenticationProvider.generateRefreshToken(member.memberId!!)
		member.updateRefreshTokenExpiration(newRefreshToken)
		return TokenResponse(newAccessToken, newRefreshToken)
	}

	@Transactional
	fun logout(memberId: Long) {
		val member = memberRepository.findById(memberId).get()
		member.logout()
	}

}
