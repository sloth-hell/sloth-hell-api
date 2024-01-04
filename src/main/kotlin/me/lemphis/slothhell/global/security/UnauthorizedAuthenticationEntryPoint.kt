package me.lemphis.slothhell.global.security

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import me.lemphis.slothhell.global.dto.ErrorResponse
import me.lemphis.slothhell.global.extension.createUnauthorizedResponse
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
