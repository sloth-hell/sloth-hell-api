package com.slothhell.api.config.validation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.Enum
import kotlin.reflect.KClass

@Constraint(validatedBy = [EnumValidator::class])
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class Enum(
	val message: String,
	val groups: Array<KClass<*>> = [],
	val payload: Array<KClass<out Payload>> = [],
	val enumClass: KClass<out Enum<*>>,
	val ignoreCase: Boolean = false,
	val nullable: Boolean = false,
)
