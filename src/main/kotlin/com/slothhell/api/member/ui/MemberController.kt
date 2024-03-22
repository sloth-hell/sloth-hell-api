package com.slothhell.api.member.ui

import com.slothhell.api.member.application.AccessDeniedException
import com.slothhell.api.member.application.CreateTokenFromProviderRequest
import com.slothhell.api.member.application.GetMemberResponse
import com.slothhell.api.member.application.MemberService
import com.slothhell.api.member.application.TokenRequest
import com.slothhell.api.member.application.TokenResponse
import com.slothhell.api.member.application.UpdateMemberRequest
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.User
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/members")
class MemberController(
	private val memberService: MemberService,
) {

	@GetMapping("/{memberId}")
	fun getMember(
		@PathVariable memberId: Long,
		@AuthenticationPrincipal user: User,
	): GetMemberResponse {
		validateMemberAccessOrThrow(memberId, user, "조회 권한이 없습니다.")
		return memberService.getMember(memberId)
	}

	private fun validateMemberAccessOrThrow(memberId: Long, user: User, message: String) {
		if (memberId != user.username.toLong()) {
			throw AccessDeniedException("memberId", memberId, message)
		}
	}

	@PatchMapping("/{memberId}")
	fun updateMemberInfo(
		@PathVariable memberId: Long,
		@AuthenticationPrincipal user: User,
		@Valid @RequestBody request: UpdateMemberRequest,
	): ResponseEntity<Void> {
		validateMemberAccessOrThrow(memberId, user, "회원 정보 업데이트 권한이 없습니다.")
		memberService.updateMemberInfo(memberId, request)
		return ResponseEntity.noContent().build()
	}

	@DeleteMapping("/{memberId}")
	fun withdrawMember(
		@PathVariable memberId: Long,
		@AuthenticationPrincipal user: User,
	): ResponseEntity<Void> {
		validateMemberAccessOrThrow(memberId, user, "회원 탈퇴 권한이 없습니다.")
		memberService.withdrawMember(memberId)
		return ResponseEntity.noContent().build()
	}

	@PostMapping("/token-from-provider")
	fun createTokenFromProviderAccessToken(@Valid @RequestBody request: CreateTokenFromProviderRequest): TokenResponse {
		return memberService.createTokenFromProviderAccessToken(request)
	}

	@PostMapping("/token")
	fun publishNewToken(@RequestBody @Valid request: TokenRequest): TokenResponse {
		return memberService.publishNewToken(request.refreshToken!!)
	}

	@PostMapping("/logout")
	fun logout(
		@AuthenticationPrincipal user: User,
	): ResponseEntity<Void> {
		memberService.logout(user.username.toLong())
		return ResponseEntity.noContent().build()
	}

}
