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

/**
 * 예약(Booking) 도메인과 관련된 핵심 비즈니스 로직을 처리하는 도메인 서비스 클래스입니다.
 * <p>
 * 예약 생성, 조회, 취소 등의 작업을 수행하며,
 * Booking 엔티티의 생명주기 및 상태 관리 책임을 가집니다.
 */
@Service
@RequiredArgsConstructor
public class BookingDomainService {

	private final BookingRepository bookingRepository;

	/**
	 * 예약을 생성하고 저장합니다.
	 *
	 * @param user         예약을 진행한 사용자
	 * @param concertSeats 예약할 좌석 목록
	 * @return 저장된 Booking 엔티티
	 */
	public Booking createBooking(User user, List<ConcertSeat> concertSeats) {
		Booking booking = Booking.builder()
			.user(user)
			.isCanceled(false)
			.concertSeats(concertSeats)
			.build();

		return bookingRepository.save(booking);
	}

	/**
	 * 사용자가 예약한 모든 Booking 목록을 조회합니다.
	 *
	 * @param user 조회 대상 사용자
	 * @return 해당 사용자가 예약한 Booking 리스트
	 */
	public List<Booking> findAllByUser(User user) {
		return bookingRepository.findAllByUser(user);
	}

	/**
	 * 예약 ID로 예약 정보를 조회하며, 존재하지 않으면 예외를 발생시킵니다.
	 *
	 * @param bookingId 조회할 예약 ID
	 * @return 해당 ID의 Booking 엔티티
	 * @throws BookingException 예약이 존재하지 않을 경우 발생
	 */
	public Booking findBookingByIdOrElseThrow(Long bookingId) {
		return bookingRepository.findById(bookingId)
			.orElseThrow(() -> new BookingException(BookingExceptionCode.BOOKING_NOT_FOUND));
	}

	/**
	 * 해당 예약을 취소 처리합니다. (내부적으로 Booking 상태를 변경)
	 *
	 * @param booking 취소할 Booking 객체
	 */
	public void cancelBooking(Booking booking) {
		booking.canceledBooking();
	}
}
