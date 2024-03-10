package com.slothhell.api.config.security

import com.slothhell.api.config.aop.RequestResponseLoggingFilter
import com.slothhell.api.member.application.OAuth2UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter
import org.springframework.security.web.SecurityFilterChain

@EnableWebSecurity
@Configuration
class SecurityConfig(
	private val oauth2UserService: OAuth2UserService,
	private val jwtAuthenticationFilter: JwtAuthenticationFilter,
	private val requestResponseLoggingFilter: RequestResponseLoggingFilter,
	private val unauthorizedAuthenticationEntryPoint: UnauthorizedAuthenticationEntryPoint,
	private val serviceOAuth2AuthenticationSuccessHandler: ServiceOAuth2AuthenticationSuccessHandler,
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
				.requestMatchers(HttpMethod.GET, "/login/oauth2/**", "/docs/**", "/meetings").permitAll()
				.requestMatchers(HttpMethod.POST, "/members/token-from-provider").permitAll()
				.requestMatchers(HttpMethod.GET, "/favicon.ico").denyAll()
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
		.addFilterBefore(requestResponseLoggingFilter, OAuth2AuthorizationRequestRedirectFilter::class.java)
		.addFilterAfter(jwtAuthenticationFilter, OAuth2LoginAuthenticationFilter::class.java)
		.build()

}
