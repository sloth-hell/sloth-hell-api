package com.slothhell.api.participant.domain

import com.slothhell.api.config.jpa.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table

@Entity
@Table(
	indexes = [
		Index(name = "ux_participant_member_id_meeting_id", columnList = "member_id, meeting_id", unique = true),
		Index(name = "ix_participant_meeting_id", columnList = "meeting_id"),
	],
)
class Participant(
	memberId: Long,
	meetingId: Long,
	isMaster: Boolean = false,
) : BaseEntity() {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	val participantId: Long? = null

	@Column(nullable = false, updatable = false)
	var memberId: Long = memberId
		protected set

	@Column(nullable = false, updatable = false)
	var meetingId: Long = meetingId
		protected set

	@Column(nullable = false)
	var isMaster: Boolean = isMaster
		protected set

}
