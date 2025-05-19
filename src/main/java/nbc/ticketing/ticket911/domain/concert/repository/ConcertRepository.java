package nbc.ticketing.ticket911.domain.concert.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;

import nbc.ticketing.ticket911.domain.concert.entity.Concert;

@Repository
public interface ConcertRepository extends JpaRepository<Concert, Long>, ConcertQueryRepository {
	List<Concert> findByDeletedAtIsNull();
}
