package nbc.ticketing.ticket911.domain.auth.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignInResponseDto {
	private String accessToken;
}
