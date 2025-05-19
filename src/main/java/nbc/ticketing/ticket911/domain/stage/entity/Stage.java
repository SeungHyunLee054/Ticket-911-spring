package nbc.ticketing.ticket911.domain.stage.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nbc.ticketing.ticket911.common.audit.BaseEntity;
import nbc.ticketing.ticket911.domain.seat.entity.Seat;
import nbc.ticketing.ticket911.domain.stage.status.Status;

@Entity
@Getter
@Builder
@Table(name = "stages")
@NoArgsConstructor
@AllArgsConstructor
public class Stage extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long totalSeat;

	@Column(nullable = false)
	private String stageName;

	@Column(nullable = false)
	private Status status;

	@Builder.Default
	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "seat_id", nullable = false)
	private List<Seat> seats = new ArrayList<>();

	public void updateStageName(String stageName){
		this.stageName = stageName;
	}

	public void updateTotalSeat(Long totalSeat){
		this.totalSeat = totalSeat;
	}

	public void updateStatus(Status status){
		this.status = status;
	}

}
