package nbc.ticketing.ticket911.application.concert.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import nbc.ticketing.ticket911.application.concertseat.service.ConcertSeatService;
import nbc.ticketing.ticket911.application.stage.service.StageService;
import nbc.ticketing.ticket911.application.user.service.UserService;
import nbc.ticketing.ticket911.domain.concert.dto.request.ConcertCreateRequest;
import nbc.ticketing.ticket911.domain.concert.dto.response.ConcertCreateResponse;
import nbc.ticketing.ticket911.domain.concert.service.ConcertDomainService;
import nbc.ticketing.ticket911.domain.stage.entity.Stage;
import nbc.ticketing.ticket911.domain.user.entity.User;

@Service
@RequiredArgsConstructor
public class ConcertService {

	private final ConcertSeatService concertSeatService;
	private final UserService userService;
	private final StageService stageService;
	private final ConcertDomainService concertDomainService;

	@Transactional
	public ConcertCreateResponse create(Long userId, ConcertCreateRequest request) {
		User user = userService.getById(userId);
		Stage stage = stageService.getById(request.stageId());

		return concertDomainService.createConcert(user, stage, request);
	}
}
