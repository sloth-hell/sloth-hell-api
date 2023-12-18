package me.lemphis.slothhell.global.security

import me.lemphis.slothhell.domain.auth.CustomOAuth2UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain

@EnableWebSecurity
@Configuration
class SecurityConfig(
	private val customOAuth2UserService: CustomOAuth2UserService,
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
		.authorizeHttpRequests {
			it.requestMatchers("/oauth2/**").permitAll()
				.anyRequest().authenticated()
		}
		.oauth2Login {
			it.userInfoEndpoint { auth -> auth.userService(customOAuth2UserService) }
		}
		.build()

}
