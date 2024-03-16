package com.slothhell.api.config.aop

import com.slothhell.api.logger
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.util.StopWatch
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper
import java.nio.charset.StandardCharsets

@Component
class RequestResponseLoggingFilter : OncePerRequestFilter() {

	private val log = logger()
	private val emptyBody = "{}"

	override fun doFilterInternal(
		request: HttpServletRequest,
		response: HttpServletResponse,
		filterChain: FilterChain,
	) {
		val requestWrapper = ContentCachingRequestWrapper(request)
		val responseWrapper = ContentCachingResponseWrapper(response)

		val stopWatch = StopWatch()
		stopWatch.start()
		filterChain.doFilter(requestWrapper, responseWrapper)
		stopWatch.stop()

		log.info(
			"""
				[{} {}]
				HTTP Status Code: {}
				Time: {} sec
				Request Headers: {}
				Response Headers: {}
				Request: {}
				Response: {}
			""".trimIndent(),
			request.method,
			request.requestURI,
			responseWrapper.status,
			stopWatch.totalTimeSeconds,
			getRequestHeaders(requestWrapper),
			getResponseHeaders(responseWrapper),
			getRequestBody(requestWrapper),
			getResponseBody(responseWrapper),
		)
	}

	private fun getRequestHeaders(request: HttpServletRequest): Map<String, Any> {
		return request.headerNames.toList().associateWith { request.getHeader(it) }
	}

	private fun getResponseHeaders(response: HttpServletResponse): Map<String, Any> {
		return response.headerNames.associateWith { response.getHeader(it) }
	}

	private fun getRequestBody(request: ContentCachingRequestWrapper): String {
		return String(request.contentAsByteArray, charset(request.characterEncoding))
			.takeIf { it.isNotEmpty() } ?: emptyBody
	}

	private fun getResponseBody(response: ContentCachingResponseWrapper): String {
		if (response.contentType != MediaType.APPLICATION_JSON_VALUE) {
			response.copyBodyToResponse()
			return "<omitted>"
		}
		val responseBody = String(response.contentAsByteArray, charset(StandardCharsets.UTF_8.name()))
		response.copyBodyToResponse()
		return responseBody.takeIf { it.isNotEmpty() } ?: emptyBody
	}

}
