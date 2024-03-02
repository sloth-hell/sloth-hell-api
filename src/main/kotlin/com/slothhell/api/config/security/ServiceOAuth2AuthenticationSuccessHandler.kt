package com.slothhell.api.config.security

import com.slothhell.api.config.extension.createAuthenticationRedirectResponse
import com.slothhell.api.member.application.AuthenticationRequestContextHolder
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component

@Component
class ServiceOAuth2AuthenticationSuccessHandler(
	private val jwtAuthenticationProvider: JwtAuthenticationProvider,
) : AuthenticationSuccessHandler {

	override fun onAuthenticationSuccess(
		request: HttpServletRequest,
		response: HttpServletResponse,
		authentication: Authentication,
	) {
		val (memberId, clientRedirectUri) = AuthenticationRequestContextHolder.getContext()
			?: throw ApplicationRequestContextMissingException("인가 처리 과정에서 AuthenticationRequestContext를 찾을 수 없습니다.")

		val accessToken: String = jwtAuthenticationProvider.generateAccessToken(memberId)
		val accessTokenCookie = Cookie("access_token", accessToken)

		val refreshToken: String = jwtAuthenticationProvider.generateRefreshToken(memberId)
		val refreshTokenCookie = Cookie("refresh_token", refreshToken)
		refreshTokenCookie.maxAge = 10

		response.addCookie(accessTokenCookie)
		response.addCookie(refreshTokenCookie)
		response.createAuthenticationRedirectResponse(clientRedirectUri)
	}

}
