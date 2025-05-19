package nbc.ticketing.ticket911.domain.concert.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import nbc.ticketing.ticket911.domain.concert.dto.request.ConcertSearchCondition;
import nbc.ticketing.ticket911.domain.concert.dto.response.ConcertPageResponse;
import nbc.ticketing.ticket911.domain.concert.entity.QConcert;
import nbc.ticketing.ticket911.domain.stage.entity.QStage;

@RequiredArgsConstructor
public class ConcertRepositoryImpl implements ConcertQueryRepository {
	private final JPAQueryFactory queryFactory;

	QConcert concert = QConcert.concert;
	QStage stage = QStage.stage;

	@Override
	public Page<ConcertPageResponse> searchConcerts(ConcertSearchCondition condition, Pageable pageable) {

		List<ConcertPageResponse> contents = queryFactory
			.select(Projections.constructor(
				ConcertPageResponse.class,
				concert.id,
				concert.title,
				concert.description,
				stage.stageName,
				concert.startTime,
				concert.ticketOpen,
				concert.ticketClose
			))
			.from(concert)
			.join(concert.stage, stage)
			.where(
				titleContains(condition.getTitle()),
				startTimeGte(condition.getStartTimeFrom()),
				startTimeLte(condition.getStartTimeTo()),
				stageNameContains(condition.getStageName())
			)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long total = queryFactory
			.select(concert.count())
			.from(concert)
			.join(concert.stage, stage)
			.where(
				notDeleted(),
				titleContains(condition.getTitle()),
				startTimeGte(condition.getStartTimeFrom()),
				startTimeLte(condition.getStartTimeTo()),
				stageNameContains(condition.getStageName())
			)
			.fetchOne();

		return new PageImpl<>(contents, pageable, total != null ? total : 0);

	}

	private BooleanExpression titleContains(String title) {
		return title != null && !title.isBlank() ? QConcert.concert.title.contains(title) : null;
	}

	private BooleanExpression startTimeGte(LocalDateTime from) {
		return from != null ? QConcert.concert.startTime.goe(from) : null;
	}

	private BooleanExpression startTimeLte(LocalDateTime to) {
		return to != null ? QConcert.concert.startTime.loe(to) : null;
	}

	private BooleanExpression stageNameContains(String stageName) {
		return stageName != null && !stageName.isBlank()
			? QConcert.concert.stage.stageName.contains(stageName)
			: null;
	}

	private BooleanExpression notDeleted() {
		return concert.deletedAt.isNull();
	}


}
