package com.slothhell.api.member.domain

import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query

interface MemberRepository : JpaRepository<Member, Long> {
	@Query("SELECT m FROM Member m WHERE m.memberId = :id")
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	fun findByIdWithWriteLock(id: Long): Member?

	fun findBySubject(subject: String): Member?
}
