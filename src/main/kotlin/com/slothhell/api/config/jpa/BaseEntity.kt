package com.slothhell.api.config.jpa

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity {

	@Column(nullable = false)
	var activated: Boolean = true

	@Column(nullable = false, updatable = false)
	@CreatedDate
	var createdAt: LocalDateTime? = null

	@Column(nullable = false)
	@LastModifiedDate
	var updatedAt: LocalDateTime? = null

}
