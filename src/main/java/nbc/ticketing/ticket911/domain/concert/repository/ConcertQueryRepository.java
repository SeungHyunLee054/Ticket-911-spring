package nbc.ticketing.ticket911.domain.concert.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import nbc.ticketing.ticket911.domain.concert.dto.request.ConcertSearchCondition;
import nbc.ticketing.ticket911.domain.concert.dto.response.ConcertPageResponse;

public interface ConcertQueryRepository {
	Page<ConcertPageResponse> searchConcerts(ConcertSearchCondition condition, Pageable pageable);
}
