package nbc.ticketing.ticket911.domain.booking.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nbc.ticketing.ticket911.domain.booking.application.BookingFacade;
import nbc.ticketing.ticket911.domain.booking.application.BookingService;
import nbc.ticketing.ticket911.common.response.CommonResponse;
import nbc.ticketing.ticket911.domain.auth.vo.AuthUser;
import nbc.ticketing.ticket911.domain.booking.dto.request.BookingRequestDto;
import nbc.ticketing.ticket911.domain.booking.dto.response.BookingResponseDto;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

	private final BookingService bookingService;
	private final BookingFacade bookingFacade;

	@PostMapping
	public ResponseEntity<CommonResponse<BookingResponseDto>> createBooking(
		@AuthenticationPrincipal AuthUser authUser,
		@Valid @RequestBody BookingRequestDto bookingRequestDto) {

		BookingResponseDto bookingResponseDto = bookingFacade.createBookingWithLock(authUser, bookingRequestDto);

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(CommonResponse.of(true, HttpStatus.CREATED.value(), "예약 성공", bookingResponseDto));
	}

	@GetMapping
	public ResponseEntity<CommonResponse<List<BookingResponseDto>>> getBookings(
		@AuthenticationPrincipal AuthUser authUser) {

		List<BookingResponseDto> bookingResponseDtoList = bookingService.getBookings(authUser);

		return ResponseEntity.status(HttpStatus.OK)
			.body(CommonResponse.of(true, HttpStatus.OK.value(), "조회 성공", bookingResponseDtoList));
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<CommonResponse<BookingResponseDto>> getBooking(
		@AuthenticationPrincipal AuthUser authUser,
		@PathVariable Long bookingId) {

		BookingResponseDto bookingResponseDto = bookingService.getBooking(authUser, bookingId);

		return ResponseEntity.status(HttpStatus.OK)
			.body(CommonResponse.of(true, HttpStatus.OK.value(), "조회 성공", bookingResponseDto));
	}

	@DeleteMapping("/{bookingId}")
	public ResponseEntity<CommonResponse<Void>> canceledBooking(
		@AuthenticationPrincipal AuthUser authUser,
		@PathVariable Long bookingId) {
		bookingService.canceledBooking(authUser, bookingId);

		return ResponseEntity.status(HttpStatus.NO_CONTENT)
			.body(CommonResponse.of(true, HttpStatus.NO_CONTENT.value(), "삭제 성공"));
	}
}
