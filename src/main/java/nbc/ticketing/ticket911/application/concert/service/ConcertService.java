package nbc.ticketing.ticket911.application.concert.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import nbc.ticketing.ticket911.application.concertseat.service.ConcertSeatService;
import nbc.ticketing.ticket911.domain.concert.dto.request.ConcertCreateRequest;
import nbc.ticketing.ticket911.domain.concert.dto.request.ConcertSearchCondition;
import nbc.ticketing.ticket911.domain.concert.dto.request.ConcertUpdateRequest;
import nbc.ticketing.ticket911.domain.concert.dto.response.ConcertCreateResponse;
import nbc.ticketing.ticket911.domain.concert.dto.response.ConcertDetailResponse;
import nbc.ticketing.ticket911.domain.concert.dto.response.ConcertPageResponse;
import nbc.ticketing.ticket911.domain.concert.entity.Concert;
import nbc.ticketing.ticket911.domain.concert.exception.ConcertException;
import nbc.ticketing.ticket911.domain.concert.exception.code.ConcertExceptionCode;
import nbc.ticketing.ticket911.domain.concert.repository.ConcertRepository;
import nbc.ticketing.ticket911.domain.concert.service.ConcertDomainService;
import nbc.ticketing.ticket911.domain.concertseat.entity.ConcertSeat;
import nbc.ticketing.ticket911.domain.concertseat.repository.ConcertSeatRepository;
import nbc.ticketing.ticket911.domain.stage.entity.Stage;
import nbc.ticketing.ticket911.domain.stage.exception.StageException;
import nbc.ticketing.ticket911.domain.stage.exception.code.StageExceptionCode;
import nbc.ticketing.ticket911.domain.stage.repository.StageRepository;
import nbc.ticketing.ticket911.domain.user.entity.User;
import nbc.ticketing.ticket911.domain.user.exception.UserException;
import nbc.ticketing.ticket911.domain.user.exception.code.UserExceptionCode;
import nbc.ticketing.ticket911.domain.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class ConcertService {

	private final ConcertRepository concertRepository;
	private final UserRepository userRepository;
	private final StageRepository stageRepository;
	private final ConcertDomainService concertDomainService;
	private final ConcertSeatRepository concertSeatRepository;
	private final ConcertSeatService concertSeatService;

	@Transactional
	public ConcertCreateResponse createConcert(Long userId, Long stageId, ConcertCreateRequest request) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new UserException(UserExceptionCode.USER_NOT_FOUND));
		Stage stage = stageRepository.findByIdWithSeats(stageId)
			.orElseThrow(() -> new StageException(StageExceptionCode.STAGE_NOT_FOUND));
		concertDomainService.validateCreatable(request.getStartTime(), request.getTicketOpen(), request.getTicketClose());

		Concert concert = Concert.builder()
			.user(user)
			.stage(stage)
			.title(request.getTitle())
			.description(request.getDescription())
			.startTime(request.getStartTime())
			.ticketOpen(request.getTicketOpen())
			.ticketClose(request.getTicketClose())
			.isSoldOut(false)
			.build();

		concertRepository.save(concert);

		concertSeatService.createConcertSeats(concert, stage.getSeats());

		return ConcertCreateResponse.from(concert);

	}

	@Transactional(readOnly = true)
	public Page<ConcertPageResponse> searchConcerts(ConcertSearchCondition condition, Pageable pageable) {
		return concertRepository.searchConcerts(condition, pageable);
	}

	@Transactional(readOnly = true)
	public ConcertDetailResponse getConcertDetail(Long concertId) {
		Concert concert = concertRepository.findById(concertId)
			.filter(c -> c.getDeletedAt() == null)
			.orElseThrow(() -> new ConcertException(ConcertExceptionCode.CONCERT_NOT_FOUND));

		return ConcertDetailResponse.from(concert);
	}

	@Transactional
	public ConcertDetailResponse updateConcert(Long concertId, Long userId, ConcertUpdateRequest request) {
		Concert concert = concertRepository.findById(concertId)
			.orElseThrow(() -> new ConcertException(ConcertExceptionCode.CONCERT_NOT_FOUND));

		concertDomainService.validateUpdatable(concert, userId);
		concertDomainService.validateCreatable(request.getStartTime(), request.getTicketOpen(), request.getTicketClose());

		concert.update(request);

		return ConcertDetailResponse.from(concert);
	}

	@Transactional
	public void deleteConcert(Long concertId, Long userId) {
		Concert concert = concertRepository.findById(concertId)
			.orElseThrow(() -> new ConcertException(ConcertExceptionCode.CONCERT_NOT_FOUND));

		concertDomainService.validateDeletable(concert, userId);

		concert.delete();
	}
}
