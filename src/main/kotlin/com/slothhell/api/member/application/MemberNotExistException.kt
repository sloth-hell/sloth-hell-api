package com.slothhell.api.member.application

import com.slothhell.api.config.exception.ApplicationRuntimeException

class MemberNotExistException(receivedValue: Long, message: String) :
	ApplicationRuntimeException(errorField = "memberId", receivedValue = receivedValue, message = message)
