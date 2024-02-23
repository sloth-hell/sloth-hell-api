package com.slothhell.api.config.extension

import com.slothhell.api.member.domain.OAuth2Provider
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.user.OAuth2User


fun Authentication.extractOAuth2UserName(): String {
	val provider = (this as OAuth2AuthenticationToken).authorizedClientRegistrationId
	val oauth2User = principal as OAuth2User
	val oauth2Provider = OAuth2Provider.valueOf(provider.uppercase())
	val oauth2Attribute = oauth2Provider.getOAuth2Attribute(oauth2User)
	return oauth2Attribute.subject
}
