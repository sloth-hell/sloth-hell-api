package com.slothhell.api.config.web

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Component
@Profile("!prod")
class WebMvcConfig : WebMvcConfigurer {

	override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
		registry
			.addResourceHandler("/swagger-ui/**")
			.addResourceLocations("classpath:/static/swagger-ui/")
	}

	override fun addCorsMappings(registry: CorsRegistry) {
		registry.addMapping("/**")
			.allowedOrigins("http://sloth-hell.com")
			.allowedMethods("OPTIONS", "GET", "POST", "PUT", "DELETE", "PATCH")
	}

}
