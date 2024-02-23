package com.slothhell.api.config.security

import com.slothhell.api.member.application.OAuth2UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter
import org.springframework.security.web.SecurityFilterChain

@EnableWebSecurity
@Configuration
class SecurityConfig(
	private val oauth2UserService: OAuth2UserService,
	private val jwtAuthenticationFilter: JwtAuthenticationFilter,
	private val unauthorizedAuthenticationEntryPoint: UnauthorizedAuthenticationEntryPoint,
	private val serviceOAuth2AuthenticationSuccessHandler: ServiceOAuth2AuthenticationSuccessHandler,
) {

	companion object {
		val ALLOWED_URI_PATTERNS = listOf("/login/oauth2/**", "/docs/**")
		val DENIED_URI_PATTERNS = listOf("/favicon.ico")
	}

	@Bean
	fun securityFilterChain(httpSecurity: HttpSecurity): SecurityFilterChain = httpSecurity
		.formLogin { it.disable() }
		.httpBasic { it.disable() }
		.cors { it.disable() }
		.csrf { it.disable() }
		.headers { it.disable() }
		.rememberMe { it.disable() }
		.sessionManagement { it.disable() }
		.authorizeHttpRequests {
			it
				.requestMatchers(*ALLOWED_URI_PATTERNS.toTypedArray()).permitAll()
				.requestMatchers(*DENIED_URI_PATTERNS.toTypedArray()).denyAll()
				.anyRequest().authenticated()
		}
		.oauth2Login {
			it
				.userInfoEndpoint { auth -> auth.userService(oauth2UserService) }
				.authorizationEndpoint { auth -> auth.baseUri("/login/oauth2/authorization") }
				.redirectionEndpoint { auth -> auth.baseUri("/login/oauth2/code/*") }
				.successHandler(serviceOAuth2AuthenticationSuccessHandler)
		}
		.exceptionHandling { it.authenticationEntryPoint(unauthorizedAuthenticationEntryPoint) }
		.addFilterAfter(jwtAuthenticationFilter, OAuth2LoginAuthenticationFilter::class.java)
		.build()

}
