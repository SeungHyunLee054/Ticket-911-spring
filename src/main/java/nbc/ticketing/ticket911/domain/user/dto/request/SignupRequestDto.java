package nbc.ticketing.ticket911.domain.user.dto.request;

import java.util.Set;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignupRequestDto {
	@NotBlank(message = "이메일은 필수 입력값이며 공백이 아니어야 합니다.")
	@Email(message = "이메일 형식이 잘못되었습니다.")
	private String email;

	@NotBlank(message = "비밀번호는 필수 입력값이며 공백이 아니어야 합니다.")
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_\\-+={\\[\\]}:;\"'<,>.?/])\\S{8,}$",
		message = "비밀번호는 대소문자, 숫자, 특수문자를 각각 최소 1자 이상 포함하며 8글자 이상이어야 합니다.")
	private String password;

	@NotBlank(message = "이름은 필수 입력값이며 공백이 아니어야 합니다.")
	private String nickname;

	@NotNull(message = "권한은 필수 입력값이며 공백이 아니어야 합니다.")
	private Set<String> roles;
}
