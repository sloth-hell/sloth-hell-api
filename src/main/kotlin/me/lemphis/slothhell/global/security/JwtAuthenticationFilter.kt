package me.lemphis.slothhell.global.security

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import me.lemphis.slothhell.global.dto.ErrorResponse
import me.lemphis.slothhell.global.security.SecurityConfig.Companion.ALLOWED_URI_PATTERNS
import me.lemphis.slothhell.global.security.SecurityConfig.Companion.DENIED_URI_PATTERNS
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.util.AntPathMatcher
import org.springframework.util.PathMatcher
import org.springframework.web.filter.OncePerRequestFilter
import java.nio.charset.StandardCharsets

@Component
class JwtAuthenticationFilter(
	private val jwtAuthenticationProvider: JwtAuthenticationProvider,
	private val objectMapper: ObjectMapper,
) : OncePerRequestFilter() {

	companion object {
		const val BEARER_TOKEN_PREFIX = "Bearer "
	}

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
		}

		val token = extractBearerToken(request)
		if (token == null) {
			sendUnauthorizedResponse(response)
		}
		// TODO: JWT 토큰 검증 후 정상 토큰이면 다음 filterchain으로 pass, 비정상 토큰일 경우 case별 예외 처리
	}

	private fun isDeniedUri(uri: String) = DENIED_URI_PATTERNS.any { pathMatcher.match(it, uri) }

	private fun sendForbiddenResponse(deniedUri: String, response: HttpServletResponse) {
		val errorResponse = ErrorResponse(
			null,
			null,
			"Access to the requested resource '$deniedUri' is not allowed.",
		)
		response.apply {
			status = HttpServletResponse.SC_FORBIDDEN
			contentType = MediaType.APPLICATION_JSON_VALUE
			characterEncoding = StandardCharsets.UTF_8.name()
			writer.write(objectMapper.writeValueAsString(errorResponse))
			writer.flush()
			writer.close()
		}
	}

	private fun isAllowedUri(uri: String) = ALLOWED_URI_PATTERNS.any { pathMatcher.match(it, uri) }

	private fun extractBearerToken(request: HttpServletRequest): String? {
		val authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION)
		if (hasBearerToken(authorizationHeader)) {
			return authorizationHeader.substring(BEARER_TOKEN_PREFIX.length)
		}
		return null
	}

	private fun hasBearerToken(authorizationHeader: String?) =
		authorizationHeader != null && authorizationHeader.startsWith(BEARER_TOKEN_PREFIX)

	private fun sendUnauthorizedResponse(response: HttpServletResponse) {
		val errorResponse = ErrorResponse(
			null,
			null,
			"Authentication is required to access this resource. Please include a valid Bearer token in the Authorization header.",
		)
		response.apply {
			status = HttpServletResponse.SC_UNAUTHORIZED
			contentType = MediaType.APPLICATION_JSON_VALUE
			characterEncoding = StandardCharsets.UTF_8.name()
			writer.write(objectMapper.writeValueAsString(errorResponse))
			writer.flush()
			writer.close()
		}
	}

}
