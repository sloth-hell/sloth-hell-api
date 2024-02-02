package me.lemphis.slothhell.user.web

import me.lemphis.slothhell.user.application.AccessTokenRequest
import me.lemphis.slothhell.user.application.OAuth2UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
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
		@AuthenticationPrincipal userId: String,
	) {
		oauth2UserService.publishAccessToken(userId, request)
	}

	@PostMapping("/logout")
	fun logout(
		@AuthenticationPrincipal userId: String,
	): ResponseEntity<Void> {
		oauth2UserService.logout(userId)
		return ResponseEntity.noContent().build()
	}

}
