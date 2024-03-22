package com.slothhell.api.config

import com.slothhell.api.config.security.JwtAuthenticationFilter
import com.slothhell.api.config.security.UnauthorizedAuthenticationEntryPoint
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter
import org.springframework.security.web.SecurityFilterChain

@TestConfiguration
@Import(UnauthorizedAuthenticationEntryPoint::class)
class TestSecurityConfig(
	private val jwtAuthenticationFilter: JwtAuthenticationFilter,
	private val unauthorizedAuthenticationEntryPoint: UnauthorizedAuthenticationEntryPoint,
) {

	@Bean
	fun securityFilterChain(httpSecurity: HttpSecurity): SecurityFilterChain = httpSecurity
		.formLogin { it.disable() }
		.httpBasic { it.disable() }
		.cors { it.disable() }
		.csrf { it.disable() }
		.headers { it.disable() }
		.rememberMe { it.disable() }
		.sessionManagement { it.disable() }
		.logout { it.disable() }
		.authorizeHttpRequests {
			it
				.requestMatchers(
					HttpMethod.GET,
					"/login/oauth2/**",
					"/docs/**",
					"/meetings",
				).permitAll()
				.requestMatchers(
					HttpMethod.POST,
					"/members/token-from-provider",
					"/members/token",
					"/members/register",
				).permitAll()
				.requestMatchers(HttpMethod.GET, "/favicon.ico").denyAll()
				.anyRequest().authenticated()
		}
		.oauth2Login { it.disable() }
		.exceptionHandling { it.authenticationEntryPoint(unauthorizedAuthenticationEntryPoint) }
		.addFilterAfter(jwtAuthenticationFilter, OAuth2LoginAuthenticationFilter::class.java)
		.build()

}
