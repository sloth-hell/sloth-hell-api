package com.slothhell.api.user.domain

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.io.Serializable

@Embeddable
class UserId(
	@Column(name = "user_id")
	val id: String,
) : Serializable
