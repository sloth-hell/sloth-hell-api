package com.slothhell.api.participant.domain

import com.slothhell.api.config.jpa.BaseEntity
import com.slothhell.api.meeting.domain.Meeting
import com.slothhell.api.member.domain.Member
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
		Index(name = "ux_participant_member_id_meeting_id", columnList = "member_id, meeting_id", unique = true),
		Index(name = "ix_participant_meeting_id", columnList = "meeting_id"),
	],
)
class Participant(
	member: Member,
	meeting: Meeting,
) : BaseEntity() {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	val participantId: Long? = null

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	var member: Member = member
		protected set

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "meeting_id")
	var meeting: Meeting = meeting
		protected set

}
