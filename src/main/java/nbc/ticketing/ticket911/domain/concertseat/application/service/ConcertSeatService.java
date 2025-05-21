package nbc.ticketing.ticket911.domain.concertseat.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import nbc.ticketing.ticket911.domain.concert.entity.Concert;
import nbc.ticketing.ticket911.domain.concertseat.dto.response.ConcertSeatResponse;
import nbc.ticketing.ticket911.domain.concertseat.service.ConcertSeatDomainService;
import nbc.ticketing.ticket911.domain.seat.entity.Seat;

@Service
@RequiredArgsConstructor
public class ConcertSeatService {

	private final ConcertSeatDomainService concertSeatDomainService;

	@Transactional
	public void createConcertSeats(Concert concert, List<Seat> seats) {
		concertSeatDomainService.createSeats(concert, seats);
	}

	@Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
	public List<ConcertSeatResponse> getSeatsByConcert(Long concertId) {
		concertSeatDomainService.validateExistence(concertId);
		return concertSeatDomainService.getSeatResponsesByConcertId(concertId);
	}

}
