package com.slothhell.api.member.application

import com.slothhell.api.config.exception.ApplicationRuntimeException

class DuplicateNicknameException(
	receivedValue: String,
	cause: Throwable?,
) : ApplicationRuntimeException(
	errorField = "nickname",
	receivedValue = receivedValue,
	message = "nickname '${receivedValue}' is already in use",
	cause = cause,
)
