package com.slothhell.api.member.ui

import com.slothhell.api.config.BaseControllerTest
import com.slothhell.api.member.application.CreateTokenFromProviderRequest
import com.slothhell.api.member.application.GetMemberResponse
import com.slothhell.api.member.application.MemberService
import com.slothhell.api.member.application.TokenResponse
import com.slothhell.api.member.domain.Gender
import com.slothhell.api.member.domain.OAuth2Provider
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
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate
import java.time.LocalDateTime

@WebMvcTest(MemberController::class)
class MemberControllerTest : BaseControllerTest() {

	@MockBean
	lateinit var memberService: MemberService

	@Test
	@WithMockUser("1")
	@DisplayName("[GET /members/{memberId}] 존재하는 회원의 memberId로 요청 시 200 응답")
	fun givenValidMemberIdRequest_whenGetMember_thenReturnOkStatusAndMember() {
		val memberId = 1L
		val accessToken = jwtAuthenticationProvider.generateAccessToken(memberId)
		val response = GetMemberResponse(
			memberId,
			"jh",
			OAuth2Provider.GOOGLE,
			LocalDate.of(1995, 11, 27),
			Gender.MALE,
			true,
			LocalDateTime.of(2024, 2, 25, 18, 0, 19),
		)

		given(memberService.getMember(memberId)).willReturn(response)

		mockMvc.perform(
			get("/members/{memberId}", memberId)
				.header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken"),
		)
			.andExpect(status().isOk)
			.andExpect(jsonPath("$.memberId").value(memberId))
			.andDo(
				document(
					"member/get-member",
					requestHeaders(
						headerWithName(HttpHeaders.AUTHORIZATION).description("access token"),
					),
					pathParameters(
						parameterWithName("memberId").description("조회할 회원 고유 식별자"),
					),
					responseHeaders(
						headerWithName(HttpHeaders.CONTENT_TYPE).description("${MediaType.APPLICATION_JSON} 고정"),
					),
					responseFields(
						fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("회원 고유 식별자"),
						fieldWithPath("nickname").type(JsonFieldType.STRING).description("회원 닉네임"),
						fieldWithPath("provider").type(JsonFieldType.STRING)
							.description("OAuth2 Provider (회원 가입 시 인증한 서드 파티 제공자)"),
						fieldWithPath("birthday").type(JsonFieldType.STRING).description("회원 생일"),
						fieldWithPath("gender").type(JsonFieldType.STRING).description("회원 성별"),
						fieldWithPath("isPushNotificationEnabled").type(JsonFieldType.BOOLEAN)
							.description("회원 푸시 알림 설정 여부"),
						fieldWithPath("createdAt").type(JsonFieldType.STRING).description("회원 가입 시각"),
					),
				),
			)
	}

	@Test
	@DisplayName("[POST /members/token-from-provider] provider의 access token으로 정상 요청 시 200 응답")
	fun givenValidCreateTokenFromProviderRequest_whenCreateTokenFromProviderToken_thenReturnOkStatus() {
		val memberId = 1L
		val request = CreateTokenFromProviderRequest(
			provider = OAuth2Provider.GOOGLE.name,
			providerAccessToken = "verified-token",
			subject = "verified-subject",
		)
		val response = TokenResponse(
			accessToken = jwtAuthenticationProvider.generateAccessToken(memberId),
			refreshToken = jwtAuthenticationProvider.generateRefreshToken(memberId),
		)

		given(memberService.createTokenFromProviderAccessToken(request)).willReturn(response)

		mockMvc.post("/members/token-from-provider") {
			contentType = MediaType.APPLICATION_JSON
			content = objectMapper.writeValueAsString(request)
		}.andExpect {
			status { isOk() }
			content { jsonPath("$.accessToken") { exists() } }
			content { jsonPath("$.refreshToken") { exists() } }
		}.andDo {
			handle(
				document(
					"member/token-from-provider",
					requestHeaders(
						headerWithName(HttpHeaders.CONTENT_TYPE).description("${MediaType.APPLICATION_JSON} 고정"),
					),
					requestFields(
						fieldWithPath("provider").type(JsonFieldType.STRING).description("OAuth2 provider"),
						fieldWithPath("providerAccessToken").type(JsonFieldType.STRING)
							.description("OAuth2 인가 절차 후 받은 provider의 access token"),
						fieldWithPath("subject").type(JsonFieldType.STRING).description("provider의 유저 식별 ID"),
					),
					responseHeaders(
						headerWithName(HttpHeaders.CONTENT_TYPE).description("${MediaType.APPLICATION_JSON} 고정"),
					),
					responseFields(
						fieldWithPath("accessToken").type(JsonFieldType.STRING).description("access token"),
						fieldWithPath("refreshToken").type(JsonFieldType.STRING).description("refresh token"),
					),
				),
			)
		}
	}

	@Test
	@WithMockUser("1")
	@DisplayName("[DELETE /members/{memberId}] 정상적인 memberId로 회원 탈퇴 요청 시 204 응답")
	fun givenValidParameters_whenUnauthorizedRequestToAuthenticationAPI_thenReturnNoContentStatus() {
		val memberId = 1L

		mockMvc.perform(delete("/members/{memberId}", memberId))
			.andExpect(status().isNoContent)
			.andExpect(jsonPath("$").doesNotExist())
			.andDo(
				document(
					"member/withdraw",
					pathParameters(
						parameterWithName("memberId").description("탈퇴할 회원 고유 식별자"),
					),
				),
			)
	}

}
