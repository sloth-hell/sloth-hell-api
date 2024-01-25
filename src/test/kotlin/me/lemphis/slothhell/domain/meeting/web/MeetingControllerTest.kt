package me.lemphis.slothhell.domain.meeting.web

import me.lemphis.slothhell.BaseControllerTest
import me.lemphis.slothhell.domain.meeting.application.CreateMeetingRequest
import me.lemphis.slothhell.domain.meeting.application.MeetingService
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.test.web.servlet.post
import java.time.LocalDateTime

@WebMvcTest(MeetingController::class)
class MeetingControllerTest : BaseControllerTest() {

	@MockBean
	lateinit var meetingService: MeetingService

	@Test
	@DisplayName("[POST /meeting/{meetingId}] 정상 요청 시 201 응답")
	fun givenValidCreateMeetingRequest_whenCreateMeeting_thenReturnCreatedStatusAndLocationHeader() {
		val createMeetingRequest = CreateMeetingRequest(
			title = "모각코 4인팟 모집",
			location = "스타벅스 과천DT점",
			startedAt = LocalDateTime.now().plusDays(5),
			kakaoChatUrl = "https://open.kakao.com/o/12345678",
		)
		val accessToken = jwtAuthenticationProvider.generateAccessToken("test")

		given(meetingService.createMeeting(createMeetingRequest)).willReturn(1L)

		mockMvc.post("/meetings") {
			contentType = MediaType.APPLICATION_JSON
			content = objectMapper.writeValueAsString(createMeetingRequest)
			header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
		}.andExpect {
			status { isCreated() }
			header { exists(HttpHeaders.LOCATION) }
			content { jsonPath("$.meetingId") { isNumber() } }
		}.andDo {
			handle(
				document(
					"meeting/create",
					requestHeaders(
						headerWithName(HttpHeaders.AUTHORIZATION).description("access token"),
						headerWithName(HttpHeaders.CONTENT_TYPE).description("${MediaType.APPLICATION_JSON} 고정"),
					),
					requestFields(
						fieldWithPath("title").description("모임 제목"),
						fieldWithPath("location").description("모임 장소"),
						fieldWithPath("startedAt").description("모임 시각"),
						fieldWithPath("kakaoChatUrl").description("카카오톡 오픈채팅 URL"),
					),
					responseHeaders(
						headerWithName(HttpHeaders.CONTENT_TYPE).description("${MediaType.APPLICATION_JSON} 고정"),
						headerWithName(HttpHeaders.LOCATION).description("생성된 모임 URI"),
					),
					responseFields(
						fieldWithPath("meetingId").type(JsonFieldType.NUMBER).description("생성된 모임 ID"),
					),
				),
			)
		}
	}

	@Test
	@DisplayName("[POST /meeting/{meetingId}] validation 통과하지 못하는 요청을 받은 경우 400 응답")
	fun givenInvalidCreateMeetingRequest_whenCreateMeeting_thenReturnBadRequestStatus() {
		val invalidKakaoChatUrl = "https://open.kakao.com/o/1234567"
		val createMeetingRequest = CreateMeetingRequest(
			title = "모각코 4인팟 모집",
			location = "스타벅스 과천DT점",
			startedAt = LocalDateTime.now().plusDays(1),
			kakaoChatUrl = invalidKakaoChatUrl,
		)
		val accessToken = jwtAuthenticationProvider.generateAccessToken("test")

		given(meetingService.createMeeting(createMeetingRequest)).willReturn(1L)

		mockMvc.post("/meetings") {
			contentType = MediaType.APPLICATION_JSON
			content = objectMapper.writeValueAsString(createMeetingRequest)
			header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
		}.andExpect {
			status { isBadRequest() }
			content {
				jsonPath("$.errorField") { value("kakaoChatUrl") }
				jsonPath("$.receivedValue") { value(invalidKakaoChatUrl) }
				jsonPath("$.message") { value("올바른 카카오톡 오픈채팅 URL 형식이 아닙니다.") }
			}
		}.andDo {
			handle(
				document(
					"meeting/create-error",
					requestHeaders(
						headerWithName(HttpHeaders.AUTHORIZATION).description("access token"),
						headerWithName(HttpHeaders.CONTENT_TYPE).description("${MediaType.APPLICATION_JSON} 고정"),
					),
					requestFields(
						fieldWithPath("title").description("모임 제목"),
						fieldWithPath("location").description("모임 장소"),
						fieldWithPath("startedAt").description("모임 시각"),
						fieldWithPath("kakaoChatUrl").description("카카오톡 오픈채팅 URL"),
					),
					responseHeaders(
						headerWithName(HttpHeaders.CONTENT_TYPE).description("${MediaType.APPLICATION_JSON} 고정"),
					),
					responseFields(
						fieldWithPath("errorField").type(JsonFieldType.STRING).description("이슈가 발생한 필드"),
						fieldWithPath("receivedValue").type(JsonFieldType.STRING).description("서버가 받은 이슈가 발생한 필드 값"),
						fieldWithPath("message").type(JsonFieldType.STRING).description("에러 메시지"),
					),
				),
			)
		}
	}

}
