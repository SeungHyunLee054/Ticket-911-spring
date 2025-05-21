package nbc.ticketing.ticket911.domain.application.concert.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import nbc.ticketing.ticket911.domain.application.concertseat.service.ConcertSeatService;
import nbc.ticketing.ticket911.domain.concert.dto.request.ConcertCreateRequest;
import nbc.ticketing.ticket911.domain.concert.dto.request.ConcertSearchCondition;
import nbc.ticketing.ticket911.domain.concert.dto.request.ConcertUpdateRequest;
import nbc.ticketing.ticket911.domain.concert.dto.response.ConcertCreateResponse;
import nbc.ticketing.ticket911.domain.concert.dto.response.ConcertDetailResponse;
import nbc.ticketing.ticket911.domain.concert.dto.response.ConcertPageResponse;
import nbc.ticketing.ticket911.domain.concert.entity.Concert;
import nbc.ticketing.ticket911.domain.concert.repository.ConcertRepository;
import nbc.ticketing.ticket911.domain.concert.service.ConcertDomainService;
import nbc.ticketing.ticket911.domain.stage.entity.Stage;
import nbc.ticketing.ticket911.domain.stage.service.StageDomainService;
import nbc.ticketing.ticket911.domain.user.entity.User;
import nbc.ticketing.ticket911.domain.user.service.UserDomainService;

@Service
@RequiredArgsConstructor
public class ConcertService {

	private final ConcertRepository concertRepository;
	private final ConcertDomainService concertDomainService;
	private final ConcertSeatService concertSeatService;
	private final UserDomainService userDomainService;
	private final StageDomainService stageDomainService;

	@Transactional
	public ConcertCreateResponse createConcert(Long userId, Long stageId, ConcertCreateRequest request) {
		User user = userDomainService.findActiveUserById(userId);
		Stage stage = stageDomainService.getStageWithSeatsById(stageId);

		concertDomainService.validateCreatable(request.getStartTime(), request.getTicketOpen(),
			request.getTicketClose());

		Concert concert = concertDomainService.createConcert(user, stage, request);

		concertSeatService.createConcertSeats(concert, stage.getSeats());

		return ConcertCreateResponse.from(concert);

	}

	@Transactional(readOnly = true)
	public Page<ConcertPageResponse> searchConcerts(ConcertSearchCondition condition, Pageable pageable) {
		return concertRepository.searchConcerts(condition, pageable);
	}

	@Transactional(readOnly = true)
	public ConcertDetailResponse getConcertDetail(Long concertId) {
		Concert concert = concertDomainService.getActiveConcertById(concertId);

		return ConcertDetailResponse.from(concert);
	}

	@Transactional
	public ConcertDetailResponse updateConcert(Long concertId, Long userId, ConcertUpdateRequest request) {
		Concert concert = concertDomainService.getConcertById(concertId);

		concertDomainService.validateUpdatable(concert, userId);
		concertDomainService.validateCreatable(request.getStartTime(), request.getTicketOpen(),
			request.getTicketClose());

		concert.update(request);

		return ConcertDetailResponse.from(concert);
	}

	@Transactional
	public void deleteConcert(Long concertId, Long userId) {
		Concert concert = concertDomainService.getConcertById(concertId);

		concertDomainService.validateDeletable(concert, userId);

		concert.delete();
	}
}
