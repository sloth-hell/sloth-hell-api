package com.slothhell.api.user.domain

import com.slothhell.api.config.jpa.BaseTimeEntity
import jakarta.persistence.Column
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import java.time.LocalDate

@Entity
class User(
	@EmbeddedId val userId: UserId,
	email: String,
	profileUrl: String,
	provider: OAuth2Provider,
) : BaseTimeEntity() {

	@Column(length = 100, nullable = false, unique = true)
	var email = email
		protected set

	@Column(length = 200, nullable = false, unique = true)
	var profileUrl = profileUrl
		protected set

	@Column(length = 20, unique = true)
	lateinit var nickname: String
		protected set

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	var provider = provider
		protected set

	var birthday: LocalDate? = null
		protected set

	@Enumerated(EnumType.STRING)
	var gender: Gender? = null
		protected set

	var activated: Boolean = true
		protected set

	var pushNotificationEnabled: Boolean = false
		protected set

	@Column(length = 300, unique = true)
	var refreshToken: String? = null
		protected set

	fun logout() {
		this.refreshToken = null
	}

	fun updateRefreshTokenExpiration(refreshToken: String) {
		this.refreshToken = refreshToken
	}

	fun compareRefreshTokenWithSession(refreshToken: String) {
		if (this.refreshToken == null) {
			throw RefreshTokenNotExistException("현재 session의 refresh token이 존재하지 않습니다.")
		}
		if (this.refreshToken != refreshToken) {
			throw InvalidRefreshTokenException("전달한 refresh token이 session의 refresh token과 일치하지 않습니다.")
		}
	}

}
