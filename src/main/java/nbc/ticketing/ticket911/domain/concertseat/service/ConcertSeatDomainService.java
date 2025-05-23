package nbc.ticketing.ticket911.domain.concertseat.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import nbc.ticketing.ticket911.domain.concert.entity.Concert;
import nbc.ticketing.ticket911.domain.concert.exception.ConcertException;
import nbc.ticketing.ticket911.domain.concert.exception.code.ConcertExceptionCode;
import nbc.ticketing.ticket911.domain.concert.repository.ConcertRepository;
import nbc.ticketing.ticket911.domain.concertseat.dto.response.ConcertSeatResponse;
import nbc.ticketing.ticket911.domain.concertseat.entity.ConcertSeat;
import nbc.ticketing.ticket911.domain.concertseat.exception.ConcertSeatException;
import nbc.ticketing.ticket911.domain.concertseat.exception.code.ConcertSeatExceptionCode;
import nbc.ticketing.ticket911.domain.concertseat.repository.ConcertSeatRepository;
import nbc.ticketing.ticket911.domain.seat.entity.Seat;

@Service
@RequiredArgsConstructor
public class ConcertSeatDomainService {

	private final ConcertRepository concertRepository;
	private final ConcertSeatRepository concertSeatRepository;

	/**
	 * 콘서트 좌석 생성
	 * @param concert 생성할 공연
	 * @param seats 공연장에 포함된 좌석 목록
	 * @return 생성된 공연 좌석 목록
	 */
	public void createSeats(Concert concert, List<Seat> seats) {
		List<ConcertSeat> concertSeats = seats.stream()
			.map(seat -> ConcertSeat.create(concert, seat))
			.toList();
		concertSeatRepository.saveAll(concertSeats);
	}

	/**
	 * 콘서트 좌성 목록을 조회 훟 DTO 반환
	 *
	 * @param concertId 공연 ID
	 * @return 공연 좌석 응답 목록
	 */
	public List<ConcertSeatResponse> getSeatResponsesByConcertId(Long concertId) {
		List<ConcertSeat> seats = concertSeatRepository.findByConcert_Id(concertId);

		return seats.stream()
			.map(ConcertSeatResponse::from)
			.toList();
	}

	/**
	 * 주어진 좌석 ID 목록으로 좌석 정보를 모두 조회합니다.
	 * <p>
	 * - 입력된 ID에 해당하는 좌석이 하나도 존재하지 않을 경우 예외를 발생시킵니다.
	 * - 일부 좌석만 존재하더라도 예외를 발생시키지 않고 조회된 좌석만 반환합니다.
	 *
	 * @param ids 조회할 좌석 ID 리스트
	 * @return 조회된 {@link ConcertSeat} 리스트 (비어 있지 않음)
	 * @throws ConcertSeatException 입력된 ID로 조회된 좌석이 하나도 없을 경우 발생
	 */
	public List<ConcertSeat> findAllByIdOrThrow(List<Long> ids) {
		List<ConcertSeat> seats = concertSeatRepository.findAllById(ids);

		if (seats.isEmpty()) {
			throw new ConcertSeatException(ConcertSeatExceptionCode.INVALID_SEAT_SELECTION);
		}

		return seats;
	}

	/**
	 * 공연 ID로 공연 존재 여부를 확인
	 *
	 * @param concertId 공연 ID
	 * @throws ConcertException 공연이 존재하지 않을 경우
	 */
	public void validateExistence(Long concertId) {
		if (!concertRepository.existsById(concertId)) {
			throw new ConcertException(ConcertExceptionCode.CONCERT_NOT_FOUND);
		}
	}

	/**
	 * 공연 좌석 목록이 모두 동일한 공연에 속하는지 검사
	 * (추가로 boolean 변환 하면서 예외를 던지지 않고 상태만
	 * 반환하므로 매서드 이름도 그에 맞춰서 바꾸는게 어떨까요?)
	 * ex : hasSeatsFromDifferentConcert
	 *
	 * @param concertSeats 검사할 공연 좌석 목록
	 * @return 하나라도 다른 공연이면 true 반환
	 */
	public void validateAllSameConcert(List<ConcertSeat> concertSeats) {

		Long concertId = concertSeats.get(0).getConcertId();
		boolean hasDifferentConcert = concertSeats.stream()
			.anyMatch(seat -> !seat.getConcertId().equals(concertId));

		if (hasDifferentConcert) {
			throw new ConcertSeatException(ConcertSeatExceptionCode.DIFFERENT_CONCERTS_NOT_ALLOWED);
		}
	}

	/**
	 * 공연 좌석 중 하나라도 예약된 좌석이 있는지 검사
	 * (추가로 boolean 변환 하면서 예외를 던지지 않고 상태만
	 * 반환하므로 매서드 이름도 그에 맞춰서 바꾸는게 어떨까요?)
	 * ex : hasReservedSeat
	 *
	 * @param concertSeats 검사할 공연 좌석 목록
	 * @return 예약된 좌석이 하나라도 있으면 true 반환
	 */
	public void validateNotReserved(List<ConcertSeat> concertSeats) {
		boolean hasReserved = concertSeats.stream()
			.anyMatch(ConcertSeat::isReserved);

		if (hasReserved) {
			throw new ConcertSeatException(ConcertSeatExceptionCode.SEAT_ALREADY_RESERVED);
		}
	}

	/**
	 * 전달받은 공연 좌석 전체를 예약 상태로 변경합니다.
	 *
	 * @param concertSeats 예약할 공연 좌석 목록
	 */
	public void reserveAll(List<ConcertSeat> concertSeats) {
		concertSeats.forEach(ConcertSeat::reserve);
	}

	/**
	 * 전달받은 공연 좌석 전체를 예약 취소 상태로 변경합니다.
	 *
	 * @param concertSeats 예약을 취소할 공연 좌석 목록
	 */
	public void cancelAll(List<ConcertSeat> concertSeats) {
		concertSeats.forEach(ConcertSeat::cancelReservation);
	}

	public List<ConcertSeat> findAllByIdForUpdate(List<Long> seatIds) {
		List<ConcertSeat> seats = concertSeatRepository.findAllByIdInWithLock(seatIds);

		if (seats.isEmpty()) {
			throw new ConcertSeatException(ConcertSeatExceptionCode.INVALID_SEAT_SELECTION);
		}

		return seats;
	}
}
