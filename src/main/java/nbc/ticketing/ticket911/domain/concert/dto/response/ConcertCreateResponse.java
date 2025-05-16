package nbc.ticketing.ticket911.domain.concert.dto.response;

import java.time.LocalDateTime;

import nbc.ticketing.ticket911.domain.concert.entity.Concert;

public record ConcertCreateResponse(
	Long id,
	String title,
	String description,
	LocalDateTime startTime
) {

	public static ConcertCreateResponse from(Concert concert) {
		return new ConcertCreateResponse(
			concert.getId(),
			concert.getTitle(),
			concert.getDescription(),
			concert.getStartTime()
		);
	}
}
