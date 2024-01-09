package me.lemphis.slothhell

import me.lemphis.slothhell.config.security.JwtProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@EnableConfigurationProperties(JwtProperties::class)
@SpringBootApplication
class SlothHellApplication

fun main(args: Array<String>) {
	runApplication<SlothHellApplication>(*args)
}
