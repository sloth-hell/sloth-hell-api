package com.slothhell.api.config.exception

import com.slothhell.api.config.dto.ErrorResponse
import com.slothhell.api.logger
import com.slothhell.api.meeting.application.MeetingNotExistException
import com.slothhell.api.member.application.AccessDeniedException
import com.slothhell.api.member.application.InvalidProviderTokenException
import com.slothhell.api.member.application.MemberNotExistException
import com.slothhell.api.member.domain.InvalidRefreshTokenException
import com.slothhell.api.member.domain.RefreshTokenNotExistException
import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.NoHandlerFoundException
import org.springframework.web.servlet.resource.NoResourceFoundException


@RestControllerAdvice
class GlobalExceptionHandler {

	private val log = logger()

	// path parameter, query parameter type mismatch
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentTypeMismatchException::class)
	fun handle400(e: MethodArgumentTypeMismatchException): ErrorResponse {
		val requiredType = e.requiredType
		val errorMessage =
			"Type mismatch error. The value provided for the '$requiredType' parameter is not of the expected type. Please ensure the parameter is of the correct type and try again."
		val errorResponse = ErrorResponse(
			errorField = e.name,
			receivedValue = e.value,
			message = errorMessage,
		)
		log.error(errorResponse.toString())
		return errorResponse
	}

	// query parameter(dto) type mismatch, validation error
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException::class)
	fun handle400(e: MethodArgumentNotValidException): ErrorResponse {
		val firstFieldError = e.bindingResult.fieldErrors.first()
		val errorField = firstFieldError.field
		val receivedValue = firstFieldError.rejectedValue
		val errorMessage = firstFieldError.defaultMessage
			?: "Invalid data for the '$errorField' field. Please check the provided '$receivedValue' and try again."
		val errorResponse = ErrorResponse(
			errorField = errorField,
			receivedValue = receivedValue,
			message = errorMessage,
		)
		log.error(errorResponse.toString())
		return errorResponse
	}

	// validation error when use @Validated (but it produces NullPointerException, IllegalStateException)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(ConstraintViolationException::class)
	fun handle400(e: ConstraintViolationException): ErrorResponse {
		val firstFieldError = e.constraintViolations.first()
		val errorField = firstFieldError.propertyPath.toString()
		val receivedValue = firstFieldError.invalidValue
		val errorMessage = firstFieldError.message
			?: "Invalid value '$receivedValue' for the '$errorField' parameter. Please provide a valid value according to the specified constraints."
		val errorResponse = ErrorResponse(
			errorField = errorField,
			receivedValue = receivedValue,
			message = errorMessage,
		)
		log.error(errorResponse.toString())
		return errorResponse
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(
		InvalidRefreshTokenException::class,
		RefreshTokenNotExistException::class,
		MeetingNotExistException::class,
		MemberNotExistException::class,
		InvalidProviderTokenException::class,
	)
	fun handle400(e: ApplicationRuntimeException): ErrorResponse {
		val errorResponse = ErrorResponse(
			errorField = e.errorField,
			receivedValue = e.receivedValue,
			message = e.message,
		)
		log.error(errorResponse.toString())
		return errorResponse
	}

	@ResponseStatus(HttpStatus.FORBIDDEN)
	@ExceptionHandler(AccessDeniedException::class)
	fun handle403(e: ApplicationRuntimeException): ErrorResponse {
		log.error(e.localizedMessage)
		return ErrorResponse(
			errorField = e.errorField,
			receivedValue = e.receivedValue,
			message = e.message,
		)
	}

	// when the requested URL doesn't have a mapped handler/controller
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(NoHandlerFoundException::class)
	fun handle404(e: NoHandlerFoundException): ErrorResponse {
		val errorResponse = ErrorResponse(message = e.message ?: "No handler found ${e.httpMethod} ${e.requestURL}")
		log.error(errorResponse.toString())
		return errorResponse
	}

	// when Spring fails to find a requested resource like a file or a resource in the classpath
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(NoResourceFoundException::class)
	fun handle404(e: NoResourceFoundException): ErrorResponse {
		val errorResponse =
			ErrorResponse(message = e.message ?: "No static resource found ${e.httpMethod} /${e.resourcePath}")
		log.error(errorResponse.toString())
		return errorResponse
	}

	@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
	@ExceptionHandler(HttpRequestMethodNotSupportedException::class)
	fun handle404(e: HttpRequestMethodNotSupportedException): ErrorResponse {
		val errorResponse =
			ErrorResponse(message = "[${e.method}] is not supported. supported methods: ${e.supportedHttpMethods}]")
		log.error(errorResponse.toString())
		return errorResponse
	}

	// when an unhandled error occurs on the server
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(Throwable::class)
	fun handle500(e: Throwable): ErrorResponse {
		log.error(e.localizedMessage)
		return ErrorResponse(message = "Sorry, an error occurred on the server. If the issue persists, please contact the administrator.")
	}

}
