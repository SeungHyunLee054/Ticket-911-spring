package nbc.ticketing.ticket911.domain.stage.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import nbc.ticketing.ticket911.domain.stage.entity.Stage;

public interface StageRepository extends JpaRepository<Stage, Long> {
    Page<Stage> findByStageNameContaining(String keyword, Pageable pageable);
}
