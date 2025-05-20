package nbc.ticketing.ticket911.domain.concertseat.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import nbc.ticketing.ticket911.application.concertseat.service.ConcertSeatService;
import nbc.ticketing.ticket911.domain.concertseat.dto.response.ConcertSeatResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/concerts/{concertId}")
public class ConcertSeatController {

	private final ConcertSeatService concertSeatService;

	@GetMapping("/seats")
	public ResponseEntity<List<ConcertSeatResponse>> getConcertSeats(
		@PathVariable Long concertId
	) {
		List<ConcertSeatResponse> seats = concertSeatService.getSeatsByConcert(concertId);
		return ResponseEntity.ok(seats);
	}
}
