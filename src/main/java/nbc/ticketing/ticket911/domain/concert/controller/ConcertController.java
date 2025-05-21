package nbc.ticketing.ticket911.domain.concert.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nbc.ticketing.ticket911.domain.concert.application.ConcertService;
import nbc.ticketing.ticket911.domain.auth.vo.AuthUser;
import nbc.ticketing.ticket911.domain.concert.dto.request.ConcertCreateRequest;
import nbc.ticketing.ticket911.domain.concert.dto.request.ConcertSearchCondition;
import nbc.ticketing.ticket911.domain.concert.dto.request.ConcertUpdateRequest;
import nbc.ticketing.ticket911.domain.concert.dto.response.ConcertCreateResponse;
import nbc.ticketing.ticket911.domain.concert.dto.response.ConcertDetailResponse;
import nbc.ticketing.ticket911.domain.concert.dto.response.ConcertPageResponse;

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
		ConcertCreateResponse response = concertService.createConcert(authUser.getId(), request.getStageId(), request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping
	public ResponseEntity<Page<ConcertPageResponse>> searchConcerts(
		@ModelAttribute ConcertSearchCondition condition,
		Pageable pageable
	) {
		Page<ConcertPageResponse> result = concertService.searchConcerts(condition, pageable);
		return ResponseEntity.status(HttpStatus.OK).body(result);
	}

	@GetMapping("/{concertId}")
	public ResponseEntity<ConcertDetailResponse> getConcert(
		@PathVariable Long concertId
	) {
		ConcertDetailResponse response = concertService.getConcertDetail(concertId);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@PatchMapping("/{concertId}")
	public ResponseEntity<ConcertDetailResponse> updateConcert(
		@PathVariable Long concertId,
		@Valid @RequestBody ConcertUpdateRequest request,
		@AuthenticationPrincipal AuthUser authUser
	) {
		ConcertDetailResponse response = concertService.updateConcert(concertId, authUser.getId(), request);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@DeleteMapping("/{concertId}")
	public ResponseEntity<Void> deleteConcert(
		@PathVariable Long concertId,
		@AuthenticationPrincipal AuthUser authUser
	) {
		concertService.deleteConcert(concertId, authUser.getId());
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}
