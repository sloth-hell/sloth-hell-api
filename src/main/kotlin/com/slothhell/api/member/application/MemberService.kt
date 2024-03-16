package com.slothhell.api.member.application

import com.slothhell.api.config.security.JwtAuthenticationProvider
import com.slothhell.api.member.domain.Member
import com.slothhell.api.member.domain.MemberQueryRepository
import com.slothhell.api.member.domain.MemberRepository
import com.slothhell.api.member.domain.OAuth2Provider
import org.springframework.dao.DataIntegrityViolationException
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
		return memberQueryRepository.findMemberById(memberId)!!
	}

	@Transactional
	fun updateMemberInfo(memberId: Long, request: UpdateMemberRequest) {
		val (nickname, pushNotificationEnabled) = request
		val member = memberRepository.findByIdWithWriteLock(memberId)!!
		member.updateInfo(nickname!!, pushNotificationEnabled!!)
		try {
			memberRepository.flush()
		} catch (e: DataIntegrityViolationException) {
			throw DuplicateNicknameException(nickname, e)
		}
	}

	@Transactional
	fun withdrawMember(memberId: Long) {
		val member = memberRepository.findById(memberId).get()
		member.withdraw()
	}

	@Transactional
	fun createTokenFromProviderAccessToken(request: CreateTokenFromProviderRequest): TokenResponse {
		val (provider, providerAccessToken, subject) = request
		val oauth2Provider = OAuth2Provider.valueOf(provider!!)
		val verified = oauth2Provider.validateProviderAccessToken(request.providerAccessToken!!)
		if (!verified) {
			throw InvalidProviderTokenException(providerAccessToken!!, "존재하지 않는 provider의 access token 입니다.")
		}
		var member = memberRepository.findBySubject(subject!!) ?: Member(
			subject = subject,
			provider = oauth2Provider,
		)
		member = memberRepository.save(member)

		val accessToken = jwtAuthenticationProvider.generateAccessToken(member.memberId!!)
		val refreshToken = jwtAuthenticationProvider.generateRefreshToken(member.memberId!!)
		member.updateRefreshToken(refreshToken)

		return TokenResponse(accessToken, refreshToken)
	}

	@Transactional
	fun publishNewToken(memberId: Long, refreshToken: String): TokenResponse {
		jwtAuthenticationProvider.isTokenValid(refreshToken)
		val member = memberRepository.findById(memberId).get()
		member.compareRefreshTokenWithSession(refreshToken)
		val newAccessToken = jwtAuthenticationProvider.generateAccessToken(member.memberId!!)
		val newRefreshToken = jwtAuthenticationProvider.generateRefreshToken(member.memberId!!)
		member.updateRefreshToken(newRefreshToken)
		return TokenResponse(newAccessToken, newRefreshToken)
	}

	@Transactional
	fun logout(memberId: Long) {
		val member = memberRepository.findById(memberId).get()
		member.logout()
	}

}
