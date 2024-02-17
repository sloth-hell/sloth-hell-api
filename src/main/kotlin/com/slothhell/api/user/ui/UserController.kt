package com.slothhell.api.user.ui

import com.slothhell.api.user.application.AccessTokenRequest
import com.slothhell.api.user.application.OAuth2UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.User
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
		@AuthenticationPrincipal user: User,
	) {
		oauth2UserService.publishAccessToken(user.username.toLong(), request)
	}

	@PostMapping("/logout")
	fun logout(
		@AuthenticationPrincipal user: User,
	): ResponseEntity<Void> {
		oauth2UserService.logout(user.username.toLong())
		return ResponseEntity.noContent().build()
	}

}
