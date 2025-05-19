package nbc.ticketing.ticket911.domain.concert.dto.request;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ConcertCreateRequest {

	@NotNull(message = "공연장 ID를 입력해주세요")
	private Long stageId;

	@NotBlank(message = "제목을 입력해주세요")
	@Size(min = 3, max = 20, message = "제목은 3글자 이상 20글자 이하입니다")
	private String title;

	@NotBlank(message = "내용을 입력해주세요")
	private String description;

	@NotNull(message = "시작 시간을 입력해주세요")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime startTime;

	@NotNull(message = "예매 시작 시간을 입력해주세요")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime ticketOpen;

	@NotNull(message = "예매 마감 시간을 입력해주세요")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime ticketClose;
}
