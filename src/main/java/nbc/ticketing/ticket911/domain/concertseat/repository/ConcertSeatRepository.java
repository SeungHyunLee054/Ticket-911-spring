package nbc.ticketing.ticket911.domain.concertseat.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import nbc.ticketing.ticket911.domain.concertseat.entity.ConcertSeat;

public interface ConcertSeatRepository extends JpaRepository<ConcertSeat, Long> {
}
