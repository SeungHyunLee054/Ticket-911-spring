package nbc.ticketing.ticket911.domain.stage.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import nbc.ticketing.ticket911.domain.stage.entity.Stage;

public interface StageRepository extends JpaRepository<Stage, Long> {
    Page<Stage> findByStageNameContaining(String keyword, Pageable pageable);

	@Query("SELECT s FROM Stage s LEFT JOIN FETCH s.seats WHERE s.id = :id")
	Optional<Stage> findByIdWithSeats(@Param("id") Long id);
}
