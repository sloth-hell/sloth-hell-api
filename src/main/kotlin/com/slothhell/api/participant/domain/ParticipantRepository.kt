package com.slothhell.api.participant.domain

import org.springframework.data.repository.CrudRepository

interface ParticipantRepository : CrudRepository<Participant, Long>
