package nbc.ticketing.ticket911.domain.concertseat.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import nbc.ticketing.ticket911.domain.concertseat.entity.ConcertSeat;

@Repository
public interface ConcertSeatRepository extends JpaRepository<ConcertSeat, Long> {

	Optional<ConcertSeat> findByConcert_IdAndSeat_Id(Long concertId, Long seatId);

	List<ConcertSeat> findByConcert_Id(Long concertId);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT s FROM ConcertSeat s WHERE s.id IN :ids")
	List<ConcertSeat> findAllByIdInWithLock(@Param("ids") List<Long> ids);
}
terstset
