package com.slothhell.api.member.domain

import com.slothhell.api.config.exception.ApplicationRuntimeException

class RefreshTokenNotExistException(message: String) : ApplicationRuntimeException(message = message)
