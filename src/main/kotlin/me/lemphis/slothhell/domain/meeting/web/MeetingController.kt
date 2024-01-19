package me.lemphis.slothhell.domain.meeting.web

import jakarta.validation.Valid
import me.lemphis.slothhell.domain.meeting.application.MeetingService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/meetings")
class MeetingController(
	private val meetingService: MeetingService,
) {

	@PostMapping
	fun createMeeting(@RequestBody @Valid request: CreateMeetingRequest): CreateMeetingResponse {
		val meetingId = meetingService.createMeeting(request)
		return CreateMeetingResponse(meetingId)
	}

}
