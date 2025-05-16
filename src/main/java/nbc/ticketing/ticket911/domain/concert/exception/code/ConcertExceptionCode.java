package nbc.ticketing.ticket911.domain.concert.exception.code;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ConcertExceptionCode {
	NOT_ENOUGH_ROLE(false, HttpStatus.BAD_REQUEST, "권한이 부족한 사용자 입니다."),
	CONCERT_ALREADY_STARTED(false, HttpStatus.BAD_REQUEST, "이미 시작된 공연은 수정 할 수 없습니다"),
	TICKET_OPEN_AFTER_START(false, HttpStatus.BAD_REQUEST, "예매 오픈 시간은 공연 시작 시간보다 빠르거나 같아야 합니다."),
	TICKET_CLOSE_BEFORE_OPEN(false, HttpStatus.BAD_REQUEST, "예매 마감 시간은 예매 시작 시간보다 같거나 늦어야 합니다."),
	PAST_CONCERT_DATE(false, HttpStatus.BAD_REQUEST, "과거 날짜로 공연을 등록할 수 없습니다."),
	CONCERT_NOT_FOUND(false, HttpStatus.NOT_FOUND, "해당 공연이 존재하지 않습니다");
	private final boolean success;
	private final HttpStatus status;
	private final String message;
}
