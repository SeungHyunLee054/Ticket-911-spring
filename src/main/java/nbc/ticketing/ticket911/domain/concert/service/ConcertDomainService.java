package nbc.ticketing.ticket911.domain.concert.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import nbc.ticketing.ticket911.domain.concert.entity.Concert;
import nbc.ticketing.ticket911.domain.concert.exception.ConcertException;
import nbc.ticketing.ticket911.domain.concert.exception.code.ConcertExceptionCode;

@Component
public class ConcertDomainService {

	public void validateCreatable(LocalDateTime startTime, LocalDateTime ticketOpen, LocalDateTime ticketClose) {
		validateAfterStartTime(startTime);

		validateBeforeEndTime(ticketOpen);

		validateBeforeStartTime(ticketClose);
	}

	public void validateUpdatable(Concert concert, Long userId) {
		validateAuthor(concert, userId);
		validateNotStarted(concert);
	}

	public void validateDeletable(Concert concert, Long userId) {
		validateAuthor(concert, userId);
	}

	private void validateAuthor(Concert concert, Long userId) {
		if (!concert.getUser().getId().equals(userId)) {
			throw new ConcertException(ConcertExceptionCode.NOT_ENOUGH_ROLE);
		}
	}

	private void validateNotStarted(Concert concert) {
		if (concert.getStartTime().isBefore(LocalDateTime.now())) {
			throw new ConcertException(ConcertExceptionCode.CONCERT_ALREADY_STARTED);
		}
	}

	private void validateAfterStartTime(LocalDateTime startTime) {
		if (startTime.isAfter(LocalDateTime.now())) {
			throw new ConcertException(ConcertExceptionCode.TICKET_OPEN_AFTER_START);
		}
	}

	private void validateBeforeEndTime(LocalDateTime endTime) {
		if (endTime.isBefore(LocalDateTime.now())) {
			throw new ConcertException(ConcertExceptionCode.TICKET_CLOSE_BEFORE_OPEN);
		}
	}

	private void validateBeforeStartTime(LocalDateTime startTime) {
		if (startTime.isBefore(LocalDateTime.now())) {
			throw new ConcertException(ConcertExceptionCode.PAST_CONCERT_DATE);
		}
	}

}
