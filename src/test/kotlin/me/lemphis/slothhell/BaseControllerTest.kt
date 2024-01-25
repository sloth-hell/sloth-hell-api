package me.lemphis.slothhell

import com.fasterxml.jackson.databind.ObjectMapper
import me.lemphis.slothhell.config.property.JwtProperties
import me.lemphis.slothhell.config.security.JwtAuthenticationProvider
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.context.annotation.Import
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration
import org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.filter.CharacterEncodingFilter
import java.nio.charset.StandardCharsets

@ExtendWith(RestDocumentationExtension::class)
@AutoConfigureRestDocs
@Import(JwtAuthenticationProvider::class)
@EnableConfigurationProperties(JwtProperties::class)
@Disabled
abstract class BaseControllerTest {

	@Autowired
	protected lateinit var objectMapper: ObjectMapper

	@Autowired
	protected lateinit var jwtAuthenticationProvider: JwtAuthenticationProvider

	protected lateinit var mockMvc: MockMvc

	@BeforeEach
	fun setUp(webApplicationContext: WebApplicationContext, restDocumentation: RestDocumentationContextProvider) {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
			.addFilter<DefaultMockMvcBuilder>(CharacterEncodingFilter(StandardCharsets.UTF_8.name(), true))
			.apply<DefaultMockMvcBuilder>(
				documentationConfiguration(restDocumentation).operationPreprocessors()
					.withRequestDefaults(prettyPrint())
					.withResponseDefaults(prettyPrint()),
			)
			.build()
	}

}
