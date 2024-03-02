package com.slothhell.api.member.application

import com.slothhell.api.member.domain.Member
import com.slothhell.api.member.domain.MemberRepository
import com.slothhell.api.member.domain.OAuth2Provider
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OAuth2UserService(
	private val memberRepository: MemberRepository,
) : DefaultOAuth2UserService() {

	@Transactional
	override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
		val oauth2User = super.loadUser(userRequest)
		val oauth2Provider = OAuth2Provider.valueOf(userRequest.clientRegistration.registrationId.uppercase())
		val (subject, email, profileUrl) = oauth2Provider.getOAuth2Attribute(oauth2User)
		val member = memberRepository.findBySubject(subject) ?: Member(
			subject = subject,
			email = email,
			profileUrl = profileUrl,
			provider = oauth2Provider,
		)
		if (member.memberId != null) {
			member.updateMemberInfo(email, profileUrl)
		}
		AuthenticationRequestContextHolder.setContext(
			AuthenticationRequestContext(
				member.memberId!!,
				"http://localhost:8080",
			),
		)
		memberRepository.save(member)
		return oauth2User
	}

}
