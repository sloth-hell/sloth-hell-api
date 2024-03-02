package com.slothhell.api.member.domain

import org.springframework.data.repository.CrudRepository

interface MemberRepository : CrudRepository<Member, Long> {
	fun findBySubject(subject: String): Member?
}
