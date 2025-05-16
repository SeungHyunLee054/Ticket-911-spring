package nbc.ticketing.ticket911.domain.seat.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nbc.ticketing.ticket911.common.audit.BaseEntity;

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

	@Column(nullable = false)
	private boolean isReserved;

}
