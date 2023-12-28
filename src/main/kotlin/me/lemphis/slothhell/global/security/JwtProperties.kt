package me.lemphis.slothhell.global.security

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("application.security.jwt")
data class JwtProperties(
	val secretKey: String,
	val expiration: Expiration,
) {
	data class Expiration(
		val accessToken: Long,
		val refreshToken: Long,
	)
}
