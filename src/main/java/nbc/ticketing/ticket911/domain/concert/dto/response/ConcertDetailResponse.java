package nbc.ticketing.ticket911.domain.concert.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nbc.ticketing.ticket911.domain.concert.entity.Concert;

@Getter
@AllArgsConstructor
public class ConcertDetailResponse {
	private Long id;
	private String title;
	private String description;
	private String stageName;
	private LocalDateTime startTime;
	private LocalDateTime ticketOpen;
	private LocalDateTime ticketClose;
	private boolean isSoldOut;

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
