package nbc.ticketing.ticket911.domain.concert.dto.request;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

public record ConcertSearchCondition(
	String title,

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	LocalDateTime startTimeFrom,

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	LocalDateTime startTimeTo,

	String stageName
) {
}
