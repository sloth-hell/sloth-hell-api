package com.slothhell.api.participant.domain

import com.slothhell.api.config.jpa.BaseEntity
import com.slothhell.api.meeting.domain.Meeting
import com.slothhell.api.user.domain.User
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(
	indexes = [
		Index(name = "ux_participant_user_id_meeting_id", columnList = "user_id, meeting_id", unique = true),
		Index(name = "ix_participant_meeting_id", columnList = "meeting_id"),
	],
)
class Participant(
	user: User,
	meeting: Meeting,
) : BaseEntity() {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	val participantId: Long? = null

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	var user: User = user
		protected set

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "meeting_id")
	var meeting: Meeting = meeting
		protected set

}
