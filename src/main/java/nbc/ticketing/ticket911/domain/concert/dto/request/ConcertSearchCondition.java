package nbc.ticketing.ticket911.domain.concert.dto.request;

import java.time.LocalDateTime;

public record ConcertSearchCondition(
	String title,
	LocalDateTime startTimeFrom,
	LocalDateTime startTimeTo,
	String stageName
) {
}
