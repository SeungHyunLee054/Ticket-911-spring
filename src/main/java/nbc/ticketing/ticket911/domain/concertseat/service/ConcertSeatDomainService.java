package nbc.ticketing.ticket911.domain.concertseat.service;

import org.springframework.stereotype.Component;

import nbc.ticketing.ticket911.domain.concertseat.entity.ConcertSeat;
import nbc.ticketing.ticket911.domain.concertseat.exception.ConcertSeatException;
import nbc.ticketing.ticket911.domain.concertseat.exception.code.ConcertSeatExceptionCode;

@Component
public class ConcertSeatDomainService {

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
