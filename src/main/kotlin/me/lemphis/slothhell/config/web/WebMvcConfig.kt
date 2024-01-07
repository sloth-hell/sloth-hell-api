package me.lemphis.slothhell.config.web

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Component
@Profile("!prod")
class WebMvcConfig : WebMvcConfigurer {

	override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
		registry
			.addResourceHandler("/docs/**")
			.addResourceLocations("classpath:/static/docs/")
	}

}
