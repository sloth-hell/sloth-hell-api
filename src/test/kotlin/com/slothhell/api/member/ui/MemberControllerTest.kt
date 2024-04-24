package com.slothhell.api.member.ui

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document
import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder
import com.slothhell.api.config.BaseControllerTest
import com.slothhell.api.member.application.CreateTokenFromProviderRequest
import com.slothhell.api.member.application.GetMemberResponse
import com.slothhell.api.member.application.MemberService
import com.slothhell.api.member.application.RegisterMemberRequest
import com.slothhell.api.member.application.TokenFromProviderResponse
import com.slothhell.api.member.application.TokenRequest
import com.slothhell.api.member.application.TokenResponse
import com.slothhell.api.member.application.UpdateMemberRequest
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
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate
import java.time.LocalDateTime

@WebMvcTest(MemberController::class)
class MemberControllerTest : BaseControllerTest() {

	@MockBean
	lateinit var memberService: MemberService

	@Test
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
				.header(HttpHeaders.AUTHORIZATION, createBearerToken(accessToken)),
		)
			.andExpect(status().isOk)
			.andExpect(jsonPath("$.memberId").value(memberId))
			.andDo(
				document(
					"member/get-member",
					ResourceSnippetParametersBuilder()
						.tag("Members")
						.description("회원 단건 조회")
						.requestHeaders(
							headerWithName(HttpHeaders.AUTHORIZATION).description("access token"),
						)
						.pathParameters(
							parameterWithName("memberId").description("조회할 회원 고유 식별자"),
						)
						.responseHeaders(
							headerWithName(HttpHeaders.CONTENT_TYPE).description("${MediaType.APPLICATION_JSON} 고정"),
						)
						.responseFields(
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
	@DisplayName("[POST /members/register] 정상 request로 요청 시 201 응답")
	fun givenValidRegisterMemberRequest_whenRegisterMember_thenReturnMemberIdWithCreatedStatus() {
		val memberId = 1L
		val accessToken = jwtAuthenticationProvider.generateAccessToken(memberId)
		val request = RegisterMemberRequest(
			subject = "verified-subject",
			gender = Gender.MALE.name,
			birthday = LocalDate.of(1992, 3, 12),
			nickname = "newNickname",
		)

		given(memberService.registerMember(request)).willReturn(memberId)

		mockMvc.perform(
			post("/members/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.header(HttpHeaders.AUTHORIZATION, createBearerToken(accessToken)),
		)
			.andExpect(status().isCreated)
			.andExpect(header().exists(HttpHeaders.LOCATION))
			.andExpect(jsonPath("$.memberId").isNumber())
			.andDo(
				document(
					"member/register",
					ResourceSnippetParametersBuilder()
						.tag("Members")
						.description("회원 등록")
						.requestHeaders(
							headerWithName(HttpHeaders.AUTHORIZATION).description("access token"),
							headerWithName(HttpHeaders.CONTENT_TYPE).description("${MediaType.APPLICATION_JSON} 고정"),
						)
						.requestFields(
							fieldWithPath("subject").type(JsonFieldType.STRING).description("provider의 유저 식별 ID"),
							fieldWithPath("gender").type(JsonFieldType.STRING).description("회원 성별"),
							fieldWithPath("birthday").type(JsonFieldType.STRING).description("생년월일"),
							fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임"),
						)
						.responseHeaders(
							headerWithName(HttpHeaders.LOCATION).description("활성화된 member의 URI"),
						)
						.responseFields(
							fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("활성화된 member의 고유 식별자"),
						),
				),
			)
	}

	@Test
	@DisplayName("[PATCH /members/{memberId}] 정상적인 memberId로 회원 정보 업데이트 요청 시 204 응답")
	fun givenValidParameters_whenUpdateMemberInfo_thenReturnNoContentStatus() {
		val memberId = 1L
		val accessToken = jwtAuthenticationProvider.generateAccessToken(memberId)
		val request = UpdateMemberRequest(
			nickname = "newNickname",
			pushNotificationEnabled = true,
		)

		mockMvc.perform(
			patch("/members/{memberId}", memberId)
				.header(HttpHeaders.AUTHORIZATION, createBearerToken(accessToken))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)),
		)
			.andExpect(status().isNoContent)
			.andExpect(jsonPath("$").doesNotExist())
			.andDo(
				document(
					"member/update-member",
					ResourceSnippetParametersBuilder()
						.tag("Members")
						.description("회원 등록")
						.requestHeaders(
							headerWithName(HttpHeaders.AUTHORIZATION).description("access token"),
							headerWithName(HttpHeaders.CONTENT_TYPE).description("${MediaType.APPLICATION_JSON} 고정"),
						)
						.pathParameters(
							parameterWithName("memberId").description("정보를 업데이트할 회원의 고유 식별자"),
						)
						.requestFields(
							fieldWithPath("nickname").type(JsonFieldType.STRING).description("변경할 닉네임"),
							fieldWithPath("pushNotificationEnabled").type(JsonFieldType.BOOLEAN)
								.description("푸시 알림 설정 여부"),
						),
				),
			)
	}

	@Test
	@DisplayName("[DELETE /members/{memberId}] 정상적인 memberId로 회원 탈퇴 요청 시 204 응답")
	fun givenValidParameters_whenWithdrawMember_thenReturnNoContentStatus() {
		val memberId = 1L
		val accessToken = jwtAuthenticationProvider.generateAccessToken(memberId)

		mockMvc.perform(
			delete("/members/{memberId}", memberId)
				.header(HttpHeaders.AUTHORIZATION, createBearerToken(accessToken)),
		)
			.andExpect(status().isNoContent)
			.andExpect(jsonPath("$").doesNotExist())
			.andDo(
				document(
					"member/withdraw",
					ResourceSnippetParametersBuilder()
						.tag("Members")
						.description("회원 탈퇴")
						.requestHeaders(
							headerWithName(HttpHeaders.AUTHORIZATION).description("access token"),
						)
						.pathParameters(
							parameterWithName("memberId").description("탈퇴할 회원 고유 식별자"),
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
		val response = TokenFromProviderResponse(
			accessToken = jwtAuthenticationProvider.generateAccessToken(memberId),
			refreshToken = jwtAuthenticationProvider.generateRefreshToken(memberId),
			memberId = memberId,
			isActive = true,
		)

		given(memberService.createTokenFromProviderAccessToken(request)).willReturn(response)

		mockMvc.perform(
			post("/members/token-from-provider")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)),
		)
			.andExpect(status().isOk)
			.andExpect(jsonPath("$.accessToken").exists())
			.andExpect(jsonPath("$.refreshToken").exists())
			.andDo(
				document(
					"member/token-from-provider",
					ResourceSnippetParametersBuilder()
						.tag("Members")
						.description("provider의 access token으로 자체 access token 발급")
						.requestHeaders(
							headerWithName(HttpHeaders.CONTENT_TYPE).description("${MediaType.APPLICATION_JSON} 고정"),
						)
						.requestFields(
							fieldWithPath("provider").type(JsonFieldType.STRING).description("OAuth2 provider"),
							fieldWithPath("providerAccessToken").type(JsonFieldType.STRING)
								.description("OAuth2 인가 절차 후 받은 provider의 access token"),
							fieldWithPath("subject").type(JsonFieldType.STRING).description("provider의 유저 식별 ID"),
						)
						.responseHeaders(
							headerWithName(HttpHeaders.CONTENT_TYPE).description("${MediaType.APPLICATION_JSON} 고정"),
						)
						.responseFields(
							fieldWithPath("accessToken").type(JsonFieldType.STRING).description("access token"),
							fieldWithPath("refreshToken").type(JsonFieldType.STRING).description("refresh token"),
							fieldWithPath("memberId").type(JsonFieldType.STRING).description("회원 고유 식별자"),
							fieldWithPath("isActive").type(JsonFieldType.STRING)
								.description("회원 활성화 여부 (성별, 생년월일, 닉네임 값이 존재할 경우 활성화 처리"),
						),
				),
			)
	}

	@Test
	@DisplayName("[POST /members/token] 정상적인 refresh token으로 access token 갱신 요청 시 200 응답")
	fun givenValidTokenRequest_whenPublishNewToken_thenReturnOkStatus() {
		val memberId = 1L
		val refreshToken = jwtAuthenticationProvider.generateRefreshToken(memberId)
		val request = TokenRequest(
			refreshToken = refreshToken,
		)

		val newAccessToken = jwtAuthenticationProvider.generateAccessToken(memberId)
		val newRefreshToken = jwtAuthenticationProvider.generateRefreshToken(memberId)
		val response = TokenResponse(
			accessToken = newAccessToken,
			refreshToken = newRefreshToken,
		)

		given(memberService.publishNewToken(refreshToken)).willReturn(response)

		mockMvc.perform(
			post("/members/token")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)),
		)
			.andExpect(status().isOk)
			.andExpect(jsonPath("$.refreshToken").isString())
			.andDo(
				document(
					"member/token",
					ResourceSnippetParametersBuilder()
						.tag("Members")
						.description("access token 재발급")
						.requestHeaders(
							headerWithName(HttpHeaders.CONTENT_TYPE).description("${MediaType.APPLICATION_JSON} 고정"),
						)
						.requestFields(
							fieldWithPath("refreshToken").type(JsonFieldType.STRING)
								.description("기존에 발급받은 refresh token"),
						)
						.responseHeaders(
							headerWithName(HttpHeaders.CONTENT_TYPE).description("${MediaType.APPLICATION_JSON} 고정"),
						)
						.responseFields(
							fieldWithPath("accessToken").type(JsonFieldType.STRING).description("새로 발급한 access token"),
							fieldWithPath("refreshToken").type(JsonFieldType.STRING)
								.description("새로 발급한 refresh token"),
						),
				),
			)
	}

	@Test
	@DisplayName("[DELETE /members/{memberId}] 인증되지 않은 상태에서 인증 API를 호출하는 경우 401 응답")
	fun givenValidParameters_whenUnauthorizedRequestToAuthenticationAPI_thenReturnUnauthorizedStatus() {
		val memberId = 1L

		mockMvc.perform(delete("/members/{memberId}", memberId))
			.andExpect(status().isUnauthorized)
			.andExpect(jsonPath("errorField").value(null))
			.andExpect(jsonPath("receivedValue").value(null))
			.andExpect(jsonPath("message").isString)
			.andDo(
				document(
					"error/unauthorized",
					ResourceSnippetParametersBuilder()
						.tag("Error (4xx)")
						.description("401 Unauthorized")
						.pathParameters(
							parameterWithName("memberId").description("탈퇴할 회원 고유 식별자"),
						)
						.responseHeaders(
							headerWithName(HttpHeaders.CONTENT_TYPE).description("${MediaType.APPLICATION_JSON} 고정"),
						)
						.responseFields(
							fieldWithPath("errorField").type(JsonFieldType.STRING).optional().description("이슈가 발생한 필드"),
							fieldWithPath("receivedValue").type(JsonFieldType.STRING).optional()
								.description("서버가 받은 이슈가 발생한 필드 값"),
							fieldWithPath("message").type(JsonFieldType.STRING).description("에러 메시지"),
						),
				),
			)
	}

	@Test
	@DisplayName("[POST /members/logout] 정상 access token으로 요청 시 204 응답")
	fun givenValidToken_whenLogout_thenReturnNoContentStatus() {
		val memberId = 1L
		val accessToken = jwtAuthenticationProvider.generateAccessToken(memberId)

		mockMvc.perform(
			post("/members/logout")
				.header(HttpHeaders.AUTHORIZATION, createBearerToken(accessToken)),
		)
			.andExpect(status().isNoContent)
			.andExpect(jsonPath("$").doesNotExist())
			.andDo(
				document(
					"member/logout",
					ResourceSnippetParametersBuilder()
						.tag("Members")
						.description("회원 로그아웃")
						.requestHeaders(
							headerWithName(HttpHeaders.AUTHORIZATION).description("access token"),
						),
				),
			)
	}

}
