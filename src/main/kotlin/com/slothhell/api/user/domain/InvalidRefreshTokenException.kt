package com.slothhell.api.user.domain

import com.slothhell.api.config.exception.ApplicationRuntimeException

class InvalidRefreshTokenException(message: String) : ApplicationRuntimeException(message = message)
