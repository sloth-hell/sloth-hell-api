package me.lemphis.slothhell.global.web

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Configuration
@EnableWebMvc
@Profile("!prod")
class WebMvcConfig : WebMvcConfigurer {

	override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
		registry
			.addResourceHandler("/docs/**")
			.addResourceLocations("classpath:/static/docs/")
	}

}
