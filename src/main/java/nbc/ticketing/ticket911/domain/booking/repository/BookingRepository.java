package nbc.ticketing.ticket911.domain.booking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import nbc.ticketing.ticket911.domain.booking.entity.Booking;
import nbc.ticketing.ticket911.domain.user.entity.User;

public interface BookingRepository extends JpaRepository<Booking, Long> {

	List<Booking> findAllByUser(User user);
}
