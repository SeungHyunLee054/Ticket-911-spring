package nbc.ticketing.ticket911.domain.concert.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import nbc.ticketing.ticket911.domain.concert.entity.Concert;
import nbc.ticketing.ticket911.domain.concert.exception.ConcertException;
import nbc.ticketing.ticket911.domain.concert.exception.code.ConcertExceptionCode;

@Component
public class ConcertDomainService {

	public void validateCreatable(LocalDateTime startTime, LocalDateTime ticketOpen, LocalDateTime ticketClose) {
		if (ticketOpen.isAfter(startTime)) {
			throw new ConcertException(ConcertExceptionCode.TICKET_OPEN_AFTER_START);
		}

		if (ticketClose.isBefore(ticketOpen)) {
			throw new ConcertException(ConcertExceptionCode.TICKET_CLOSE_BEFORE_OPEN);
		}

		if (startTime.isBefore(LocalDateTime.now())) {
			throw new ConcertException(ConcertExceptionCode.PAST_CONCERT_DATE);
		}
	}

	public void validateUpdatable(Concert concert, Long currentUserId) {
		if (!concert.getUser().getId().equals(currentUserId)) {
			throw new ConcertException(ConcertExceptionCode.NOT_ENOUGH_ROLE);
		}

		if (concert.getStartTime().isBefore(LocalDateTime.now())) {
			throw new ConcertException(ConcertExceptionCode.CONCERT_ALREADY_STARTED);
		}
	}


}
