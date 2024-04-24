package com.slothhell.api.member.application

data class TokenFromProviderResponse(
	val accessToken: String,
	val refreshToken: String,
	val memberId: Long,
	val isActive: Boolean,
)
