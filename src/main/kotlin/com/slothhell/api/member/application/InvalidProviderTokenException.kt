package com.slothhell.api.member.application

import com.slothhell.api.config.exception.ApplicationRuntimeException

class InvalidProviderTokenException(receivedValue: String, message: String) :
	ApplicationRuntimeException("providerAccessToken", receivedValue, message)
