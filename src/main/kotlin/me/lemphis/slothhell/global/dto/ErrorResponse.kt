package me.lemphis.slothhell.global.dto

data class ErrorResponse(
	val errorField: String? = null,
	val receivedValue: Any? = null,
	val message: String,
)
