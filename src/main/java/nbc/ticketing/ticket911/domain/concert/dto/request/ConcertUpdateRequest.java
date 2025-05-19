package nbc.ticketing.ticket911.domain.concert.dto.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ConcertUpdateRequest(
	@NotBlank(message = "제목을 입력해주세요")
	String title,

	@NotBlank(message = "내용을 입력해주세요")
	String description,

	@NotNull(message = "시작 시간을 입력해주세요")
	LocalDateTime startTime,

	@NotNull(message = "예매 시작 시간을 입력해주세요")
	LocalDateTime ticketOpen,

	@NotNull(message = "예매 마감 시간을 입력해주세요")
	LocalDateTime ticketClose
) {
}
