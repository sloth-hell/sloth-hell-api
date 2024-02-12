package com.slothhell.api.meeting.domain

import com.slothhell.api.config.jpa.BaseTimeEntity
import com.slothhell.api.user.domain.Gender
import com.slothhell.api.user.domain.UserId
import jakarta.persistence.AttributeOverride
import jakarta.persistence.AttributeOverrides
import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import java.time.LocalDateTime

@Entity
class Meeting(
	title: String,
	creatorId: UserId,
	location: String,
	startedAt: LocalDateTime,
	kakaoChatUrl: String,
	description: String? = null,
	allowedGender: Gender? = null,
	minAge: Byte? = null,
	maxAge: Byte? = null,
	conversationType: ConversationType,
) : BaseTimeEntity() {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	val meetingId: Long? = null

	@Embedded
	@AttributeOverrides(
		AttributeOverride(
			name = "id",
			column = Column(name = "creator_user_id", length = 50, nullable = false),
		),
	)
	var creatorId: UserId = creatorId
		protected set

	@ElementCollection
	@CollectionTable(
		name = "user_meeting",
		joinColumns = [JoinColumn(name = "meeting_id")],
	)
	val creatorUserIds: MutableSet<UserId> = mutableSetOf()

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

	var activated: Boolean = true
		protected set

}

