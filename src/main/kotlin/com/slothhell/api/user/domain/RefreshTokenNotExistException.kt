package com.slothhell.api.user.domain

import com.slothhell.api.config.exception.ApplicationRuntimeException

class RefreshTokenNotExistException(message: String) : ApplicationRuntimeException(message = message)
