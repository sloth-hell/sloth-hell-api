package com.slothhell.api.config.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.slothhell.api.config.dto.ErrorResponse
import com.slothhell.api.config.extension.createUnauthorizedResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component

@Component
class UnauthorizedAuthenticationEntryPoint(
	private val objectMapper: ObjectMapper,
) : AuthenticationEntryPoint {

	override fun commence(
		request: HttpServletRequest,
		response: HttpServletResponse,
		authException: AuthenticationException,
	) {
		sendUnauthorizedResponse(response)
	}

	fun sendUnauthorizedResponse(response: HttpServletResponse) {
		val errorResponse = ErrorResponse(message = "Authentication required. Please provide a valid token.")
		response.createUnauthorizedResponse(objectMapper.writeValueAsString(errorResponse))
	}

}
