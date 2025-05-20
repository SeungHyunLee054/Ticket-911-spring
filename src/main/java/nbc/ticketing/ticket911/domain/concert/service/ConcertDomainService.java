package nbc.ticketing.ticket911.domain.concert.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import nbc.ticketing.ticket911.domain.booking.constant.BookableStatus;
import nbc.ticketing.ticket911.domain.concert.dto.request.ConcertCreateRequest;
import nbc.ticketing.ticket911.domain.concert.entity.Concert;
import nbc.ticketing.ticket911.domain.concert.exception.ConcertException;
import nbc.ticketing.ticket911.domain.concert.exception.code.ConcertExceptionCode;
import nbc.ticketing.ticket911.domain.concert.repository.ConcertRepository;
import nbc.ticketing.ticket911.domain.stage.entity.Stage;
import nbc.ticketing.ticket911.domain.user.entity.User;

/**
 * 공연(Concert) 도메인 관련 비즈니스 검증을 담당하는 서비스
 */
@Service
@RequiredArgsConstructor
public class ConcertDomainService {

	private final ConcertRepository concertRepository;

	/**
	 * 공연 생성
	 *
	 * @param user 공연 등록자
	 * @param stage 공연장 정보
	 * @param request 새성 요청 DTO
	 * @return 생성된 Concert 객체
	 */
	public Concert createConcert(User user, Stage stage, ConcertCreateRequest request) {
		Concert concert = Concert.builder()
			.user(user)
			.stage(stage)
			.title(request.getTitle())
			.description(request.getDescription())
			.startTime(request.getStartTime())
			.ticketOpen(request.getTicketOpen())
			.ticketClose(request.getTicketClose())
			.isSoldOut(false)
			.build();
		return concertRepository.save(concert);
	}

	/**
	 * 공연 생성 시의 시간 조건들을 통합 검증
	 *
	 * @param startTime   공연 시작 시간
	 * @param ticketOpen  티켓 오픈 시간
	 * @param ticketClose 티켓 마감 시간
	 */
	public void validateCreatable(LocalDateTime startTime, LocalDateTime ticketOpen, LocalDateTime ticketClose) {
		validateTicketOpenBeforeStartTime(ticketOpen, startTime);
		validateTicketCloseAfterOpenTime(ticketOpen, ticketClose);
		validateConcertNotInPast(startTime);
	}

	/**
	 * 공연 수정 가능 여부를 검증
	 * - 작성자인지 확인
	 * - 공연이 이미 시작되지 않았는지 확인
	 *
	 * @param concert  공연 엔티티
	 * @param userId   현재 사용자 ID
	 */
	public void validateUpdatable(Concert concert, Long userId) {
		validateAuthor(concert, userId);
		validateNotStarted(concert);
	}

	/**
	 * 공연 삭제 가능 여부를 검증
	 * - 작성자인지 확인
	 *
	 * @param concert  공연 엔티티
	 * @param userId   현재 사용자 ID
	 */
	public void validateDeletable(Concert concert, Long userId) {
		validateAuthor(concert, userId);
	}

	/**
	 * 현재 시간 기준으로 공연 예매 가능 상태를 반환
	 *
	 * @param concert 공연 엔티티
	 * @param now     현재 시간
	 * @return BookableStatus (예매 전/중/종료)
	 */
	public BookableStatus getBookableStatus(Concert concert, LocalDateTime now) {
		if (concert.getTicketOpen().isAfter(now)) {
			return BookableStatus.BEFORE_OPEN;
		}
		if (concert.getTicketClose().isBefore(now)) {
			return BookableStatus.AFTER_CLOSE;
		}
		return BookableStatus.BOOKABLE;
	}

	/**
	 * 공연 ID로 공연을 조회
	 *
	 * @param concertId 공연 ID
	 * @return Concert 엔티티
	 */
	public Concert getConcertById(Long concertId) {
		return concertRepository.findById(concertId)
			.orElseThrow(() -> new ConcertException(ConcertExceptionCode.CONCERT_NOT_FOUND));
	}

	/**
	 * 공연 ID로 삭제되지 않은 공연을 조회
	 *
	 * @param concertId 공연 ID
	 * @return Concert 엔티티
	 */
	public Concert getActiveConcertById(Long concertId) {
		return concertRepository.findById(concertId)
			.filter(c -> c.getDeletedAt() == null)
			.orElseThrow(() -> new ConcertException(ConcertExceptionCode.CONCERT_NOT_FOUND));
	}

	/**
	 * 현재 사용자가 해당 공연의 작성자인지 검증
	 *
	 * @param concert 공연 엔티티
	 * @param userId  현재 사용자 ID
	 */
	private void validateAuthor(Concert concert, Long userId) {
		if (!concert.getUser().getId().equals(userId)) {
			throw new ConcertException(ConcertExceptionCode.NOT_ENOUGH_ROLE);
		}
	}

	/**
	 * 공연이 이미 시작되지 않았는지 검증
	 *
	 * @param concert 공연 엔티티
	 */
	private void validateNotStarted(Concert concert) {
		if (concert.getStartTime().isBefore(LocalDateTime.now())) {
			throw new ConcertException(ConcertExceptionCode.CONCERT_ALREADY_STARTED);
		}
	}

	/**
	 * 티켓 오픈 시간이 공연 시작 시간보다 앞서는지 검증
	 *
	 * @param ticketOpen 티켓 오픈 시간
	 * @param startTime  공연 시작 시간
	 */
	private void validateTicketOpenBeforeStartTime(LocalDateTime ticketOpen, LocalDateTime startTime) {
		if (ticketOpen.isAfter(startTime)) {
			throw new ConcertException(ConcertExceptionCode.TICKET_OPEN_AFTER_START);
		}
	}

	/**
	 * 티켓 마감 시간이 티켓 오픈 시간 이후인지 검증
	 *
	 * @param ticketOpen  티켓 오픈 시간
	 * @param ticketClose 티켓 마감 시간
	 */
	private void validateTicketCloseAfterOpenTime(LocalDateTime ticketOpen, LocalDateTime ticketClose) {
		if (ticketClose.isBefore(ticketOpen)) {
			throw new ConcertException(ConcertExceptionCode.TICKET_CLOSE_BEFORE_OPEN);
		}
	}

	/**
	 * 공연 시작 시간이 현재보다 미래인지 검증
	 *
	 * @param startTime 공연 시작 시간
	 */
	private void validateConcertNotInPast(LocalDateTime startTime) {
		if (startTime.isBefore(LocalDateTime.now())) {
			throw new ConcertException(ConcertExceptionCode.PAST_CONCERT_DATE);
		}
	}

}
