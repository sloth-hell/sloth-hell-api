package me.lemphis.slothhell.config.jpa

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@EnableJpaAuditing
@EntityScan("me.lemphis.slothhell.**.domain")
@Configuration
class JpaConfig
