package com.slothhell.api.member.ui

import com.slothhell.api.member.application.AccessDeniedException
import com.slothhell.api.member.application.AccessTokenRequest
import com.slothhell.api.member.application.GetMemberResponse
import com.slothhell.api.member.application.MemberService
import com.slothhell.api.member.application.OAuth2UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/members")
class MemberController(
	private val oauth2UserService: OAuth2UserService,
	private val memberService: MemberService,
) {

	@GetMapping("/{memberId}")
	fun getMember(
		@PathVariable memberId: Long,
		@AuthenticationPrincipal user: User,
	): GetMemberResponse {
		if (memberId != user.username.toLong()) {
			throw AccessDeniedException("memberId", memberId, "memberId: ${memberId}에 대한 조회 권한이 없습니다.")
		}
		return memberService.getMember(memberId)
	}

	@PostMapping("/access-token")
	fun publishAccessToken(
		@RequestBody request: AccessTokenRequest,
		@AuthenticationPrincipal user: User,
	) {
		oauth2UserService.publishAccessToken(user.username.toLong(), request)
	}

	@PostMapping("/logout")
	fun logout(
		@AuthenticationPrincipal user: User,
	): ResponseEntity<Void> {
		oauth2UserService.logout(user.username.toLong())
		return ResponseEntity.noContent().build()
	}

}
