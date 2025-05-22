package nbc.ticketing.ticket911.common.exception.handler;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fasterxml.jackson.databind.exc.ValueInstantiationException;

import lombok.extern.slf4j.Slf4j;
import nbc.ticketing.ticket911.common.exception.BaseException;
import nbc.ticketing.ticket911.common.exception.dto.ValidationError;
import nbc.ticketing.ticket911.common.response.CommonResponse;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(BaseException.class)
	public ResponseEntity<CommonResponse<?>> handleBaseException(BaseException baseException) {
		log.error("exception : {}", baseException.getMessage());

		return ResponseEntity.status(baseException.getHttpStatus())
			.body(CommonResponse.of(false, baseException.getHttpStatus().value(), baseException.getMessage()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<CommonResponse<List<ValidationError>>> inputValidationExceptionHandler(
		MethodArgumentNotValidException methodArgumentNotValidException
	) {
		log.error("exception : {}", methodArgumentNotValidException.getMessage(), methodArgumentNotValidException);
		BindingResult result = methodArgumentNotValidException.getBindingResult();

		List<ValidationError> validationErrors = result.getFieldErrors().stream()
			.map(fieldError -> ValidationError.builder()
				.field(fieldError.getField())
				.message(fieldError.getDefaultMessage())
				.code(fieldError.getCode())
				.build())
			.toList();

		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(CommonResponse.of(false, HttpStatus.BAD_REQUEST.value(), "잘못된 요청입니다.",
				validationErrors));
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<CommonResponse<?>> handleHttpMessageNotReadableException(
		HttpMessageNotReadableException httpMessageNotReadableException
	) {
		Throwable cause = httpMessageNotReadableException.getCause();
		if (cause instanceof ValueInstantiationException && cause.getCause() instanceof BaseException baseException) {
			log.error("exception : {}", baseException.getMessage(), baseException);

			return ResponseEntity.status(baseException.getHttpStatus())
				.body(CommonResponse.of(false, baseException.getHttpStatus().value(), baseException.getMessage()));
		}

		log.error("exception : {}", httpMessageNotReadableException.getMessage(), httpMessageNotReadableException);
		return ResponseEntity.badRequest()
			.body(CommonResponse.of(false, HttpStatus.BAD_REQUEST.value(), "잘못된 요청입니다."));
	}
}
