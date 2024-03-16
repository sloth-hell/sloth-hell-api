package com.slothhell.api.config.extension

import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType

fun HttpServletResponse.createUnauthorizedResponse(responseBody: String) {
	status = HttpServletResponse.SC_UNAUTHORIZED
	setResponseBody(responseBody)
}

private fun HttpServletResponse.setResponseBody(responseBody: String) {
	contentType = MediaType.APPLICATION_JSON_VALUE
	writer.write(responseBody)
}

fun HttpServletResponse.createAuthenticationRedirectResponse(redirectUrl: String) {
	status = HttpServletResponse.SC_MOVED_TEMPORARILY
	setHeader(HttpHeaders.LOCATION, redirectUrl)
}
