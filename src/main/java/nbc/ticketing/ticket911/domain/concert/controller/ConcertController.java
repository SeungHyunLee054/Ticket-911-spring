package nbc.ticketing.ticket911.domain.concert.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nbc.ticketing.ticket911.application.concert.service.ConcertService;
import nbc.ticketing.ticket911.domain.auth.vo.AuthUser;
import nbc.ticketing.ticket911.domain.concert.dto.request.ConcertCreateRequest;
import nbc.ticketing.ticket911.domain.concert.dto.response.ConcertCreateResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/concerts")
public class ConcertController {

	private final ConcertService concertService;

	@PostMapping
	public ResponseEntity<ConcertCreateResponse> createConcert(
		@Valid @RequestBody ConcertCreateRequest request,
		@AuthenticationPrincipal AuthUser authUser
	) {
		ConcertCreateResponse response = concertService.createConcert(authUser.getId(), request.stageId(), request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

}
