package nbc.ticketing.ticket911.domain.concert.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nbc.ticketing.ticket911.domain.concert.entity.Concert;

@Getter
@AllArgsConstructor
public class ConcertCreateResponse {
	private Long id;
	private String title;
	private String description;
	private LocalDateTime startTime;

	public static ConcertCreateResponse from(Concert concert) {
		return new ConcertCreateResponse(
			concert.getId(),
			concert.getTitle(),
			concert.getDescription(),
			concert.getStartTime()
		);
	}
}
