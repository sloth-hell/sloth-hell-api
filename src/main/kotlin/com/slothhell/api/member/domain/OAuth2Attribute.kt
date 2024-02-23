package com.slothhell.api.member.domain

data class OAuth2Attribute(
	val subject: String,
	val email: String,
	val profileUrl: String,
)
