package com.slothhell.api.config.exception

abstract class ApplicationRuntimeException(
	val errorField: String? = null,
	val receivedValue: Any? = null,
	message: String,
) : RuntimeException(message)
