package me.lemphis.slothhell.global.dto

data class ErrorResponse(
	val errorField: String,
	val receivedValue: Any?,
	val message: String?,
)
