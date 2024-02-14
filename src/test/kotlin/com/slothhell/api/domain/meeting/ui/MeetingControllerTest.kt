package com.slothhell.api.domain.meeting.ui

import com.slothhell.api.BaseControllerTest
import com.slothhell.api.meeting.application.CreateMeetingRequest
import com.slothhell.api.meeting.application.MeetingService
import com.slothhell.api.meeting.domain.ConversationType
import com.slothhell.api.meeting.ui.MeetingController
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
		val userId = 1L
		val createMeetingRequest = CreateMeetingRequest(
			title = "모각코 4인팟 모집",
			location = "스타벅스 과천DT점",
			startedAt = LocalDateTime.now().plusDays(5),
			kakaoChatUrl = "https://open.kakao.com/o/12345678",
			allowedGender = null,
			minAge = 20,
			maxAge = null,
			description = "모여서 각자 코딩하실 3분을 더 모집합니다!",
			conversationType = ConversationType.LIGHT_CONVERSATION,
		)
		val accessToken = jwtAuthenticationProvider.generateAccessToken(userId)

		given(meetingService.createMeeting(createMeetingRequest, userId)).willReturn(1L)

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
						fieldWithPath("allowedGender").optional().description("모임에 참여 가능한 성별"),
						fieldWithPath("minAge").optional().description("모임에 참여 가능한 최소 연령"),
						fieldWithPath("maxAge").optional().description("모임에 참여 가능한 최대 연령"),
						fieldWithPath("description").optional().description("모임에 참여 가능한 최대 연령"),
						fieldWithPath("conversationType").description("대화할 수 있는 정도"),
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
		val userId = 1L
		val invalidKakaoChatUrl = "https://open.kakao.com/o/1234567"
		val createMeetingRequest = CreateMeetingRequest(
			title = "모각코 4인팟 모집",
			location = "스타벅스 과천DT점",
			startedAt = LocalDateTime.now().plusDays(5),
			kakaoChatUrl = invalidKakaoChatUrl,
			allowedGender = null,
			minAge = 20,
			maxAge = null,
			description = null,
			conversationType = ConversationType.LIGHT_CONVERSATION,
		)
		val accessToken = jwtAuthenticationProvider.generateAccessToken(userId)

		given(meetingService.createMeeting(createMeetingRequest, userId)).willReturn(1L)

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
						fieldWithPath("allowedGender").optional().description("모임에 참여 가능한 성별"),
						fieldWithPath("minAge").optional().description("모임에 참여 가능한 최소 연령"),
						fieldWithPath("maxAge").optional().description("모임에 참여 가능한 최대 연령"),
						fieldWithPath("description").optional().description("모임에 참여 가능한 최대 연령"),
						fieldWithPath("conversationType").description("대화할 수 있는 정도"),
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
