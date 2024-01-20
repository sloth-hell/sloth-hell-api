package me.lemphis.slothhell.domain.user.web

import me.lemphis.slothhell.config.extension.extractOAuth2UserName
import me.lemphis.slothhell.domain.user.application.AccessTokenRequest
import me.lemphis.slothhell.domain.user.application.OAuth2UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/user")
class UserController(
	private val oauth2UserService: OAuth2UserService,
) {

	@PostMapping("/access-token")
	fun publishAccessToken(
		@RequestBody request: AccessTokenRequest,
		authentication: Authentication,
	) {
		oauth2UserService.publishAccessToken(authentication.extractOAuth2UserName(), request)
	}

	@PostMapping("/logout")
	fun logout(authentication: Authentication): ResponseEntity<Void> {
		val userName = authentication.extractOAuth2UserName()
		oauth2UserService.logout(userName)
		return ResponseEntity.noContent().build()
	}

}
