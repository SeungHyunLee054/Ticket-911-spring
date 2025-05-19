package nbc.ticketing.ticket911.domain.concertseat.service;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import nbc.ticketing.ticket911.domain.concert.exception.ConcertException;
import nbc.ticketing.ticket911.domain.concert.exception.code.ConcertExceptionCode;
import nbc.ticketing.ticket911.domain.concert.repository.ConcertRepository;
import nbc.ticketing.ticket911.domain.concertseat.entity.ConcertSeat;
import nbc.ticketing.ticket911.domain.concertseat.exception.ConcertSeatException;
import nbc.ticketing.ticket911.domain.concertseat.exception.code.ConcertSeatExceptionCode;

@Component
@RequiredArgsConstructor
public class ConcertSeatDomainService {

	private final ConcertRepository concertRepository;

	public void validateExistence(Long concertId) {
		if (!concertRepository.existsById(concertId)) {
			throw new ConcertException(ConcertExceptionCode.CONCERT_NOT_FOUND);
		}
	}

	public void reserve(ConcertSeat seat) {
		if (seat.isReserved()) {
			throw new ConcertSeatException(ConcertSeatExceptionCode.ALREADY_BOOKED);
		}
		seat.markReserved(); // 엔티티는 단순 상태 전환 메서드만 보유
	}

	public void cancel(ConcertSeat seat) {
		seat.markUnreserved();
	}

}
