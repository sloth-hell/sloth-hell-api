package me.lemphis.slothhell

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SlothHellApplication

inline fun <reified T> T.logger(): Logger = LoggerFactory.getLogger(T::class.java)

fun main(args: Array<String>) {
	runApplication<SlothHellApplication>(*args)
}
