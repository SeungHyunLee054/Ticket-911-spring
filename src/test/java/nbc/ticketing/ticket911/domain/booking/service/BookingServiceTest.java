package nbc.ticketing.ticket911.domain.booking.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import nbc.ticketing.ticket911.domain.auth.vo.AuthUser;
import nbc.ticketing.ticket911.domain.booking.application.BookingService;
import nbc.ticketing.ticket911.domain.booking.dto.request.BookingRequestDto;
import nbc.ticketing.ticket911.domain.booking.dto.response.BookingResponseDto;
import nbc.ticketing.ticket911.domain.booking.entity.Booking;
import nbc.ticketing.ticket911.domain.booking.exception.BookingException;
import nbc.ticketing.ticket911.domain.booking.exception.code.BookingExceptionCode;
import nbc.ticketing.ticket911.domain.booking.service.BookingDomainService;
import nbc.ticketing.ticket911.domain.concertseat.entity.ConcertSeat;
import nbc.ticketing.ticket911.domain.concertseat.service.ConcertSeatDomainService;
import nbc.ticketing.ticket911.domain.user.constant.UserRole;
import nbc.ticketing.ticket911.domain.user.entity.User;
import nbc.ticketing.ticket911.domain.user.service.UserDomainService;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

	@Mock
	private UserDomainService userDomainService;

	@Mock
	private BookingDomainService bookingDomainService;

	@Mock
	private ConcertSeatDomainService concertSeatDomainService;

	@InjectMocks
	private BookingService bookingService;

	private final User mockUser = User.builder()
		.id(1L)
		.email("test@test.com")
		.password("pw")
		.nickname("tester")
		.point(10000)
		.roles(Set.of(UserRole.ROLE_USER))
		.build();

	private final List<Long> seatIds = List.of(1L, 2L);
	private final BookingRequestDto bookingRequestDto = new BookingRequestDto(seatIds);
	private final AuthUser authUser = AuthUser.builder()
		.id(mockUser.getId())
		.email(mockUser.getEmail())
		.roles(mockUser.getRoles())
		.build();

	private final ConcertSeat seat1 = mock(ConcertSeat.class);
	private final ConcertSeat seat2 = mock(ConcertSeat.class);
	private final List<ConcertSeat> concertSeats = List.of(seat1, seat2);

	private final Booking booking = mock(Booking.class);

	@Nested
	@DisplayName("예매 생성 테스트")
	class CreateBookingTest {

		@Test
		@DisplayName("성공적으로 예매 생성")
		void success_createBooking() {
			// Given
			given(userDomainService.findActiveUserById(anyLong())).willReturn(mockUser);
			given(concertSeatDomainService.findAllByIdOrThrow(anyList())).willReturn(concertSeats);
			willDoNothing().given(bookingDomainService).validateBookable(anyList(), any());
			willDoNothing().given(concertSeatDomainService).validateAllSameConcert(anyList());
			willDoNothing().given(concertSeatDomainService).validateNotReserved(anyList());
			given(bookingDomainService.createBooking(any(), anyList())).willReturn(booking);
			given(booking.getTotalPrice()).willReturn(5000);
			willDoNothing().given(concertSeatDomainService).reserveAll(anyList());
			given(userDomainService.minusPoint(any(), anyInt())).willReturn(mockUser);

			// When
			BookingResponseDto responseDto = bookingService.createBooking(authUser, bookingRequestDto);

			// Then
			assertThat(responseDto).isNotNull();
		}
	}

	@Nested
	@DisplayName("예매 단건 조회 테스트")
	class GetBookingTest {

		@Test
		@DisplayName("예매 단건 조회 실패 - 소유자가 아님")
		void fail_notOwnedBooking() {
			// Given
			User anotherUser = User.builder().id(99L).email("notowner@test.com").build();
			given(userDomainService.findActiveUserById(anyLong())).willReturn(anotherUser);
			given(bookingDomainService.findBookingByIdOrElseThrow(anyLong())).willReturn(booking);
			given(booking.isOwnedBy(any(User.class))).willReturn(false);

			// When & Then
			BookingException exception = assertThrows(BookingException.class,
				() -> bookingService.getBooking(authUser, 1L));

			assertThat(exception.getErrorCode()).isEqualTo(BookingExceptionCode.NOT_OWN_BOOKING);
		}
	}

	@Nested
	@DisplayName("예매 취소 테스트")
	class CancelBookingTest {

		@Test
		@DisplayName("예매 취소 실패 - 콘서트 시작됨")
		void fail_concertStarted() {
			// Given
			given(userDomainService.findActiveUserById(anyLong())).willReturn(mockUser);
			given(bookingDomainService.findBookingByIdOrElseThrow(anyLong())).willReturn(booking);
			given(booking.isOwnedBy(any(User.class))).willReturn(true);
			given(booking.isCanceled()).willReturn(false);
			given(booking.validateCancellable(any())).willReturn(false); // 시작됨

			// When & Then
			BookingException exception = assertThrows(BookingException.class,
				() -> bookingService.canceledBooking(authUser, 1L));

			assertThat(exception.getErrorCode()).isEqualTo(BookingExceptionCode.CONCERT_STARTED_CANNOT_CANCEL);
		}
	}
}
