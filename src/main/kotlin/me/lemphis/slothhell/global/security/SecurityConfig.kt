package me.lemphis.slothhell.global.security

import me.lemphis.slothhell.domain.auth.CustomOAuth2UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter
import org.springframework.security.web.SecurityFilterChain

@EnableWebSecurity
@Configuration
class SecurityConfig(
	private val customOAuth2UserService: CustomOAuth2UserService,
	private val jwtAuthenticationFilter: JwtAuthenticationFilter,
	private val unauthorizedAuthenticationEntryPoint: UnauthorizedAuthenticationEntryPoint,
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
				.userInfoEndpoint { auth -> auth.userService(customOAuth2UserService) }
				.authorizationEndpoint { auth -> auth.baseUri("/login/oauth2/authorization") }
				.redirectionEndpoint { auth -> auth.baseUri("/login/oauth2/code/*") }
		}
		.exceptionHandling { it.authenticationEntryPoint(unauthorizedAuthenticationEntryPoint) }
		.addFilterAfter(jwtAuthenticationFilter, OAuth2LoginAuthenticationFilter::class.java)
		.build()

}
