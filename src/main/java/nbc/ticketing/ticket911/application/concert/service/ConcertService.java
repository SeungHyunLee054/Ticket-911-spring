package nbc.ticketing.ticket911.application.concert.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import nbc.ticketing.ticket911.domain.concert.dto.request.ConcertCreateRequest;
import nbc.ticketing.ticket911.domain.concert.dto.response.ConcertCreateResponse;
import nbc.ticketing.ticket911.domain.concert.entity.Concert;
import nbc.ticketing.ticket911.domain.concert.repository.ConcertRepository;
import nbc.ticketing.ticket911.domain.concert.service.ConcertDomainService;
import nbc.ticketing.ticket911.domain.stage.entity.Stage;
import nbc.ticketing.ticket911.domain.stage.repository.StageRepository;
import nbc.ticketing.ticket911.domain.user.entity.User;
import nbc.ticketing.ticket911.domain.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class ConcertService {

	private final ConcertRepository concertRepository;
	private final UserRepository userRepository;
	private final StageRepository stageRepository;
	private final ConcertDomainService concertDomainService;

	@Transactional
	public ConcertCreateResponse createConcert(Long userId, Long stageId, ConcertCreateRequest request) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));
		Stage stage = stageRepository.findById(stageId)
			.orElseThrow(() -> new IllegalArgumentException("공연장이 존재하지 않습니다."));

		concertDomainService.validateCreatable(request.startTime(), request.ticketOpen(), request.ticketClose());

		Concert concert = Concert.builder()
			.user(user)
			.stage(stage)
			.title(request.title())
			.description(request.description())
			.startTime(request.startTime())
			.ticketOpen(request.ticketOpen())
			.ticketClose(request.ticketClose())
			.isSoldOut(false)
			.build();

		concertRepository.save(concert);

		return ConcertCreateResponse.from(concert);

	}
}