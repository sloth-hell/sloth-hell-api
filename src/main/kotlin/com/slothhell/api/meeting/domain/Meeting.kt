package com.slothhell.api.meeting.domain

import com.slothhell.api.config.jpa.BaseEntity
import com.slothhell.api.participant.domain.Participant
import com.slothhell.api.user.domain.Gender
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import java.time.LocalDateTime

@Entity
class Meeting(
	title: String,
	creatorUserId: Long,
	location: String,
	startedAt: LocalDateTime,
	kakaoChatUrl: String,
	description: String? = null,
	allowedGender: Gender? = null,
	minAge: Byte? = null,
	maxAge: Byte? = null,
	conversationType: ConversationType,
) : BaseEntity() {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	val meetingId: Long? = null

	@Column(nullable = false, updatable = false)
	var creatorUserId: Long = creatorUserId
		protected set

	@OneToMany(mappedBy = "participantId", cascade = [CascadeType.ALL])
	val participants: Set<Participant> = mutableSetOf()

	@Column(length = 30, nullable = false)
	var title = title
		protected set

	@Column(length = 200, nullable = false)
	var location = location
		protected set

	@Column(nullable = false)
	var startedAt = startedAt
		protected set

	@Column(length = 33, nullable = false, columnDefinition = "CHAR(33)")
	var kakaoChatUrl = kakaoChatUrl
		protected set

	@Column(length = 20)
	var kakaoChatPassword: String? = null
		protected set

	@Column(length = 200)
	var description: String? = description
		protected set

	@Enumerated(EnumType.STRING)
	var allowedGender: Gender? = allowedGender
		protected set

	var minAge: Byte? = minAge
		protected set

	var maxAge: Byte? = maxAge
		protected set

	@Enumerated(EnumType.STRING)
	var conversationType: ConversationType = conversationType
		protected set

}

