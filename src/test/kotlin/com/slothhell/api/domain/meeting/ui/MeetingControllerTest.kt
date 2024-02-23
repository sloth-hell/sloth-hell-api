package com.slothhell.api.domain.meeting.ui

import com.slothhell.api.BaseControllerTest
import com.slothhell.api.meeting.application.CreateMeetingRequest
import com.slothhell.api.meeting.application.GetMeetingResponse
import com.slothhell.api.meeting.application.GetMeetingsResponse
import com.slothhell.api.meeting.application.MeetingService
import com.slothhell.api.meeting.domain.ConversationType
import com.slothhell.api.meeting.ui.MeetingController
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.restdocs.request.RequestDocumentation.queryParameters
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime

@WebMvcTest(MeetingController::class)
class MeetingControllerTest : BaseControllerTest() {

	@MockBean
	lateinit var meetingService: MeetingService

	@Test
	@WithMockUser("1")
	@DisplayName("[GET /meeting] 정상 요청 시 200 응답")
	fun givenValidPageRequest_whenGetMeetings_thenReturnOkStatusAndMeetings() {
		val memberId = 1L
		val accessToken = jwtAuthenticationProvider.generateAccessToken(memberId)
		val contents = listOf(
			GetMeetingsResponse(
				1L,
				"모각코 4인 모집",
				"스타벅스 과천DT점",
				LocalDateTime.of(2024, 2, 18, 18, 16, 5),
				"모여서 각자 코딩하실 3분을 더 모집합니다!",
				null,
				20,
				30,
				ConversationType.LIGHT_CONVERSATION,
				1L,
			),
			GetMeetingsResponse(
				2L,
				"개발자 면접 스터디",
				"이디야커피 영등포아크로타워점",
				LocalDateTime.of(2024, 5, 2, 8, 0, 0),
				"개발자 네카라쿠배 면접 스터디",
				null,
				null,
				null,
				ConversationType.COMFORTABLE,
				2L,
			),
		)

		val pageRequest = PageRequest.of(0, 10)
		val pageResponse = PageImpl(contents, pageRequest, contents.size.toLong())
		given(meetingService.getMeetings(pageRequest)).willReturn(pageResponse)

		mockMvc.get("/meetings") {
			header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
		}.andExpect {
			status { isOk() }
			content {
				jsonPath("$.totalPages") { value(1) }
				jsonPath("$.totalElements") { value(2) }
			}
		}.andDo {
			handle(
				document(
					"meeting/get-meetings",
					requestHeaders(
						headerWithName(HttpHeaders.AUTHORIZATION).description("access token"),
					),
					queryParameters(
						parameterWithName("page").optional().description("요청 페이지 번호 (0부터 시작, default: 0)"),
						parameterWithName("size").optional().description("페이지 당 항목 수 (default: 10)"),
					),
					responseHeaders(
						headerWithName(HttpHeaders.CONTENT_TYPE).description("${MediaType.APPLICATION_JSON} 고정"),
					),
					responseFields(
						fieldWithPath("content").type(JsonFieldType.ARRAY).description("현재 페이지에 포함된 데이터"),
						fieldWithPath("content.[].meetingId").type(JsonFieldType.NUMBER).description("모임 고유 식별자"),
						fieldWithPath("content.[].title").type(JsonFieldType.STRING).description("모임 제목"),
						fieldWithPath("content.[].location").type(JsonFieldType.STRING).description("모임 장소"),
						fieldWithPath("content.[].startedAt").type(JsonFieldType.STRING).description("모임 시각"),
						fieldWithPath("content.[].description").type(JsonFieldType.STRING).optional()
							.description("모임 설명"),
						fieldWithPath("content.[].allowedGender").type(JsonFieldType.STRING).optional()
							.description("모임에 참여 가능한 성별"),
						fieldWithPath("content.[].minAge").type(JsonFieldType.NUMBER).optional()
							.description("모임에 참여 가능한 최소 연령"),
						fieldWithPath("content.[].maxAge").type(JsonFieldType.NUMBER).optional()
							.description("모임에 참여 가능한 최대 연령"),
						fieldWithPath("content.[].conversationType").type(JsonFieldType.STRING)
							.description("대화할 수 있는 정도"),
						fieldWithPath("content.[].memberCount").type(JsonFieldType.NUMBER).description("현재 참여 중인 유저 수"),
						fieldWithPath("pageable.pageNumber").type(JsonFieldType.NUMBER)
							.description("현재 페이지 번호 (0부터 시작)"),
						fieldWithPath("pageable.pageSize").type(JsonFieldType.NUMBER).description("페이지 당 항목 수"),
						fieldWithPath("pageable.sort").type(JsonFieldType.OBJECT).description("정렬에 관한 정보를 포함하는 객체"),
						fieldWithPath("pageable.sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 정보가 비어있는지 여부"),
						fieldWithPath("pageable.sort.sorted").type(JsonFieldType.BOOLEAN)
							.description("정렬이 적용된 상태인지 여부"),
						fieldWithPath("pageable.sort.unsorted").type(JsonFieldType.BOOLEAN)
							.description("정렬이 되지 않은 상태인지 여부"),
						fieldWithPath("pageable.offset").type(JsonFieldType.NUMBER).description("현재 페이지의 시작 인덱스"),
						fieldWithPath("pageable.paged").type(JsonFieldType.BOOLEAN).description("페이징이 적용된 상태인지 여부"),
						fieldWithPath("pageable.unpaged").type(JsonFieldType.BOOLEAN)
							.description("페이징이 적용되지 않은 상태인지 여부"),
						fieldWithPath("last").type(JsonFieldType.BOOLEAN).description("마지막 페이지인지 여부를 나타내는 부울 값."),
						fieldWithPath("totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수"),
						fieldWithPath("totalElements").type(JsonFieldType.NUMBER).description("전체 항목(데이터) 수"),
						fieldWithPath("size").type(JsonFieldType.NUMBER).description("페이지 크기 (페이지 당 최대 항목 수)"),
						fieldWithPath("number").type(JsonFieldType.NUMBER).description("현재 페이지 번호 (0부터 시작)"),
						fieldWithPath("first").type(JsonFieldType.BOOLEAN).description("첫 번째 페이지인지 여부를 나타내는 부울 값"),
						fieldWithPath("sort").type(JsonFieldType.OBJECT).description("정렬에 관한 정보를 포함하는 객체"),
						fieldWithPath("sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 정보가 비어있는지 여부"),
						fieldWithPath("sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬이 적용된 상태인지 여부"),
						fieldWithPath("sort.unsorted").type(JsonFieldType.BOOLEAN).description("정렬이 되지 않은 상태인지 여부"),
						fieldWithPath("numberOfElements").type(JsonFieldType.NUMBER).description("현재 페이지의 항목(데이터) 수"),
						fieldWithPath("empty").type(JsonFieldType.BOOLEAN).description("현재 페이지가 비어있는지 여부를 나타내는 bool 값"),
					),
				),
			)
		}
	}

