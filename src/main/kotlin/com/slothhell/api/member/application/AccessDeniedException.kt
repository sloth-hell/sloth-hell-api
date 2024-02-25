package com.slothhell.api.member.application

import com.slothhell.api.config.exception.ApplicationRuntimeException

class AccessDeniedException(errorField: String, receivedValue: Any, message: String) :
	ApplicationRuntimeException(errorField, receivedValue, message)
