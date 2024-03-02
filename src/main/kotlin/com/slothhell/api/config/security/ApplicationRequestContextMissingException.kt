package com.slothhell.api.config.security

import com.slothhell.api.config.exception.ApplicationRuntimeException

class ApplicationRequestContextMissingException(message: String) : ApplicationRuntimeException(message = message)
