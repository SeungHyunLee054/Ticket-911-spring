package nbc.ticketing.ticket911.domain.booking.application;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import nbc.ticketing.ticket911.common.lock.lettuce.DistributedLockService;
import nbc.ticketing.ticket911.domain.auth.vo.AuthUser;
import nbc.ticketing.ticket911.domain.booking.dto.request.BookingRequestDto;
import nbc.ticketing.ticket911.domain.booking.dto.response.BookingResponseDto;

@Service
@RequiredArgsConstructor
public class BookingFacade {

	private final DistributedLockService lockService;
	private final BookingService bookingService;

	public BookingResponseDto createBookingWithLock(AuthUser authUser, BookingRequestDto dto) {
		List<String> lockKeys = dto.getSeatIds().stream()
			.distinct()
			.sorted()
			.map(id -> "lock:seat:" + id)
			.toList();

		return lockService.runWithLock(lockKeys, 10_000L,
			() -> bookingService.createBookingLettuce(authUser, dto));
	}
}
