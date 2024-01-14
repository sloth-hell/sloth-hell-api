package me.lemphis.slothhell

import me.lemphis.slothhell.config.security.JwtProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@EnableConfigurationProperties(JwtProperties::class)
@EnableJpaAuditing
@SpringBootApplication
class SlothHellApplication

fun main(args: Array<String>) {
	runApplication<SlothHellApplication>(*args)
}
