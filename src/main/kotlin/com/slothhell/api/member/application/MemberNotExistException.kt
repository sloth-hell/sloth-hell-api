package com.slothhell.api.member.application

import com.slothhell.api.config.exception.ApplicationRuntimeException

class MemberNotExistException(receivedValue: Long) :
	ApplicationRuntimeException(
		errorField = "memberId",
		receivedValue = receivedValue,
		message = "memberId: ${receivedValue}에 해당하는 회원이 존재하지 않습니다.",
	)
