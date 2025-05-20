package nbc.ticketing.ticket911.domain.user.exception.code;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserExceptionCode {
	NOT_ENOUGH_POINT(false, HttpStatus.FORBIDDEN, "포인트가 부족합니다."),
	WRONG_ROLES(false, HttpStatus.BAD_REQUEST, "유저 권한을 잘못 입력하였습니다."),
	WRONG_PASSWORD(false, HttpStatus.UNAUTHORIZED, "비밀번호가 틀렸습니다."),
	NOT_CHANGED(false, HttpStatus.BAD_REQUEST, "수정 사항이 존재하지 않습니다."),
	USER_NOT_FOUND(false, HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다."),
	ALREADY_EXISTS_EMAIL(false, HttpStatus.CONFLICT, "이미 존재하는 이메일이 있습니다."),
	ALREADY_EXISTS_NICKNAME(false, HttpStatus.CONFLICT, "이미 존재하는 닉네임이 있습니다.");

	private final boolean success;
	private final HttpStatus status;
	private final String message;
}
