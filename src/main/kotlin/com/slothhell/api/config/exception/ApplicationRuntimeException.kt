package com.slothhell.api.config.exception

abstract class ApplicationRuntimeException(
	val errorField: String? = null,
	val receivedValue: Any? = null,
	override val message: String,
	cause: Throwable? = null,
) : RuntimeException(message, cause)
