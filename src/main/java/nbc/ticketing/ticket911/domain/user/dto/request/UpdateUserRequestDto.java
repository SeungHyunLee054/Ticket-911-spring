package nbc.ticketing.ticket911.domain.user.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class UpdateUserRequestDto {
	private String nickname;

	@Valid
	private PasswordUpdateForm passwordUpdateForm;

	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class PasswordUpdateForm {
		@NotBlank(message = "비밀번호는 필수 입력값이며 공백이 아니어야 합니다.")
		@Size(min = 8, message = "비밀번호는 8글자 이상이어야 합니다.")
		private String oldPassword;

		@NotBlank(message = "비밀번호는 필수 입력값이며 공백이 아니어야 합니다.")
		@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_\\-+={\\[\\]}:;\"'<,>.?/])\\S{8,}$",
			message = "비밀번호는 대소문자, 숫자, 특수문자를 각각 최소 1자 이상 포함하며 8글자 이상이어야 합니다.")
		private String newPassword;
	}

	public String getNewPassword() {
		return passwordUpdateForm.getNewPassword();
	}

	public String getOldPassword() {
		return passwordUpdateForm.getOldPassword();
	}
}
