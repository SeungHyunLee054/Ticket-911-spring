package nbc.ticketing.ticket911.domain.concertseat.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import nbc.ticketing.ticket911.domain.concert.entity.Concert;
import nbc.ticketing.ticket911.domain.concert.exception.ConcertException;
import nbc.ticketing.ticket911.domain.concert.exception.code.ConcertExceptionCode;
import nbc.ticketing.ticket911.domain.concert.repository.ConcertRepository;
import nbc.ticketing.ticket911.domain.concertseat.dto.response.ConcertSeatResponse;
import nbc.ticketing.ticket911.domain.concertseat.entity.ConcertSeat;
import nbc.ticketing.ticket911.domain.concertseat.repository.ConcertSeatRepository;
import nbc.ticketing.ticket911.domain.seat.entity.Seat;

@Service
@RequiredArgsConstructor
public class ConcertSeatDomainService {

	private final ConcertRepository concertRepository;
	private final ConcertSeatRepository concertSeatRepository;

	public List<ConcertSeat> createAll(Concert concert, List<Seat> seats) {
		return seats.stream()
			.map(seat -> ConcertSeat.of(concert, seat))
			.collect(Collectors.toList());
	}

	public List<ConcertSeatResponse> getSeatResponsesByConcertId(Long concertId) {
		List<ConcertSeat> seats = concertSeatRepository.findByConcertId(concertId);

		return seats.stream()
			.map(ConcertSeatResponse::from)
			.toList();
	}

	public void validateExistence(Long concertId) {
		if (!concertRepository.existsById(concertId)) {
			throw new ConcertException(ConcertExceptionCode.CONCERT_NOT_FOUND);
		}
	}

	public boolean validateAllSameConcert(Concert concert, List<ConcertSeat> concertSeats) {
		return concertSeats.stream()
			.anyMatch(seat -> !seat.getConcertId().equals(concert.getId()));
	}

	public boolean validateNotReserved(List<ConcertSeat> concertSeats) {
		return concertSeats.stream()
			.anyMatch(ConcertSeat::isReserved);
	}

	public void reserveAll(List<ConcertSeat> concertSeats) {
		concertSeats.forEach(ConcertSeat::reserve);
	}

	public void cancelAll(List<ConcertSeat> concertSeats) {
		concertSeats.forEach(ConcertSeat::cancelReservation);
	}
}
