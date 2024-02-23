package com.slothhell.api.member.application

import com.slothhell.api.config.security.JwtAuthenticationProvider
import com.slothhell.api.member.domain.OAuth2Provider
import com.slothhell.api.member.domain.Member
import com.slothhell.api.member.domain.MemberRepository
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class OAuth2UserService(
	private val memberRepository: MemberRepository,
	private val jwtAuthenticationProvider: JwtAuthenticationProvider,
) : DefaultOAuth2UserService() {

	@Transactional
	override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
		val oauth2User = super.loadUser(userRequest)
		val oauth2Provider = OAuth2Provider.valueOf(userRequest.clientRegistration.registrationId.uppercase())
		val oauth2Attribute = oauth2Provider.getOAuth2Attribute(oauth2User)
		val member = Member(
			subject = oauth2Attribute.subject,
			email = oauth2Attribute.email,
			profileUrl = oauth2Attribute.profileUrl,
			provider = oauth2Provider,
		)
		memberRepository.save(member)
		return oauth2User
	}

	@Transactional
	fun publishAccessToken(memberId: Long, request: AccessTokenRequest) {
		jwtAuthenticationProvider.isTokenValid(request.refreshToken)
		val member = memberRepository.findById(memberId).get()
		member.compareRefreshTokenWithSession(request.refreshToken)
		val newRefreshToken = jwtAuthenticationProvider.generateRefreshToken(member.memberId!!)
		member.updateRefreshTokenExpiration(newRefreshToken)
	}

	@Transactional
	fun logout(memberId: Long) {
		val member = memberRepository.findById(memberId).get()
		member.logout()
	}

}
