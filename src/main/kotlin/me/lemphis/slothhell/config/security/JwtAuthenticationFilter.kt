package me.lemphis.slothhell.config.security

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import me.lemphis.slothhell.config.dto.ErrorResponse
import me.lemphis.slothhell.config.extension.createForbiddenResponse
import me.lemphis.slothhell.config.extension.createUnauthorizedResponse
import me.lemphis.slothhell.config.security.SecurityConfig.Companion.ALLOWED_URI_PATTERNS
import me.lemphis.slothhell.config.security.SecurityConfig.Companion.DENIED_URI_PATTERNS
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.util.AntPathMatcher
import org.springframework.util.PathMatcher
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
	private val jwtAuthenticationProvider: JwtAuthenticationProvider,
	private val objectMapper: ObjectMapper,
) : OncePerRequestFilter() {

	private val bearerTokenPrefix = "Bearer "
	private val pathMatcher: PathMatcher = AntPathMatcher()

	override fun doFilterInternal(
		request: HttpServletRequest,
		response: HttpServletResponse,
		filterChain: FilterChain,
	) {
		val uri = request.requestURI
		if (isDeniedUri(uri)) {
			sendForbiddenResponse(uri, response)
			return
		}
		if (isAllowedUri(uri)) {
			filterChain.doFilter(request, response)
			return
		}
		val token = extractBearerToken(request)
		if (token == null) {
			sendUnauthorizedResponse(response, "Authentication required. Please provide a valid token.")
			return
		}

		runCatching {
			jwtAuthenticationProvider.isTokenValid(token)
		}.getOrElse {
			sendUnauthorizedResponse(response, "JWT token has expired or invalid.")
		}
		setAuthenticationToSecurityContext(token)

		filterChain.doFilter(request, response)
	}

	private fun isDeniedUri(uri: String) = DENIED_URI_PATTERNS.any { pathMatcher.match(it, uri) }

	private fun sendForbiddenResponse(deniedUri: String, response: HttpServletResponse) {
		val errorResponse = ErrorResponse(message = "Access to the requested resource '$deniedUri' is not allowed.")
		response.createForbiddenResponse(objectMapper.writeValueAsString(errorResponse))
	}

	private fun isAllowedUri(uri: String) = ALLOWED_URI_PATTERNS.any { pathMatcher.match(it, uri) }

	private fun extractBearerToken(request: HttpServletRequest): String? {
		val authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION)
		if (hasBearerToken(authorizationHeader)) {
			return authorizationHeader.substring(bearerTokenPrefix.length)
		}
		return null
	}

	private fun hasBearerToken(authorizationHeader: String?) =
		authorizationHeader != null && authorizationHeader.startsWith(bearerTokenPrefix)

	private fun sendUnauthorizedResponse(response: HttpServletResponse, message: String) {
		val errorResponse = ErrorResponse(message = message)
		response.createUnauthorizedResponse(objectMapper.writeValueAsString(errorResponse))
	}

	private fun setAuthenticationToSecurityContext(token: String) {
		val userId = jwtAuthenticationProvider.extractUsername(token)
		val authentication = UsernamePasswordAuthenticationToken(userId, "", setOf(SimpleGrantedAuthority("USER")))
		SecurityContextHolder.getContext().authentication = authentication
	}

}
