package nbc.ticketing.ticket911.application.booking.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import nbc.ticketing.ticket911.domain.auth.vo.AuthUser;
import nbc.ticketing.ticket911.domain.booking.constant.BookableStatus;
import nbc.ticketing.ticket911.domain.booking.dto.request.BookingRequestDto;
import nbc.ticketing.ticket911.domain.booking.dto.response.BookingResponseDto;
import nbc.ticketing.ticket911.domain.booking.entity.Booking;
import nbc.ticketing.ticket911.domain.booking.exception.BookingException;
import nbc.ticketing.ticket911.domain.booking.exception.code.BookingExceptionCode;
import nbc.ticketing.ticket911.domain.booking.repository.BookingRepository;
import nbc.ticketing.ticket911.domain.booking.service.BookingDomainService;
import nbc.ticketing.ticket911.domain.concert.entity.Concert;
import nbc.ticketing.ticket911.domain.concert.service.ConcertDomainService;
import nbc.ticketing.ticket911.domain.concertseat.entity.ConcertSeat;
import nbc.ticketing.ticket911.domain.concertseat.repository.ConcertSeatRepository;
import nbc.ticketing.ticket911.domain.concertseat.service.ConcertSeatDomainService;
import nbc.ticketing.ticket911.domain.user.entity.User;
import nbc.ticketing.ticket911.domain.user.exception.UserException;
import nbc.ticketing.ticket911.domain.user.exception.code.UserExceptionCode;
import nbc.ticketing.ticket911.domain.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class BookingService {

	private final UserRepository userRepository;
	private final BookingRepository bookingRepository;
	private final ConcertSeatRepository concertSeatRepository;
	private final BookingDomainService bookingDomainService;
	private final ConcertDomainService concertDomainService;
	private final ConcertSeatDomainService concertSeatDomainService;

	@Transactional
	public BookingResponseDto createBooking(AuthUser authUser, BookingRequestDto bookingRequestDto) {

		// 유저
		User user = findUserByIdOrElseThrow(authUser);

		// 콘서트 좌석
		List<ConcertSeat> concertSeats = concertSeatRepository.findAllById(bookingRequestDto.getSeatIds());

		// 콘서트
		Concert concert = concertSeats.get(0).getConcert();

		// 콘서트
		BookableStatus status = concertDomainService.getBookableStatus(concert, LocalDateTime.now());
		switch (status) {
			case BEFORE_OPEN -> throw new BookingException(BookingExceptionCode.BOOKING_NOT_OPEN);
			case AFTER_CLOSE -> throw new BookingException(BookingExceptionCode.BOOKING_CLOSED);
			case BOOKABLE -> {
			}
		}

		// 콘서트 좌석
		concertSeatDomainService.validateAllSameConcert(concert, concertSeats);
		concertSeatDomainService.validateNotReserved(concertSeats);

		Booking booking = Booking.builder()
			.user(user)
			.isCanceled(false)
			.concertSeats(concertSeats)
			.build();

		bookingDomainService.pay(user, booking);
		bookingRepository.save(booking);

		// 콘서트 좌석
		concertSeatDomainService.reserveAll(concertSeats);

		return BookingResponseDto.from(booking);
	}

	@Transactional(readOnly = true)
	public List<BookingResponseDto> getBookings(AuthUser authUser) {

		// 유저
		User user = findUserByIdOrElseThrow(authUser);

		List<Booking> bookings = bookingRepository.findAllByUser(user);

		return bookings.stream()
			.map(BookingResponseDto::from)
			.toList();
	}

	@Transactional(readOnly = true)
	public BookingResponseDto getBooking(AuthUser authUser, Long bookingId) {

		// 유저
		User user = findUserByIdOrElseThrow(authUser);

		Booking booking = findBookingByIdOrElseThrow(bookingId);

		if (!booking.isOwnedBy(user)) {
			throw new BookingException(BookingExceptionCode.NOT_OWN_BOOKING);
		}

		return BookingResponseDto.from(booking);
	}

	@Transactional
	public void canceledBooking(AuthUser authUser, Long bookingId) {

		// 유저
		User user = findUserByIdOrElseThrow(authUser);

		Booking booking = findBookingByIdOrElseThrow(bookingId);

		if (!booking.isOwnedBy(user)) {
			throw new BookingException(BookingExceptionCode.NOT_OWN_BOOKING);
		}

		if (booking.isCanceled()) {
			throw new BookingException(BookingExceptionCode.ALREADY_CANCELED);
		}

		if (!booking.validateCancellable(LocalDateTime.now())) {
			throw new BookingException(BookingExceptionCode.CONCERT_STARTED_CANNOT_CANCEL);
		}

		// 콘서트 좌석
		concertSeatDomainService.cancelAll(booking.getConcertSeats());
		bookingDomainService.cancelBooking(user, booking);
	}

	private User findUserByIdOrElseThrow(AuthUser authUser) {
		return userRepository.findById(authUser.getId())
			.orElseThrow(() -> new UserException(UserExceptionCode.USER_NOT_FOUND));
	}

	private Booking findBookingByIdOrElseThrow(Long bookingId) {
		return bookingRepository.findById(bookingId)
			.orElseThrow(() -> new BookingException(BookingExceptionCode.BOOKING_NOT_FOUND));
	}
}
