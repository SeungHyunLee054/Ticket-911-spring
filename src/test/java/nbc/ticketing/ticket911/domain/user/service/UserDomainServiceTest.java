package nbc.ticketing.ticket911.domain.user.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import nbc.ticketing.ticket911.domain.user.constant.UserRole;
import nbc.ticketing.ticket911.domain.user.entity.User;
import nbc.ticketing.ticket911.domain.user.exception.UserException;
import nbc.ticketing.ticket911.domain.user.exception.code.UserExceptionCode;

@ExtendWith(MockitoExtension.class)
class UserDomainServiceTest {
	@InjectMocks
	private UserDomainService userDomainService;

	@Spy
	private User user;

	@BeforeEach
	void setUp() {
		user = User.builder()
			.id(1L)
			.email("test@test.com")
			.password("encodedPassword")
			.nickname("test")
			.roles(Set.of(UserRole.ROLE_ADMIN))
			.point(0)
			.isDeleted(false)
			.build();
	}

	@Nested
	@DisplayName("회원가입 검증 테스트")
	class SignUpTest {
		@Test
		@DisplayName("회원가입 검증 실패 - 이미 존재하는 이메일")
		void fail_validateSignup_alreadyExistsEmail() {
			// Given
			boolean isEmailExist = true;
			boolean isNicknameExist = false;

			// When
			UserException exception = assertThrows(UserException.class,
				() -> userDomainService.validateSignup(isEmailExist, isNicknameExist));

			// Then
			assertThat(exception.getErrorCode())
				.isEqualTo(UserExceptionCode.ALREADY_EXISTS_EMAIL);
			assertThat(exception.getMessage())
				.isEqualTo(UserExceptionCode.ALREADY_EXISTS_EMAIL.getMessage());
			assertThat(exception.getHttpStatus())
				.isEqualTo(UserExceptionCode.ALREADY_EXISTS_EMAIL.getStatus());

		}

		@Test
		@DisplayName("회원가입 검증 실패 - 이미 존재하는 닉네임")
		void fail_validateSignup_alreadyExistsNickname() {
			// Given
			boolean isEmailExist = false;
			boolean isNicknameExist = true;

			// When
			UserException exception = assertThrows(UserException.class,
				() -> userDomainService.validateSignup(isEmailExist, isNicknameExist));

			// Then
			assertThat(exception.getErrorCode())
				.isEqualTo(UserExceptionCode.ALREADY_EXISTS_NICKNAME);
			assertThat(exception.getMessage())
				.isEqualTo(UserExceptionCode.ALREADY_EXISTS_NICKNAME.getMessage());
			assertThat(exception.getHttpStatus())
				.isEqualTo(UserExceptionCode.ALREADY_EXISTS_NICKNAME.getStatus());

		}
	}

	@Test
	@DisplayName("사용자 생성 테스트")
	void success_createUser() {
		// given
		String email = "test@test.com";
		String encodedPassword = "encodedPassword";
		String nickname = "testUser";
		Set<String> roles = Set.of("ROLE_USER");

		// when
		User user = userDomainService.createUser(email, encodedPassword, nickname, roles);

		// then
		assertThat(user.getEmail()).isEqualTo(email);
		assertThat(user.getPassword()).isEqualTo(encodedPassword);
		assertThat(user.getNickname()).isEqualTo(nickname);
		assertThat(user.getRoles()).containsExactly(UserRole.ROLE_USER);
		assertThat(user.getPoint()).isZero();
	}

	@Nested
	@DisplayName("닉네임 수정 검증 테스트")
	class ValidateAndUpdateNicknameTest {
		@Test
		@DisplayName("닉네임 수정 검증 성공")
		void validateAndUpdateNickname_Success() {
			// given
			String newNickname = "newNickname";
			boolean isNicknameExists = false;

			// when
			userDomainService.validateAndUpdateNickname(user, newNickname, isNicknameExists);

			// then
			assertThat(user.getNickname()).isEqualTo(newNickname);
		}

		@Test
		@DisplayName("닉네임 수정 검증 실패 - 이미 존재하는 닉네임")
		void fail_validateAndUpdateNickname_nicknameExists() {
			// given
			String newNickname = "newNickname";
			boolean isNicknameExists = true;

			// When
			UserException exception = assertThrows(UserException.class,
				() -> userDomainService.validateAndUpdateNickname(user, newNickname, isNicknameExists));

			// Then
			assertThat(exception.getErrorCode())
				.isEqualTo(UserExceptionCode.ALREADY_EXISTS_NICKNAME);
			assertThat(exception.getMessage())
				.isEqualTo(UserExceptionCode.ALREADY_EXISTS_NICKNAME.getMessage());
			assertThat(exception.getHttpStatus())
				.isEqualTo(UserExceptionCode.ALREADY_EXISTS_NICKNAME.getStatus());
		}

	}

	@Nested
	@DisplayName("비밀번호 수정 검증 테스트")
	class ValidateAndUpdatePasswordTest {
		@Test
		@DisplayName("비밀번호 정상 수정")
		void validateAndUpdatePassword_Success() {
			// given
			String newPassword = "newPassword123!";
			boolean isPasswordCorrect = true;

			// when
			userDomainService.validateAndUpdatePassword(user, newPassword, isPasswordCorrect);

			// then
			assertThat(user.getPassword()).isEqualTo(newPassword);
		}

		@Test
		@DisplayName("이전 비밀번호가 일치하지 않는 경우 예외 발생")
		void validateAndUpdatePassword_WrongPassword_ThrowsException() {
			// given
			String newPassword = "newPassword";
			boolean isPasswordCorrect = false;

			// When
			UserException exception = assertThrows(UserException.class,
				() -> userDomainService.validateAndUpdatePassword(user, newPassword, isPasswordCorrect));

			// Then
			assertThat(exception.getErrorCode())
				.isEqualTo(UserExceptionCode.WRONG_PASSWORD);
			assertThat(exception.getMessage())
				.isEqualTo(UserExceptionCode.WRONG_PASSWORD.getMessage());
			assertThat(exception.getHttpStatus())
				.isEqualTo(UserExceptionCode.WRONG_PASSWORD.getStatus());
		}

	}

	@Test
	@DisplayName("회원 탈퇴 실패 - 이미 탈퇴한 유저")
	void fail_validateWithdrawnUser() {
		// Given

		// When
		UserException exception = assertThrows(UserException.class,
			() -> userDomainService.validateWithdrawnUser(user.toBuilder()
				.isDeleted(true)
				.build()));

		// Then
		assertThat(exception.getErrorCode())
			.isEqualTo(UserExceptionCode.WITHDRAWN_USER);
		assertThat(exception.getMessage())
			.isEqualTo(UserExceptionCode.WITHDRAWN_USER.getMessage());
		assertThat(exception.getHttpStatus())
			.isEqualTo(UserExceptionCode.WITHDRAWN_USER.getStatus());

	}

	@Test
	@DisplayName("유저 검증 및 회원 탈퇴 성공")
	void success_validateAndWithdrawUser() {
		// Given

		// When
		userDomainService.validateAndWithdrawUser(user);

		// Then
		assertThat(user.isDeleted()).isTrue();

	}

	@Test
	@DisplayName("포인트 충전 성공")
	void success_chargePoint() {
		// Given
		int point = 100;
		int originalPoint = user.getPoint();

		// When
		userDomainService.chargePoint(user, point);

		// Then
		assertThat(user.getPoint())
			.isNotEqualTo(originalPoint)
			.isEqualTo(originalPoint + point);

	}

}