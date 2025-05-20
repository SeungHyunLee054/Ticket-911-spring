package nbc.ticketing.ticket911.domain.application.concertseat.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import nbc.ticketing.ticket911.domain.concert.entity.Concert;
import nbc.ticketing.ticket911.domain.concert.repository.ConcertRepository;
import nbc.ticketing.ticket911.domain.concertseat.dto.response.ConcertSeatResponse;
import nbc.ticketing.ticket911.domain.concertseat.entity.ConcertSeat;
import nbc.ticketing.ticket911.domain.concertseat.repository.ConcertSeatRepository;
import nbc.ticketing.ticket911.domain.concertseat.service.ConcertSeatDomainService;
import nbc.ticketing.ticket911.domain.seat.entity.Seat;
import nbc.ticketing.ticket911.domain.seat.repository.SeatRepository;

@Service
@RequiredArgsConstructor
public class ConcertSeatService {

	private final ConcertSeatDomainService concertSeatDomainService;
	private final ConcertSeatRepository concertSeatRepository;

	@Transactional
	public void createConcertSeats(Concert concert, List<Seat> seats) {
		List<ConcertSeat> concertSeats = concertSeatDomainService.createAll(concert, seats);
		concertSeatRepository.saveAll(concertSeats);
	}

	@Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
	public List<ConcertSeatResponse> getSeatsByConcert(Long concertId) {
		concertSeatDomainService.validateExistence(concertId);
		return concertSeatDomainService.getSeatResponsesByConcertId(concertId);
	}

}
