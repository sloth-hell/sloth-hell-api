package me.lemphis.slothhell.domain.user.application

import me.lemphis.slothhell.config.security.JwtAuthenticationProvider
import me.lemphis.slothhell.domain.user.domain.OAuth2Provider
import me.lemphis.slothhell.domain.user.domain.User
import me.lemphis.slothhell.domain.user.domain.UserRepository
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
			userId = oauth2Attribute.id,
			email = oauth2Attribute.email,
			profileUrl = oauth2Attribute.profileUrl,
			provider = oauth2Provider,
		)
		userRepository.save(user)
		return oauth2User
	}

	@Transactional
	fun publishAccessToken(userId: String, refreshToken: String) {
		jwtAuthenticationProvider.isTokenValid(refreshToken)
		val user = userRepository.findById(userId).get()
		user.compareRefreshTokenWithSession(refreshToken)
		val newRefreshToken = jwtAuthenticationProvider.generateRefreshToken(user.userId)
		user.updateRefreshTokenExpiration(newRefreshToken)
	}

	@Transactional
	fun logout(userId: String) {
		val user = userRepository.findById(userId).get()
		user.logout()
	}

}
