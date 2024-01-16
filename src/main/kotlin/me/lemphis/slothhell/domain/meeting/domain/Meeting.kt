package me.lemphis.slothhell.domain.meeting.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import me.lemphis.slothhell.config.entity.BaseTimeEntity
import me.lemphis.slothhell.domain.user.domain.Gender
import java.time.LocalDateTime

@Entity
class Meeting(
	title: String,
	location: String,
	startedAt: LocalDateTime,
	kakaoChatUrl: String,
) : BaseTimeEntity() {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	var meetingId: Long? = null
		protected set

	@Column(length = 30, nullable = false)
	var title = title
		protected set

	@Column(length = 200, nullable = false)
	var location = location
		protected set

	@Column(nullable = false)
	var startedAt = startedAt
		protected set

	@Column(length = 200, nullable = false)
	var kakaoChatUrl = kakaoChatUrl
		protected set

	@Column(length = 20)
	var kakaoChatPassword: String? = null
		protected set

	@Column(length = 200)
	var description: String? = null
		protected set

	@Enumerated(EnumType.STRING)
	var allowedGender: Gender? = null
		protected set

	var minAge: Byte? = null
		protected set

	var maxAge: Byte? = null
		protected set

	var activated: Boolean = true
		protected set

}

