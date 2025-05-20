package nbc.ticketing.ticket911.domain.concert.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ConcertPageResponse {
	private Long id;
	private String title;
	private String description;
	private String stageName;
	private LocalDateTime startTime;
	private LocalDateTime ticketOpen;
	private LocalDateTime ticketClos;
}
