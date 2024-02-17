package com.slothhell.api.config.security

import com.slothhell.api.config.property.JwtProperties
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SignatureException
import org.springframework.context.annotation.Configuration
import java.util.Date

@Configuration
class JwtAuthenticationProvider(
	private val jwtProperties: JwtProperties,
) {

	private val secretKey = Keys.hmacShaKeyFor(jwtProperties.secretKey.toByteArray())
	private val jwtParser = Jwts.parser().verifyWith(secretKey).build()

	fun extractSubject(token: String): String {
		return extractClaim(token) { it.subject }
	}

	private fun <T> extractClaim(token: String, claimsResolver: (Claims) -> T): T {
		val claims = extractAllClaims(token)
		return claimsResolver(claims)
	}

	private fun extractAllClaims(token: String): Claims {
		return jwtParser
			.parseSignedClaims(token)
			.payload
	}

	fun generateAccessToken(subject: Long): String {
		return buildToken(subject.toString(), jwtProperties.expiration.accessToken)
	}

	fun generateRefreshToken(subject: Long): String {
		return buildToken(subject.toString(), jwtProperties.expiration.refreshToken)
	}

	private fun buildToken(subject: String, expiration: Long): String {
		return Jwts.builder()
			.subject(subject)
			.issuedAt(Date())
			.expiration(Date(System.currentTimeMillis() + (expiration * 1000)))
			.signWith(secretKey)
			.compact()
	}

	@Throws(
		ExpiredJwtException::class,
		MalformedJwtException::class,
		SignatureException::class,
	)
	fun isTokenValid(token: String): Boolean {
		return !isTokenExpired(token)
	}

	private fun isTokenExpired(token: String): Boolean {
		return extractExpiration(token).before(Date())
	}

	private fun extractExpiration(token: String): Date {
		return extractClaim(token) { it.expiration }
	}

}
