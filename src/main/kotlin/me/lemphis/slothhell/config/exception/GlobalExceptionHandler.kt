package me.lemphis.slothhell.config.exception

import jakarta.validation.ConstraintViolationException
import me.lemphis.slothhell.config.dto.ErrorResponse
import me.lemphis.slothhell.domain.meeting.application.MeetingNotExistException
import me.lemphis.slothhell.domain.user.domain.InvalidRefreshTokenException
import me.lemphis.slothhell.domain.user.domain.RefreshTokenNotExistException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.NoHandlerFoundException
import org.springframework.web.servlet.resource.NoResourceFoundException


@RestControllerAdvice
class GlobalExceptionHandler {

	// path parameter, query parameter type mismatch
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentTypeMismatchException::class)
	fun handle400(e: MethodArgumentTypeMismatchException): ErrorResponse {
		val requiredType = e.requiredType
		val errorMessage =
			"Type mismatch error. The value provided for the '$requiredType' parameter is not of the expected type. Please ensure the parameter is of the correct type and try again."
		return ErrorResponse(
			errorField = e.name,
			receivedValue = e.value,
			message = errorMessage,
		)
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
		return ErrorResponse(
			errorField = errorField,
			receivedValue = receivedValue,
			message = errorMessage,
		)
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
		return ErrorResponse(
			errorField = errorField,
			receivedValue = receivedValue,
			message = errorMessage,
		)
	}

	// when the requested URL doesn't have a mapped handler/controller
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(NoHandlerFoundException::class)
	fun handle404(e: NoHandlerFoundException): ErrorResponse {
		return ErrorResponse(message = e.message ?: "No handler found ${e.httpMethod} ${e.requestURL}")
	}

	// when Spring fails to find a requested resource like a file or a resource in the classpath
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(NoResourceFoundException::class)
	fun handle404(e: NoResourceFoundException): ErrorResponse {
		return ErrorResponse(message = e.message ?: "No static resource found ${e.httpMethod} /${e.resourcePath}")
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(
        InvalidRefreshTokenException::class,
        RefreshTokenNotExistException::class,
        MeetingNotExistException::class,
    )
	fun handle400(e: Exception): ErrorResponse {
		return ErrorResponse(message = e.message ?: "Bad Request: Invalid or missing parameters.")
	}

	// when an unhandled error occurs on the server
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(Throwable::class)
	fun handle500(e: Throwable): ErrorResponse {
		return ErrorResponse(message = "Sorry, an error occurred on the server. If the issue persists, please contact the administrator.")
	}

}
