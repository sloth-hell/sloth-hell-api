package me.lemphis.slothhell.global.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtParser
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Configuration
import org.springframework.security.core.userdetails.UserDetails
import java.util.Date
import javax.crypto.SecretKey

@Configuration
class JwtAuthenticationProvider(
	private val jwtProperties: JwtProperties,
) {

//	private val jwtParser = Jwts.parser()
//		.verifyWith(getSignInKey())
//		.build()

//	private lateinit var jwtParser: JwtParser
//
//	@PostConstruct
//	fun initJwtParser() {
//		jwtParser = Jwts.parser()
//			.verifyWith(getSignInKey())
//			.build()
//	}

	fun extractUsername(token: String): String {
		return extractClaim(token) { it.subject }
	}

	private fun <T> extractClaim(token: String, claimsResolver: (Claims) -> T): T {
		val claims = extractAllClaims(token)
		return claimsResolver(claims)
	}

	private fun extractAllClaims(token: String): Claims {
		return Jwts.parser()
			.verifyWith(getSignInKey())
			.build()
			.parseSignedClaims(token)
			.payload
	}

	private fun getSignInKey(): SecretKey {
		return Keys.hmacShaKeyFor(jwtProperties.secretKey.toByteArray())
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
			.signWith(getSignInKey())
			.compact()
	}

	fun isTokenValid(token: String, userDetails: UserDetails): Boolean {
		val username = extractUsername(token)
		return (username == userDetails.username) && !isTokenExpired(token)
	}

	private fun isTokenExpired(token: String): Boolean {
		return extractExpiration(token).before(Date())
	}

	private fun extractExpiration(token: String): Date {
		return extractClaim(token) { it.expiration }
	}

}
