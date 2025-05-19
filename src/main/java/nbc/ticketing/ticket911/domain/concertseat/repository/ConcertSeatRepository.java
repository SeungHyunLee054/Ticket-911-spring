package nbc.ticketing.ticket911.domain.concertseat.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import nbc.ticketing.ticket911.domain.concertseat.entity.ConcertSeat;

@Repository
public interface ConcertSeatRepository extends JpaRepository<ConcertSeat, Long> {

	Optional<ConcertSeat> findByConcertIdAndSeatId(Long concertId, Long seatId);
}
