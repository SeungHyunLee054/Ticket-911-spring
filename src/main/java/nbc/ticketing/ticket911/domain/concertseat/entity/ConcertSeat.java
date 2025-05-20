package nbc.ticketing.ticket911.domain.concertseat.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nbc.ticketing.ticket911.domain.concert.entity.Concert;
import nbc.ticketing.ticket911.domain.seat.entity.Seat;

@Entity
@Getter
@Builder
@Table(
	name = "concert_seats",
	uniqueConstraints = {
		@UniqueConstraint(name = "uk_concert_seat", columnNames = {"concert_id", "seat_id"})
	}
)
@NoArgsConstructor
@AllArgsConstructor
public class ConcertSeat {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "concert_id", nullable = false)
	private Concert concert;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "seat_id", nullable = false)
	private Seat seat;

	@Column(nullable = false)
	private boolean isReserved;

	public static ConcertSeat of(Concert concert, Seat seat) {
		return new ConcertSeat(null, concert, seat, false);
	}

	public void markReserved() {
		this.isReserved = true;
	}

	public void markUnreserved() {
		this.isReserved = false;
	}

}
