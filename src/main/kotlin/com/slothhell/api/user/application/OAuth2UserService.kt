package com.slothhell.api.user.application

import com.slothhell.api.config.security.JwtAuthenticationProvider
import com.slothhell.api.user.domain.OAuth2Provider
import com.slothhell.api.user.domain.User
import com.slothhell.api.user.domain.UserId
import com.slothhell.api.user.domain.UserRepository
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class OAuth2UserService(
	private val userRepository: UserRepository,
	private val jwtAuthenticationProvider: JwtAuthenticationProvider,
) : DefaultOAuth2UserService() {

	@Transactional
	override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
		val oauth2User = super.loadUser(userRequest)
		val oauth2Provider = OAuth2Provider.valueOf(userRequest.clientRegistration.registrationId.uppercase())
		val oauth2Attribute = oauth2Provider.getOAuth2Attribute(oauth2User)
		val user = User(
			userId = UserId(oauth2Attribute.id),
			email = oauth2Attribute.email,
			profileUrl = oauth2Attribute.profileUrl,
			provider = oauth2Provider,
		)
		userRepository.save(user)
		return oauth2User
	}

	@Transactional
	fun publishAccessToken(userId: String, request: AccessTokenRequest) {
		jwtAuthenticationProvider.isTokenValid(request.refreshToken)
		val user = userRepository.findById(UserId(userId)).get()
		user.compareRefreshTokenWithSession(request.refreshToken)
		val newRefreshToken = jwtAuthenticationProvider.generateRefreshToken(user.userId.id)
		user.updateRefreshTokenExpiration(newRefreshToken)
	}

	@Transactional
	fun logout(userId: String) {
		val user = userRepository.findById(UserId(userId)).get()
		user.logout()
	}

}
