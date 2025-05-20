package nbc.ticketing.ticket911.domain.user.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import nbc.ticketing.ticket911.domain.user.constant.UserRole;
import nbc.ticketing.ticket911.domain.user.dto.request.SignupRequestDto;
import nbc.ticketing.ticket911.domain.user.dto.request.UpdateUserRequestDto;
import nbc.ticketing.ticket911.domain.user.entity.User;
import nbc.ticketing.ticket911.domain.user.exception.UserException;
import nbc.ticketing.ticket911.domain.user.exception.code.UserExceptionCode;
import nbc.ticketing.ticket911.domain.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserDomainServiceTest {
	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private UserDomainService userDomainService;

	@Spy
	private User user;

	private final SignupRequestDto signupRequestDto = new SignupRequestDto("test@test.com", "password123!",
		"test", Set.of("ROLE_USER"));

	private final UpdateUserRequestDto updateUserRequestDto = new UpdateUserRequestDto("newTest",
		new UpdateUserRequestDto.PasswordUpdateForm("oldPassword", "newPassword"));

	@BeforeEach
	void setUp() {
		user = User.builder()
			.id(1L)
			.email("test@test.com")
			.password("encodedPassword")
			.nickname("test")
			.roles(Set.of(UserRole.ROLE_ADMIN))
			.point(100)
			.isDeleted(false)
			.build();
	}

	@Nested
	@DisplayName("유저 생성 테스트")
	class CreateUserTest {
		@Test
		@DisplayName("유저 생성 성공")
		void success_createUser() {
			// Given
			given(userRepository.existsByEmail(anyString()))
				.willReturn(false);
			given(userRepository.existsByNickname(anyString()))
				.willReturn(false);
			given(userRepository.save(any(User.class)))
				.willReturn(user);

			// When
			User savedUser = userDomainService.createUser(signupRequestDto);

			// Then
			verify(userRepository, times(1)).existsByEmail(anyString());
			verify(userRepository, times(1)).existsByNickname(anyString());
			verify(userRepository, times(1)).save(any(User.class));

			assertThat(savedUser)
				.isNotNull();
			assertThat(savedUser.getEmail())
				.isEqualTo(signupRequestDto.getEmail());
			assertThat(savedUser.getPassword())
				.isEqualTo("encodedPassword");
			assertThat(savedUser.getNickname())
				.isEqualTo(signupRequestDto.getNickname());
		}

		@Test
		@DisplayName("유저 생성 실패 - 이미 존재하는 이메일")
		void fail_createUser_alreadyExistsEmail() {
			// Given
			given(userRepository.existsByEmail(anyString()))
				.willReturn(true);

			// When
			UserException exception = assertThrows(UserException.class,
				() -> userDomainService.createUser(signupRequestDto));

			// Then
			assertThat(exception.getErrorCode())
				.isEqualTo(UserExceptionCode.ALREADY_EXISTS_EMAIL);
			assertThat(exception.getMessage())
				.isEqualTo(UserExceptionCode.ALREADY_EXISTS_EMAIL.getMessage());
			assertThat(exception.getHttpStatus())
				.isEqualTo(UserExceptionCode.ALREADY_EXISTS_EMAIL.getStatus());

		}

		@Test
		@DisplayName("유저 생성 실패 - 이미 존재하는 닉네임")
		void fail_createUser_alreadyExistsNickname() {
			// Given
			given(userRepository.existsByEmail(anyString()))
				.willReturn(false);
			given(userRepository.existsByNickname(anyString()))
				.willReturn(true);

			// When
			UserException exception = assertThrows(UserException.class,
				() -> userDomainService.createUser(signupRequestDto));

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
	@DisplayName("유저 id로 조회 테스트")
	class FindUserByUserIdOrElseThrowTest {
		@Test
		@DisplayName("유저 id로 조회 성공")
		void success_findUserByUserIdOrElseThrow() {
			// Given
			given(userRepository.findActiveUserById(anyLong()))
				.willReturn(Optional.of(user));

			// When
			User findedUser = userDomainService.findActiveUserById(1L);

			// Then
			verify(userRepository, times(1)).findActiveUserById(anyLong());

			assertThat(findedUser)
				.isNotNull();
			assertThat(findedUser.getId())
				.isEqualTo(user.getId());
		}

		@Test
		@DisplayName("유저 id로 조회 실패 - 유저를 찾을 수 없음")
		void fail_findUserByUserIdOrElseThrow_userNotFound() {
			// Given
			given(userRepository.findActiveUserById(anyLong()))
				.willReturn(Optional.empty());

			// When
			UserException exception = assertThrows(UserException.class,
				() -> userDomainService.findActiveUserById(1L));

			// Then
			assertThat(exception.getErrorCode())
				.isEqualTo(UserExceptionCode.USER_NOT_FOUND);
			assertThat(exception.getMessage())
				.isEqualTo(UserExceptionCode.USER_NOT_FOUND.getMessage());
			assertThat(exception.getHttpStatus())
				.isEqualTo(UserExceptionCode.USER_NOT_FOUND.getStatus());
		}
	}

	@Nested
	@DisplayName("유저 수정 테스트")
	class UpdateUserNicknameOrPasswordTest {
		@Test
		@DisplayName("유저 수정 성공")
		void success_updateUserNicknameOrPassword() {
			// Given
			given(userRepository.findActiveUserById(anyLong()))
				.willReturn(Optional.of(user));
			given(passwordEncoder.matches(anyString(), anyString()))
				.willReturn(true);

			// When
			User updatedUser = userDomainService.updateUserNicknameOrPassword(1L, updateUserRequestDto);

			// Then
			verify(userRepository, times(1)).findActiveUserById(anyLong());

			assertThat(updatedUser)
				.isNotNull();
		}

		@Test
		@DisplayName("유저 수정 실패 - 수정 사항이 존재하지 않음")
		void fail_updateUserNicknameOrPassword_notChanged() {
			// Given
			given(userRepository.findActiveUserById(anyLong()))
				.willReturn(Optional.of(user));

			// When
			UserException exception = assertThrows(UserException.class,
				() -> userDomainService.updateUserNicknameOrPassword(1L, new UpdateUserRequestDto(null, null)));

			// Then
			assertThat(exception.getErrorCode())
				.isEqualTo(UserExceptionCode.NOT_CHANGED);
			assertThat(exception.getMessage())
				.isEqualTo(UserExceptionCode.NOT_CHANGED.getMessage());
			assertThat(exception.getHttpStatus())
				.isEqualTo(UserExceptionCode.NOT_CHANGED.getStatus());
		}
	}

	@Nested
	@DisplayName("닉네임 수정 테스트")
	class ChangeNicknameIfPresentTest {
		@Test
		@DisplayName("닉네임 수정 성공")
		void success_changeNicknameIfPresent() {
			// given
			String originalNickname = user.getNickname();
			String newNickname = "newNickname";

			// when
			userDomainService.changeNicknameIfPresent(user, newNickname);

			// then
			assertThat(user.getNickname())
				.isNotEqualTo(originalNickname)
				.isEqualTo(newNickname);
		}

		@Test
		@DisplayName("닉네임이 없을 때 변경되지 않는다.")
		void notChangeWhenNicknameIsNull() {
			// Given
			String originalNickname = user.getNickname();

			// When
			userDomainService.changeNicknameIfPresent(user, null);

			// Then
			assertThat(user.getNickname())
				.isEqualTo(originalNickname);
		}

		@Test
		@DisplayName("닉네임 수정 실패 - 이미 존재하는 닉네임")
		void fail_changeNicknameIfPresent_alreadyExistsNickname() {
			// Given
			given(userRepository.existsByNickname(anyString()))
				.willReturn(true);

			String newNickname = "newNickname";

			// When
			UserException exception = assertThrows(UserException.class,
				() -> userDomainService.changeNicknameIfPresent(user, newNickname));

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
	@DisplayName("비밀번호 수정 테스트")
	class ChangePasswordIfPresentTest {
		@Test
		@DisplayName("비밀번호 수정 성공")
		void success_changePasswordIfPresent() {
			// given
			given(passwordEncoder.matches(anyString(), anyString()))
				.willReturn(true);
			given(passwordEncoder.encode(anyString()))
				.willReturn("encodedNewPassword");

			String originalPassword = user.getPassword();

			// when
			userDomainService.changePasswordIfPresent(user, updateUserRequestDto.getPasswordUpdateForm());

			// then
			verify(passwordEncoder, times(1)).encode(anyString());
			verify(passwordEncoder, times(1)).matches(anyString(), anyString());

			assertThat(user.getPassword())
				.isNotEqualTo(originalPassword)
				.isEqualTo("encodedNewPassword");
		}

		@Test
		@DisplayName("비밀번호 변경 폼이 없을 때 변경되지 않는다.")
		void notChangeWhenPasswordFormIsNull() {
			// Given
			String originalPassword = user.getPassword();

			// When
			userDomainService.changePasswordIfPresent(user, null);

			// Then
			assertThat(user.getPassword())
				.isEqualTo(originalPassword);
		}

		@Test
		@DisplayName("비밀번호 수정 실패 - 비밀번호 오류")
		void fail_changePasswordIfPresent_wrongPassword() {
			// given
			given(passwordEncoder.matches(anyString(), anyString()))
				.willReturn(false);

			// When
			UserException exception = assertThrows(UserException.class,
				() -> userDomainService.changePasswordIfPresent(user, updateUserRequestDto.getPasswordUpdateForm()));

			// Then
			assertThat(exception.getErrorCode())
				.isEqualTo(UserExceptionCode.WRONG_PASSWORD);
			assertThat(exception.getMessage())
				.isEqualTo(UserExceptionCode.WRONG_PASSWORD.getMessage());
			assertThat(exception.getHttpStatus())
				.isEqualTo(UserExceptionCode.WRONG_PASSWORD.getStatus());
		}
	}

	@Nested
	@DisplayName("회원 탈퇴 테스트")
	class WithdrawUserTest {
		@Test
		@DisplayName("회원 탈퇴 성공")
		void success_withdrawUser() {
			// Given
			given(userRepository.findActiveUserById(anyLong()))
				.willReturn(Optional.of(user));

			// When
			Long withdrawnUserId = userDomainService.withdrawUser(1L);

			// Then
			verify(userRepository, times(1)).findActiveUserById(anyLong());

			assertThat(withdrawnUserId)
				.isEqualTo(1L);
			assertThat(user.isDeleted())
				.isTrue();
		}

		@Test
		@DisplayName("회원 탈퇴 실패 - 이미 탈퇴한 유저")
		void fail_withdrawUser_userNotFound() {
			// Given
			given(userRepository.findActiveUserById(anyLong()))
				.willReturn(Optional.empty());

			// When
			UserException exception = assertThrows(UserException.class,
				() -> userDomainService.withdrawUser(1L));

			// Then
			assertThat(exception.getErrorCode())
				.isEqualTo(UserExceptionCode.USER_NOT_FOUND);
			assertThat(exception.getMessage())
				.isEqualTo(UserExceptionCode.USER_NOT_FOUND.getMessage());
			assertThat(exception.getHttpStatus())
				.isEqualTo(UserExceptionCode.USER_NOT_FOUND.getStatus());

		}
	}

	@Nested
	@DisplayName("포인트 충전 테스트")
	class ChargePointTest {
		@Test
		@DisplayName("포인트 충전 성공")
		void success_chargePoint() {
			// Given
			int point = 100;
			int originalPoint = user.getPoint();

			// When
			User changedUser = userDomainService.chargePoint(user, point);

			// Then
			assertThat(user.getPoint())
				.isNotEqualTo(originalPoint)
				.isEqualTo(changedUser.getPoint());
		}
	}

	@Nested
	@DisplayName("포인트 차감 테스트")
	class MinusPointTest {
		@Test
		@DisplayName("포인트 차감 성공")
		void success_minusPoint() {
			// Given
			int point = 100;
			int originalPoint = user.getPoint();

			// When
			User changedUser = userDomainService.minusPoint(user, point);

			// Then
			assertThat(user.getPoint())
				.isNotEqualTo(originalPoint)
				.isEqualTo(changedUser.getPoint());
		}

		@Test
		@DisplayName("포인트 차감 실패 - 포인트 부족")
		void fail_minusPoint_notEnoughPoint() {
			// Given
			int point = 1000;

			// When
			UserException exception = assertThrows(UserException.class,
				() -> userDomainService.minusPoint(user, point));

			// Then
			assertThat(exception.getErrorCode())
				.isEqualTo(UserExceptionCode.NOT_ENOUGH_POINT);
			assertThat(exception.getMessage())
				.isEqualTo(UserExceptionCode.NOT_ENOUGH_POINT.getMessage());
			assertThat(exception.getHttpStatus())
				.isEqualTo(UserExceptionCode.NOT_ENOUGH_POINT.getStatus());
		}
	}

	@Nested
	@DisplayName("유저 이메일로 조회 테스트")
	class FindUserByEmailOrElseThrowTest {
		@Test
		@DisplayName("유저 이메일로 조회 성공")
		void success_findUserByEmailOrElseThrow() {
			// Given
			given(userRepository.findByEmail(anyString()))
				.willReturn(Optional.of(user));

			// When
			User findedUser = userDomainService.findUserByEmailOrElseThrow("test@test.com");

			// Then
			verify(userRepository, times(1)).findByEmail(anyString());

			assertThat(findedUser)
				.isNotNull();
			assertThat(findedUser.getEmail())
				.isEqualTo(user.getEmail());
		}

		@Test
		@DisplayName("유저 이메일로 조회 실패 - 유저를 찾을 수 없음")
		void fail_findUserByEmailOrElseThrow_userNotFound() {
			// Given
			given(userRepository.findByEmail(anyString()))
				.willReturn(Optional.empty());

			// When
			UserException exception = assertThrows(UserException.class,
				() -> userDomainService.findUserByEmailOrElseThrow("test@test"));

			// Then
			assertThat(exception.getErrorCode())
				.isEqualTo(UserExceptionCode.USER_NOT_FOUND);
			assertThat(exception.getMessage())
				.isEqualTo(UserExceptionCode.USER_NOT_FOUND.getMessage());
			assertThat(exception.getHttpStatus())
				.isEqualTo(UserExceptionCode.USER_NOT_FOUND.getStatus());
		}
	}
}
