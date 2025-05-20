package nbc.ticketing.ticket911.domain.booking.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import nbc.ticketing.ticket911.domain.booking.entity.Booking;

@Getter
@Builder
public class BookingResponseDto {

	private final Long id;

	private final String userEmail;

	private final String userNickname;

	private final String concertTitle;

	private final String stageName;

	private final LocalDateTime startTime;

	private final LocalDateTime updatedAt;

	private final List<String> seatNames;

	public static BookingResponseDto from(Booking booking) {
		return BookingResponseDto.builder()
			.id(booking.getId())
			.userEmail(booking.getUserEmail())
			.userNickname(booking.getUserNickname())
			.concertTitle(booking.getConcertTitle())
			.stageName(booking.getConcertStageName())
			.startTime(booking.getConcertStartTime())
			.updatedAt(booking.getModifiedAt())
			.seatNames(booking.getSeatNames())
			.build();
	}
}
