package nbc.ticketing.ticket911.infrastructure.security.jwt.exception.code;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JwtTokenExceptionCode {
	TOKEN_EXPIRED(false, HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
	EMPTY_TOKEN(false, HttpStatus.UNAUTHORIZED, "헤더에 토큰을 포함하고 있지 않습니다."),
	MALFORMED_JWT_REQUEST(false, HttpStatus.UNAUTHORIZED, "요청 형태가 잘못 되었습니다."),
	BAD_REQUEST(false, HttpStatus.BAD_REQUEST, "옳바르지 않은 요청입니다.."),
	EXPIRED_JWT_TOKEN(false, HttpStatus.FORBIDDEN, "만료된 JWT 토큰입니다."),
	NOT_VALID_JWT_TOKEN(false, HttpStatus.FORBIDDEN, "옳바르지 않은 JWT 토큰입니다."),
	NOT_VALID_SIGNATURE(false, HttpStatus.FORBIDDEN, "서명이 옳바르지 않습니다."),
	NOT_VALID_CONTENT(false, HttpStatus.FORBIDDEN, "내용이 옳바르지 않습니다.");

	private final boolean success;
	private final HttpStatus status;
	private final String message;
}
