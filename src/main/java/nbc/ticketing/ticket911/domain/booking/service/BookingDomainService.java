package nbc.ticketing.ticket911.domain.booking.service;

import org.springframework.stereotype.Component;

import nbc.ticketing.ticket911.domain.booking.entity.Booking;
import nbc.ticketing.ticket911.domain.booking.exception.BookingException;
import nbc.ticketing.ticket911.domain.booking.exception.code.BookingExceptionCode;
import nbc.ticketing.ticket911.domain.user.entity.User;

@Component
public class BookingDomainService {

	public void pay(User user, Booking booking) {
		int totalPrice = booking.getTotalPrice();

		if (user.getPoint() < totalPrice) {
			throw new BookingException(BookingExceptionCode.PAYMENT_FAILED);
		}

		user.usePoint(totalPrice);
	}

	public void cancelBooking(User user, Booking booking) {
		int refundPoint = booking.getTotalPrice();
		user.refundPoint(refundPoint);
		booking.canceledBooking();
	}
}
