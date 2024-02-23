package com.slothhell.api.member.domain

import com.slothhell.api.config.exception.ApplicationRuntimeException

class InvalidRefreshTokenException(message: String) : ApplicationRuntimeException(message = message)
