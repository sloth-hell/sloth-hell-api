package com.slothhell.api.member.application

data class TokenResponse(
	val accessToken: String,
	val refreshToken: String,
)
