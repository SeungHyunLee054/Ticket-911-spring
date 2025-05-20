package nbc.ticketing.ticket911.domain.booking.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nbc.ticketing.ticket911.common.audit.BaseEntity;
import nbc.ticketing.ticket911.domain.concert.entity.Concert;
import nbc.ticketing.ticket911.domain.concertseat.entity.ConcertSeat;
import nbc.ticketing.ticket911.domain.user.entity.User;

@Entity
@Getter
@Builder
@Table(name = "bookings")
@NoArgsConstructor
@AllArgsConstructor
public class Booking extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(nullable = false)
	private boolean isCanceled;

	@Builder.Default
	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "booking_id")
	private List<ConcertSeat> concertSeats = new ArrayList<>();

	public Concert getConcert() {
		return this.concertSeats.get(0).getConcert();
	}

	public String getUserEmail() {
		return this.user.getEmail();
	}

	public String getUserNickname() {
		return this.user.getNickname();
	}

	public String getConcertTitle() {
		return getConcert().getTitle();
	}

	public String getConcertStageName() {
		return getConcert().getStageName();
	}

	public LocalDateTime getConcertStartTime() {
		return getConcert().getStartTime();
	}

	public int getTotalPrice() {
		return this.concertSeats.stream()
			.mapToInt(ConcertSeat::getSeatPriceToInt)
			.sum();
	}

	public List<String> getSeatNames() {
		return this.concertSeats.stream()
			.map(ConcertSeat::getSeatName)
			.toList();
	}

	public void canceledBooking() {
		this.isCanceled = true;
	}

	public boolean isOwnedBy(User user) {
		return this.user.equals(user);
	}

	public boolean validateCancellable(LocalDateTime now) {
		return getConcertStartTime().isAfter(now);
	}

}
