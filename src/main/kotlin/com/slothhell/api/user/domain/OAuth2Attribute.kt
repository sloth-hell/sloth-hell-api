package com.slothhell.api.user.domain

data class OAuth2Attribute(
	val subject: String,
	val email: String,
	val profileUrl: String,
)
