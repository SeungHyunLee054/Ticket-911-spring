package nbc.ticketing.ticket911.domain.booking.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import nbc.ticketing.ticket911.domain.booking.entity.Booking;
import nbc.ticketing.ticket911.domain.booking.exception.BookingException;
import nbc.ticketing.ticket911.domain.booking.exception.code.BookingExceptionCode;
import nbc.ticketing.ticket911.domain.booking.repository.BookingRepository;
import nbc.ticketing.ticket911.domain.concertseat.entity.ConcertSeat;
import nbc.ticketing.ticket911.domain.user.entity.User;

@Service
@RequiredArgsConstructor
public class BookingDomainService {

	private final BookingRepository bookingRepository;

	public Booking createBooking(User user, List<ConcertSeat> concertSeats) {
		Booking booking = Booking.builder()
			.user(user)
			.isCanceled(false)
			.concertSeats(concertSeats)
			.build();

		return bookingRepository.save(booking);
	}

	public List<Booking> findAllByUser(User user) {
		return bookingRepository.findAllByUser(user);
	}

	public Booking findBookingByIdOrElseThrow(Long bookingId) {
		return bookingRepository.findById(bookingId)
			.orElseThrow(() -> new BookingException(BookingExceptionCode.BOOKING_NOT_FOUND));
	}

	public void cancelBooking(Booking booking) {
		booking.canceledBooking();
	}
}
