package com.slothhell.api.config.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.slothhell.api.config.dto.ErrorResponse
import com.slothhell.api.config.extension.createUnauthorizedResponse
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
	private val jwtAuthenticationProvider: JwtAuthenticationProvider,
	private val objectMapper: ObjectMapper,
) : OncePerRequestFilter() {

	private val bearerTokenPrefix = "Bearer "

	override fun doFilterInternal(
		request: HttpServletRequest,
		response: HttpServletResponse,
		filterChain: FilterChain,
	) {
		val token = extractBearerToken(request)
		if (token != null) {
			runCatching {
				jwtAuthenticationProvider.isTokenValid(token)
			}.getOrElse {
				sendUnauthorizedResponse(response)
			}
			setAuthenticationToSecurityContext(token)
		}

		filterChain.doFilter(request, response)
	}

	private fun extractBearerToken(request: HttpServletRequest): String? {
		val authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION)
		if (hasBearerToken(authorizationHeader)) {
			return authorizationHeader.substring(bearerTokenPrefix.length)
		}
		return null
	}

	private fun hasBearerToken(authorizationHeader: String?) =
		authorizationHeader != null && authorizationHeader.startsWith(bearerTokenPrefix)

	private fun sendUnauthorizedResponse(response: HttpServletResponse) {
		val errorResponse = ErrorResponse(message = "JWT token has expired or invalid.")
		response.createUnauthorizedResponse(objectMapper.writeValueAsString(errorResponse))
	}

	private fun setAuthenticationToSecurityContext(token: String) {
		val memberId = jwtAuthenticationProvider.extractSubject(token)
		val user = User(memberId, "", setOf(SimpleGrantedAuthority("USER")))
		val authentication = UsernamePasswordAuthenticationToken(user, user.password, user.authorities)
		SecurityContextHolder.getContext().authentication = authentication
	}

}
