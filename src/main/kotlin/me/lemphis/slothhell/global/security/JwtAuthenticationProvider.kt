package me.lemphis.slothhell.global.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SignatureException
import org.springframework.context.annotation.Configuration
import org.springframework.security.core.userdetails.UserDetails
import java.util.Base64
import java.util.Date

@Configuration
class JwtAuthenticationProvider(
	private val jwtProperties: JwtProperties,
) {

	private val secretKey = Keys.hmacShaKeyFor(Base64.getEncoder().encode(jwtProperties.secretKey.toByteArray()))
	private val jwtParser = Jwts.parser().verifyWith(secretKey).build()

	fun extractUsername(token: String): String {
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

	fun generateAccessToken(userDetails: UserDetails): String {
		return buildToken(mutableMapOf(), userDetails, jwtProperties.expiration.accessToken)
	}

	fun generateRefreshToken(userDetails: UserDetails): String {
		return buildToken(mutableMapOf(), userDetails, jwtProperties.expiration.refreshToken)
	}

	private fun buildToken(extraClaims: Map<String, Any?>, userDetails: UserDetails, expiration: Long): String {
		return Jwts.builder()
			.claims(extraClaims)
			.subject(userDetails.username)
			.issuedAt(Date())
			.expiration(Date(System.currentTimeMillis() + expiration))
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
