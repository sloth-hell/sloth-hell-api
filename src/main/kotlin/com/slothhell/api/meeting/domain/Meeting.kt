package com.slothhell.api.meeting.domain

import com.slothhell.api.config.jpa.BaseEntity
import com.slothhell.api.member.domain.Gender
import com.slothhell.api.participant.domain.Participant
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
	location: String,
	startedAt: LocalDateTime,
	kakaoChatUrl: String,
	description: String? = null,
	allowedGender: Gender? = null,
	minAge: Int,
	maxAge: Int,
	conversationType: ConversationType,
) : BaseEntity() {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	val meetingId: Long? = null

	@OneToMany(mappedBy = "participantId", cascade = [CascadeType.PERSIST])
	var participants: MutableSet<Participant> = mutableSetOf()

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

	@Column(nullable = false)
	var minAge: Int = minAge
		protected set

	@Column(nullable = false)
	var maxAge: Int = maxAge
		protected set

	@Enumerated(EnumType.STRING)
	var conversationType: ConversationType = conversationType
		protected set

}

