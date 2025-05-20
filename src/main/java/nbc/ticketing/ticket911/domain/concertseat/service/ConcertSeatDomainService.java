package nbc.ticketing.ticket911.domain.concertseat.service;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import nbc.ticketing.ticket911.domain.booking.exception.BookingException;
import nbc.ticketing.ticket911.domain.booking.exception.code.BookingExceptionCode;
import nbc.ticketing.ticket911.domain.concert.entity.Concert;
import nbc.ticketing.ticket911.domain.concert.exception.ConcertException;
import nbc.ticketing.ticket911.domain.concert.exception.code.ConcertExceptionCode;
import nbc.ticketing.ticket911.domain.concert.repository.ConcertRepository;
import nbc.ticketing.ticket911.domain.concertseat.entity.ConcertSeat;

@Component
@RequiredArgsConstructor
public class ConcertSeatDomainService {

	private final ConcertRepository concertRepository;

	public void validateExistence(Long concertId) {
		if (!concertRepository.existsById(concertId)) {
			throw new ConcertException(ConcertExceptionCode.CONCERT_NOT_FOUND);
		}
	}

	public void validateAllSameConcert(Concert concert, List<ConcertSeat> concertSeats) {

		boolean allSameConcert = concertSeats.stream()
			.allMatch(seat -> seat.getConcertId().equals(concert.getId()));

		if (!allSameConcert) {
			throw new BookingException(BookingExceptionCode.DIFFERENT_CONCERTS_NOT_ALLOWED);
		}
	}

	public void validateNotReserved(List<ConcertSeat> concertSeats) {
		concertSeats.forEach(seat -> {
			if (seat.isReserved()) {
				throw new BookingException(BookingExceptionCode.SEAT_ALREADY_RESERVED);
			}
		});
	}

	public void reserveAll(List<ConcertSeat> concertSeats) {
		concertSeats.forEach(ConcertSeat::reserve);
	}

	public void cancelAll(List<ConcertSeat> concertSeats) {
		concertSeats.forEach(ConcertSeat::cancelReservation);
	}
}
