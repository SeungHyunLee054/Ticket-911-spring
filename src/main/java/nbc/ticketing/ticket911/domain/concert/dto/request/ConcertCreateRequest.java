package nbc.ticketing.ticket911.domain.concert.dto.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;

public record ConcertCreateRequest(
	@NotBlank
	Long stageId,

	@NotBlank
	String title,

	@NotBlank
	String description,

	@NotBlank
	LocalDateTime startTime,

	@NotBlank
	LocalDateTime ticketOpen,

	@NotBlank
	LocalDateTime ticketClose
) {
}