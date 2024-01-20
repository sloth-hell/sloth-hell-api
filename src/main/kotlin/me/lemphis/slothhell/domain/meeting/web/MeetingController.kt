package me.lemphis.slothhell.domain.meeting.web

import jakarta.validation.Valid
import me.lemphis.slothhell.domain.meeting.application.CreateMeetingRequest
import me.lemphis.slothhell.domain.meeting.application.CreateMeetingResponse
import me.lemphis.slothhell.domain.meeting.application.GetMeetingResponse
import me.lemphis.slothhell.domain.meeting.application.MeetingService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/meetings")
class MeetingController(
	private val meetingService: MeetingService,
) {

	@GetMapping
	fun getMeetings(@PageableDefault pageable: Pageable): Page<GetMeetingResponse> {
		return meetingService.getMeetings(pageable)
	}

	@GetMapping("/{meetingId}")
	fun getMeeting(@PathVariable meetingId: Long): GetMeetingResponse {
		return meetingService.getMeeting(meetingId)
	}

	@PostMapping
	fun createMeeting(@RequestBody @Valid request: CreateMeetingRequest): ResponseEntity<CreateMeetingResponse> {
		val meetingId = meetingService.createMeeting(request)
		val newMeetingUri = URI.create("/meetings/$meetingId")
		return ResponseEntity.created(newMeetingUri).body(CreateMeetingResponse(meetingId))
	}

}
