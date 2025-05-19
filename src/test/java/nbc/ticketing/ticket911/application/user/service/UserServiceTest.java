package nbc.ticketing.ticket911.application.user.service;

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
import nbc.ticketing.ticket911.domain.user.dto.response.UserResponseDto;
import nbc.ticketing.ticket911.domain.user.entity.User;
import nbc.ticketing.ticket911.domain.user.exception.UserException;
import nbc.ticketing.ticket911.domain.user.exception.code.UserExceptionCode;
import nbc.ticketing.ticket911.domain.user.repository.UserRepository;
import nbc.ticketing.ticket911.domain.user.service.UserDomainService;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private UserService userService;

	@Spy
	private UserDomainService userDomainService;

	@Spy
	private User user;

	private final SignupRequestDto signupRequestDto = new SignupRequestDto("test@test.com", "password123!",
		"test", Set.of("ROLE_USER"));

	private final UpdateUserRequestDto updateUserRequestDto = new UpdateUserRequestDto("newTest",
		new UpdateUserRequestDto.PasswordUpdateForm("oldPassword", "newPassword"));

	@BeforeEach
	void setUp() {
		userDomainService = new UserDomainService();
		userService = new UserService(userRepository, userDomainService, passwordEncoder);

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
	@DisplayName("회원가입 테스트")
	class SignUpTest {
		@Test
		@DisplayName("회원가입 성공")
		void success_signUp() {
			// Given
			given(userRepository.existsByEmail(anyString()))
				.willReturn(false);
			given(userRepository.existsByNickname(anyString()))
				.willReturn(false);
			given(passwordEncoder.encode(anyString()))
				.willReturn("testPassword");
			given(userRepository.save(any()))
				.willReturn(user);

			// When
			UserResponseDto responseDto = userService.signUp(signupRequestDto);

			// Then
			assertThat(responseDto.getEmail()).isEqualTo(signupRequestDto.getEmail());
			assertThat(responseDto.getNickname()).isEqualTo(signupRequestDto.getNickname());

		}

	}

	@Nested
	@DisplayName("유저 조회 테스트")
	class GetUserTest {
		@Test
		@DisplayName("유저 조회 성공")
		void success_getUser() {
			// Given
			given(userRepository.findById(anyLong()))
				.willReturn(Optional.of(user));

			// When
			UserResponseDto responseDto = userService.getUser(1L);

			// Then
			assertThat(responseDto.getId()).isEqualTo(1L);
			assertThat(responseDto.getEmail()).isEqualTo("test@test.com");
			assertThat(responseDto.getNickname()).isEqualTo("test");

		}

		@Test
		@DisplayName("유저 조회 실패 - 유저를 찾을 수 없음")
		void fail_getUser_userNotFound() {
			// Given
			given(userRepository.findById(anyLong()))
				.willReturn(Optional.empty());

			// When
			UserException exception = assertThrows(UserException.class, () -> userService.getUser(2L));

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
	class UpdateUserTest {
		@Test
		@DisplayName("유저 수정 성공")
		void success_updateUser() {
			// Given
			given(userRepository.findById(anyLong()))
				.willReturn(Optional.of(user));
			given(userRepository.existsByNickname(anyString()))
				.willReturn(false);
			given(passwordEncoder.encode(updateUserRequestDto.getPasswordUpdateForm().getOldPassword()))
				.willReturn("encodedPassword");
			given(passwordEncoder.encode(updateUserRequestDto.getPasswordUpdateForm().getNewPassword()))
				.willReturn("newEncodedPassword");

			String originalNickname = user.getNickname();
			String originalPassword = user.getPassword();

			// When
			UserResponseDto responseDto = userService.updateUser(1L, updateUserRequestDto);

			// Then
			assertThat(user.getNickname())
				.isNotEqualTo(originalNickname)
				.isEqualTo(updateUserRequestDto.getNickname());

			assertThat(user.getPassword())
				.isNotEqualTo(originalPassword)
				.isEqualTo("newEncodedPassword");

			assertThat(responseDto.getNickname()).isEqualTo(updateUserRequestDto.getNickname());

		}

		@Test
		@DisplayName("유저 수정 실패 - 유저를 찾을 수 없음")
		void fail_updateUser_userNotFound() {
			// Given
			given(userRepository.findById(anyLong()))
				.willReturn(Optional.empty());

			// When
			UserException exception = assertThrows(UserException.class,
				() -> userService.updateUser(1L, updateUserRequestDto));

			// Then
			assertThat(exception.getErrorCode())
				.isEqualTo(UserExceptionCode.USER_NOT_FOUND);
			assertThat(exception.getMessage())
				.isEqualTo(UserExceptionCode.USER_NOT_FOUND.getMessage());
			assertThat(exception.getHttpStatus())
				.isEqualTo(UserExceptionCode.USER_NOT_FOUND.getStatus());

		}

		@Test
		@DisplayName("유저 수정 실패 - 이미 탈퇴한 유저")
		void fail_updateUser_withdrawnUser() {
			// Given
			given(userRepository.findById(anyLong()))
				.willReturn(Optional.of(user.toBuilder()
					.isDeleted(true)
					.build()));

			// When
			UserException exception = assertThrows(UserException.class,
				() -> userService.updateUser(1L, updateUserRequestDto));

			// Then
			assertThat(exception.getErrorCode())
				.isEqualTo(UserExceptionCode.WITHDRAWN_USER);
			assertThat(exception.getMessage())
				.isEqualTo(UserExceptionCode.WITHDRAWN_USER.getMessage());
			assertThat(exception.getHttpStatus())
				.isEqualTo(UserExceptionCode.WITHDRAWN_USER.getStatus());

		}

		@Test
		@DisplayName("유저 수정 실패 - 수정할 내용이 없음")
		void fail_updateUser_notChanged() {
			// Given
			given(userRepository.findById(anyLong()))
				.willReturn(Optional.of(user));

			// When
			UserException exception = assertThrows(UserException.class,
				() -> userService.updateUser(1L, new UpdateUserRequestDto(null, null)));

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
	@DisplayName("포인트 충전 테스트")
	class ChargePoint {
		@Test
		@DisplayName("포인트 충전 성공")
		void success_chargePoint() {
			// Given
			int point = 100;

			given(userRepository.findById(anyLong()))
				.willReturn(Optional.of(user));

			int originalPoint = user.getPoint();

			// When
			UserResponseDto responseDto = userService.chargePoint(1L, point);

			// Then
			assertThat(user.getPoint())
				.isNotEqualTo(originalPoint)
				.isEqualTo(originalPoint + point);
			assertThat(responseDto.getPoint()).isEqualTo(100);

		}

		@Test
		@DisplayName("포인트 충전 실패 - 유저를 찾을 수 없음")
		void fail_chargePoint_userNotFound() {
			// Given
			int point = 100;

			given(userRepository.findById(anyLong()))
				.willReturn(Optional.empty());

			// When
			UserException exception = assertThrows(UserException.class,
				() -> userService.chargePoint(1L, point));

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