	@Test
	@WithMockUser("1")
	@DisplayName("[GET /meeting/{meetingId}] 정상 요청 시 200 응답")
	fun givenValidMeetingIdRequest_whenGetMeeting_thenReturnOkStatusAndMeeting() {
		val memberId = 1L
		val meetingId = 1L
		val accessToken = jwtAuthenticationProvider.generateAccessToken(memberId)
		val getMeetingResponse = GetMeetingResponse(
			meetingId,
			memberId,
			"memberNickName",
			"모각코 4인 모집",
			"스타벅스 과천DT점",
			LocalDateTime.of(2024, 2, 18, 18, 16, 5),
			"https://open.kakao.com/o/abcdefgh",
			"모여서 각자 코딩하실 3분을 더 모집합니다!",
			null,
			20,
			30,
			ConversationType.LIGHT_CONVERSATION,
			LocalDateTime.of(2024, 2, 1, 13, 27, 21),
		)

		given(meetingService.getMeeting(meetingId)).willReturn(getMeetingResponse)

		mockMvc.perform(
			get("/meetings/{meetingId}", meetingId)
				.header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken"),
		)
			.andExpect(status().isOk)
			.andExpect(jsonPath("$.meetingId").value(meetingId))
			.andExpect(jsonPath("$.creatorMemberId").value(memberId))
			.andDo(
				document(
					"meeting/get-meeting",
					requestHeaders(
						headerWithName(HttpHeaders.AUTHORIZATION).description("access token"),
					),
					pathParameters(
						parameterWithName("meetingId").description("조회할 모임 고유 식별자"),
					),
					responseHeaders(
						headerWithName(HttpHeaders.CONTENT_TYPE).description("${MediaType.APPLICATION_JSON} 고정"),
					),
					responseFields(
						fieldWithPath("meetingId").type(JsonFieldType.NUMBER).description("모임 고유 식별자"),
						fieldWithPath("creatorMemberId").type(JsonFieldType.NUMBER).description("모임 생성 유저 고유 식별자"),
						fieldWithPath("creatorMemberNickname").type(JsonFieldType.STRING).description("모임 생성 유저 닉네임"),
						fieldWithPath("title").type(JsonFieldType.STRING).description("모임 제목"),
						fieldWithPath("location").type(JsonFieldType.STRING).description("모임 장소"),
						fieldWithPath("startedAt").type(JsonFieldType.STRING).description("모임 시각"),
						fieldWithPath("kakaoChatUrl").type(JsonFieldType.STRING).description("카카오톡 오픈채팅 URL"),
						fieldWithPath("description").type(JsonFieldType.STRING).optional().description("모임 설명"),
						fieldWithPath("allowedGender").type(JsonFieldType.STRING).optional()
							.description("모임에 참여 가능한 성별"),
						fieldWithPath("minAge").type(JsonFieldType.NUMBER).optional().description("모임에 참여 가능한 최소 연령"),
						fieldWithPath("maxAge").type(JsonFieldType.NUMBER).optional().description("모임에 참여 가능한 최대 연령"),
						fieldWithPath("conversationType").type(JsonFieldType.STRING).description("대화할 수 있는 정도"),
						fieldWithPath("createdAt").type(JsonFieldType.STRING).description("모임 생성 시각"),
					),
				),
			)
	}

	@Test
	@WithMockUser("1")
	@DisplayName("[POST /meeting/{meetingId}] 정상 요청 시 201 응답")
	fun givenValidCreateMeetingRequest_whenCreateMeeting_thenReturnCreatedStatusAndLocationHeader() {
		val memberId = 1L
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
		val accessToken = jwtAuthenticationProvider.generateAccessToken(memberId)

		given(meetingService.createMeeting(createMeetingRequest, memberId)).willReturn(1L)

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
	@WithMockUser("1")
	@DisplayName("[POST /meeting/{meetingId}] validation 통과하지 못하는 요청을 받은 경우 400 응답")
	fun givenInvalidCreateMeetingRequest_whenCreateMeeting_thenReturnBadRequestStatus() {
		val memberId = 1L
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
		val accessToken = jwtAuthenticationProvider.generateAccessToken(memberId)

		given(meetingService.createMeeting(createMeetingRequest, memberId)).willReturn(1L)

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
