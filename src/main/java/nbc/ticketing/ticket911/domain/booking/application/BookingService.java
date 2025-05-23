package nbc.ticketing.ticket911.domain.booking.application;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import nbc.ticketing.ticket911.common.lock.RedissonMultiLock;
import nbc.ticketing.ticket911.domain.auth.vo.AuthUser;
import nbc.ticketing.ticket911.domain.booking.dto.request.BookingRequestDto;
import nbc.ticketing.ticket911.domain.booking.dto.response.BookingResponseDto;
import nbc.ticketing.ticket911.domain.booking.entity.Booking;
import nbc.ticketing.ticket911.domain.booking.exception.BookingException;
import nbc.ticketing.ticket911.domain.booking.exception.code.BookingExceptionCode;
import nbc.ticketing.ticket911.domain.booking.service.BookingDomainService;
import nbc.ticketing.ticket911.domain.concert.service.ConcertDomainService;
import nbc.ticketing.ticket911.domain.concertseat.entity.ConcertSeat;
import nbc.ticketing.ticket911.domain.concertseat.service.ConcertSeatDomainService;
import nbc.ticketing.ticket911.domain.user.entity.User;
import nbc.ticketing.ticket911.domain.user.service.UserDomainService;

@Service
@RequiredArgsConstructor
public class BookingService {

	private final UserDomainService userDomainService;
	private final BookingDomainService bookingDomainService;
	private final ConcertDomainService concertDomainService;
	private final ConcertSeatDomainService concertSeatDomainService;

	@RedissonMultiLock(key = "#bookingRequestDto.seatIds", group = "concertSeat")
	@Transactional
	public BookingResponseDto createBooking(AuthUser authUser, BookingRequestDto bookingRequestDto) {

		User user = userDomainService.findActiveUserById(authUser.getId());

		List<ConcertSeat> concertSeats = concertSeatDomainService.findAllByIdOrThrow(bookingRequestDto.getSeatIds());

		bookingDomainService.validateBookable(concertSeats, LocalDateTime.now());
		concertSeatDomainService.validateAllSameConcert(concertSeats);
		concertSeatDomainService.validateNotReserved(concertSeats);

		Booking booking = bookingDomainService.createBooking(user, concertSeats);

		int totalPrice = booking.getTotalPrice();

		userDomainService.minusPoint(user, totalPrice);
		concertSeatDomainService.reserveAll(concertSeats);

		return BookingResponseDto.from(booking);
	}

	@Transactional(readOnly = true)
	public List<BookingResponseDto> getBookings(AuthUser authUser) {

		User user = userDomainService.findActiveUserById(authUser.getId());

		List<Booking> bookings = bookingDomainService.findAllByUser(user);

		return bookings.stream()
			.map(BookingResponseDto::from)
			.toList();
	}

	@Transactional(readOnly = true)
	public BookingResponseDto getBooking(AuthUser authUser, Long bookingId) {

		User user = userDomainService.findActiveUserById(authUser.getId());

		Booking booking = bookingDomainService.findBookingByIdOrElseThrow(bookingId);

		if (!booking.isOwnedBy(user)) {
			throw new BookingException(BookingExceptionCode.NOT_OWN_BOOKING);
		}

		return BookingResponseDto.from(booking);
	}

	@Transactional
	public void canceledBooking(AuthUser authUser, Long bookingId) {

		// 유저
		User user = userDomainService.findActiveUserById(authUser.getId());

		Booking booking = bookingDomainService.findBookingByIdOrElseThrow(bookingId);

		if (!booking.isOwnedBy(user)) {
			throw new BookingException(BookingExceptionCode.NOT_OWN_BOOKING);
		}

		if (booking.isCanceled()) {
			throw new BookingException(BookingExceptionCode.ALREADY_CANCELED);
		}

		if (!booking.validateCancellable(LocalDateTime.now())) {
			throw new BookingException(BookingExceptionCode.CONCERT_STARTED_CANNOT_CANCEL);
		}

		int totalPrice = booking.getTotalPrice();
		userDomainService.chargePoint(user, totalPrice);
		concertSeatDomainService.cancelAll(booking.getConcertSeats());
		bookingDomainService.cancelBooking(booking);
	}

	@Transactional
	public BookingResponseDto createBookingByMySQL(AuthUser authUser, BookingRequestDto bookingRequestDto) {
		User user = userDomainService.findActiveUserById(authUser.getId());

		List<ConcertSeat> concertSeats = concertSeatDomainService.findAllByIdForUpdate(bookingRequestDto.getSeatIds());

		bookingDomainService.validateBookable(concertSeats, LocalDateTime.now());
		concertSeatDomainService.validateAllSameConcert(concertSeats);
		concertSeatDomainService.validateNotReserved(concertSeats);

		Booking booking = bookingDomainService.createBooking(user, concertSeats);

		int totalPrice = booking.getTotalPrice();

		userDomainService.minusPoint(user, totalPrice);
		concertSeatDomainService.reserveAll(concertSeats);

		return BookingResponseDto.from(booking);

	}
}
