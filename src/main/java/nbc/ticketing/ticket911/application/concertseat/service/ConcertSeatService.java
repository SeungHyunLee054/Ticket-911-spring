package nbc.ticketing.ticket911.application.concertseat.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import nbc.ticketing.ticket911.domain.concert.entity.Concert;
import nbc.ticketing.ticket911.domain.concert.exception.ConcertException;
import nbc.ticketing.ticket911.domain.concert.exception.code.ConcertExceptionCode;
import nbc.ticketing.ticket911.domain.concert.repository.ConcertRepository;
import nbc.ticketing.ticket911.domain.concertseat.controller.ConcertSeatController;
import nbc.ticketing.ticket911.domain.concertseat.entity.ConcertSeat;
import nbc.ticketing.ticket911.domain.concertseat.exception.ConcertSeatException;
import nbc.ticketing.ticket911.domain.concertseat.exception.code.ConcertSeatExceptionCode;
import nbc.ticketing.ticket911.domain.concertseat.repository.ConcertSeatRepository;
import nbc.ticketing.ticket911.domain.concertseat.service.ConcertSeatDomainService;
import nbc.ticketing.ticket911.domain.seat.entity.Seat;
import nbc.ticketing.ticket911.domain.seat.exception.SeatException;
import nbc.ticketing.ticket911.domain.seat.exception.code.SeatExceptionCode;
import nbc.ticketing.ticket911.domain.seat.repository.SeatRepository;

@Service
@RequiredArgsConstructor
public class ConcertSeatService {

	private final SeatRepository seatRepository;
	private final ConcertRepository concertRepository;
	private final ConcertSeatDomainService concertSeatDomainService;
	private final ConcertSeatRepository concertSeatRepository;

	@Transactional
	public void reserveConcertSeat(Long userId, Long concertId, Long seatId) {
		Concert concert = concertRepository.findById(concertId)
			.orElseThrow(() -> new ConcertException(ConcertExceptionCode.CONCERT_NOT_FOUND));

		Seat seat = seatRepository.findById(seatId)
			.orElseThrow(() -> new SeatException(SeatExceptionCode.SEAT_NOT_FOUND));

		ConcertSeat concertSeat = concertSeatRepository.findByConcertIdAndSeatId(concertId, seatId)
			.orElseThrow(() -> new ConcertSeatException(ConcertSeatExceptionCode.CONCERT_SEAT_NOT_FOUND));

		concertSeatDomainService.reserve(concertSeat);
	}

}
