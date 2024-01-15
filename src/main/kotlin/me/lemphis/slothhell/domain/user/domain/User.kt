package me.lemphis.slothhell.domain.user.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import me.lemphis.slothhell.config.entity.BaseTimeEntity
import java.time.LocalDate

@Entity
class User(
	userId: String,
	email: String,
	profileUrl: String,
	provider: OAuth2Provider,
) : BaseTimeEntity() {

	@Id
	var userId = userId
		protected set

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
		if (refreshToken != this.refreshToken) {
			throw InvalidRefreshTokenException("전달한 refresh token이 session의 refresh token과 일치하지 않습니다.")
		}
	}

}
