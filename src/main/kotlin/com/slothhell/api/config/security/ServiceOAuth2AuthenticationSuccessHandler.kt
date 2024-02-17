package com.slothhell.api.config.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.slothhell.api.config.extension.extractOAuth2UserName
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler
import org.springframework.stereotype.Component


@Component
class ServiceOAuth2AuthenticationSuccessHandler(
	private val jwtAuthenticationProvider: JwtAuthenticationProvider,
	private val objectMapper: ObjectMapper,
) : SavedRequestAwareAuthenticationSuccessHandler() {

	override fun onAuthenticationSuccess(
		request: HttpServletRequest,
		response: HttpServletResponse,
		authentication: Authentication,
	) {
		val userName = authentication.extractOAuth2UserName()

//		val accessToken: String = jwtAuthenticationProvider.generateAccessToken(userName)
//		val refreshToken: String = jwtAuthenticationProvider.generateRefreshToken(userName)
//
//		val responseBody = mapOf(
//			"accessToken" to accessToken,
//			"refreshToken" to refreshToken,
//		)
//
//		val redirectUri = UriComponentsBuilder.fromUriString("http://localhost:8080")
//			.queryParam("accessToken", accessToken)
//			.queryParam("refreshToken", refreshToken)
//			.build()
//			.toUriString()
//		response.createRedirectResponse(objectMapper.writeValueAsString(responseBody), redirectUri)
	}

}
