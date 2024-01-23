package me.lemphis.slothhell.domain.meeting.web

import com.fasterxml.jackson.databind.ObjectMapper
import me.lemphis.slothhell.BaseControllerTest
import me.lemphis.slothhell.domain.meeting.application.CreateMeetingRequest
import me.lemphis.slothhell.domain.meeting.application.MeetingService
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
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
class MeetingControllerTest(
    @Autowired private val objectMapper: ObjectMapper,
) : BaseControllerTest() {

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

        given(meetingService.createMeeting(createMeetingRequest)).willReturn(1L)

        mockMvc.post("/meetings") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(createMeetingRequest)
        }.andExpect {
            status { isCreated() }
            header { exists(HttpHeaders.LOCATION) }
            content { jsonPath("$.meetingId") { isNumber() } }
        }.andDo {
            document(
                "meeting/create",
                requestHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION).description("access token"),
                    headerWithName(HttpHeaders.ACCEPT).description("${MediaType.APPLICATION_JSON}을 포함하는 값"),
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
            )
        }
    }

}
