package com.slothhell.api.meeting.application

import com.slothhell.api.config.exception.ApplicationRuntimeException

class MeetingNotExistException(receivedValue: Long, message: String) :
	ApplicationRuntimeException(errorField = "meetingId", receivedValue = receivedValue, message = message)
