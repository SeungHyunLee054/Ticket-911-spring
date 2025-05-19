package nbc.ticketing.ticket911.domain.concert.dto.response;

import java.time.LocalDateTime;

import nbc.ticketing.ticket911.domain.concert.entity.Concert;

public record ConcertDetailResponse(
	Long id,
	String title,
	String description,
	String stageName,
	LocalDateTime startTime,
	LocalDateTime ticketOpen,
	LocalDateTime ticketClose,
	boolean isSoldOut
) {
	public static ConcertDetailResponse from(Concert concert) {
		return new ConcertDetailResponse(
			concert.getId(),
			concert.getTitle(),
			concert.getDescription(),
			concert.getStage().getStageName(),
			concert.getStartTime(),
			concert.getTicketOpen(),
			concert.getTicketClose(),
			concert.isSoldOut()
		);
	}
}
