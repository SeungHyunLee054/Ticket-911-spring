package nbc.ticketing.ticket911.domain.concert.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import nbc.ticketing.ticket911.domain.concert.dto.request.ConcertCreateRequest;
import nbc.ticketing.ticket911.domain.concert.dto.response.ConcertCreateResponse;
import nbc.ticketing.ticket911.domain.concert.entity.Concert;
import nbc.ticketing.ticket911.domain.concert.repository.ConcertRepository;
import nbc.ticketing.ticket911.domain.stage.entity.Stage;
import nbc.ticketing.ticket911.domain.user.entity.User;

@Service
@RequiredArgsConstructor
public class ConcertDomainService {

	private final ConcertRepository concertRepository;

	public ConcertCreateResponse createConcert(User user, Stage stage, ConcertCreateRequest request) {
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

		Concert saved = concertRepository.save(concert);

		return ConcertCreateResponse.from(saved);

	}
}
