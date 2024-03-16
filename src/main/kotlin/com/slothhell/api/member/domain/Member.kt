package com.slothhell.api.member.domain

import com.slothhell.api.config.jpa.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import java.time.LocalDate

@Entity
@Table(
	indexes = [
		Index(name = "ux-member-subject", columnList = "subject", unique = true),
		Index(name = "ux-member-nickname", columnList = "nickname", unique = true),
	],
)
class Member(
	subject: String,
	provider: OAuth2Provider,
) : BaseEntity() {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	var memberId: Long? = null

	@Column(length = 50, nullable = false, unique = true)
	var subject = subject
		protected set

	@Column(length = 20, unique = true)
	var nickname: String? = null
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

	@Column(nullable = false)
	var isPushNotificationEnabled: Boolean = false
		protected set

	@Column(length = 300)
	var refreshToken: String? = null
		protected set

	fun updateInfo(nickname: String, pushNotificationEnabled: Boolean) {
		this.nickname = nickname
		this.isPushNotificationEnabled = pushNotificationEnabled
	}

	fun withdraw() {
		this.refreshToken = null
		this.isActive = false
	}

	fun logout() {
		this.refreshToken = null
	}

	fun updateRefreshToken(refreshToken: String) {
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

	fun activateWithRequiredInfo(nickname: String, birthday: LocalDate) {
		this.nickname = nickname
		this.birthday = birthday
		this.isActive = true
	}

}
