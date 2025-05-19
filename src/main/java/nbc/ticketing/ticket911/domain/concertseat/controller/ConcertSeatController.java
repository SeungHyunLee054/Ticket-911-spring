package nbc.ticketing.ticket911.domain.concertseat.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nbc.ticketing.ticket911.application.concertseat.service.ConcertSeatService;
import nbc.ticketing.ticket911.domain.auth.vo.AuthUser;
import nbc.ticketing.ticket911.domain.concertseat.dto.request.ConcertSeatReserveRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/concerts/{concertId}")
public class ConcertSeatController {

	private final ConcertSeatService concertSeatService;

	@PostMapping("/bookings")
	public ResponseEntity<Void> reserveConcertSeat(
		@AuthenticationPrincipal AuthUser authUser,
		@PathVariable Long concertId,
		@RequestBody @Valid ConcertSeatReserveRequest request
	) {
		concertSeatService.reserveConcertSeat(authUser.getId(), concertId, request.getSeatId());
		return ResponseEntity.status(HttpStatus.OK).build();
	}
}
