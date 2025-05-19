package nbc.ticketing.ticket911.domain.seat.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nbc.ticketing.ticket911.common.audit.BaseEntity;
import nbc.ticketing.ticket911.domain.stage.entity.Stage;

@Entity
@Getter
@Builder
@Table(name = "seats")
@NoArgsConstructor
@AllArgsConstructor
public class Seat extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String seatName;

	@Column(nullable = false)
	private Long seatPrice;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "stage_id", nullable = false)
	private Stage stage;

	public void addStage(Stage stage) {
		this.stage = stage;
		stage.getSeats().add(this);
	}

	public void updateSeatName(String seatName) {
		this.seatName = seatName;
	}

	public void updateSeatPrice(Long seatPrice) {
		this.seatPrice = seatPrice;
	}
}
