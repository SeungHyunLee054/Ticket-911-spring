package nbc.ticketing.ticket911.domain.user.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import nbc.ticketing.ticket911.domain.user.entity.User;

@Getter
@Builder
public class UserResponseDto {
	private Long id;
	private String email;
	private String nickname;
	private int point;
	private LocalDateTime createdAt;
	private LocalDateTime modifiedAt;

	public static UserResponseDto from(User user) {
		return UserResponseDto.builder()
			.id(user.getId())
			.nickname(user.getNickname())
			.email(user.getEmail())
			.point(user.getPoint())
			.createdAt(user.getCreatedAt())
			.modifiedAt(user.getModifiedAt())
			.build();
	}
}
