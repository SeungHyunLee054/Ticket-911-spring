package nbc.ticketing.ticket911.domain.seat.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import nbc.ticketing.ticket911.domain.seat.entity.Seat;

public interface SeatRepository extends JpaRepository<Seat, Long> {
}
