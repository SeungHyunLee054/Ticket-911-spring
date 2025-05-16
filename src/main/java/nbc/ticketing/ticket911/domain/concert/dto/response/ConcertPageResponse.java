package nbc.ticketing.ticket911.domain.concert.dto.response;

import java.time.LocalDateTime;

public record ConcertPageResponse(
	Long id,
	String title,
	String description,
	String stageName,
	LocalDateTime startTime,
	LocalDateTime ticketOpen,
	LocalDateTime ticketClose
) {
}
