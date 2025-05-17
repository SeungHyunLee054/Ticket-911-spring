package nbc.ticketing.ticket911.domain.stage.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateStageRequestDto {
    @NotBlank(message = "공연장 이름은 필수 입력값 입니다.")
    private String stageName;
}
