package com.slothhell.api.config.validation

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class EnumValidator : ConstraintValidator<Enum, String> {

	private lateinit var annotation: Enum

	override fun initialize(constraintAnnotation: Enum) {
		this.annotation = constraintAnnotation
	}

	override fun isValid(value: String?, context: ConstraintValidatorContext): Boolean {
		if (value == null && annotation.nullable) {
			return true
		}
		val enumValues = annotation.enumClass.java.enumConstants
		return enumValues.any { it.name.equals(value, annotation.ignoreCase) }
	}

}
