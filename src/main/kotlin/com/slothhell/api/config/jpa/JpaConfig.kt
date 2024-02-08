package com.slothhell.api.config.jpa

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@EnableJpaAuditing
@EntityScan("com.slothhell.api.**.domain")
@Configuration
class JpaConfig
