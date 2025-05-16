package nbc.ticketing.ticket911.domain.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import nbc.ticketing.ticket911.domain.booking.entity.Booking;

public interface BookingRepository extends JpaRepository<Booking, Long> {
}
