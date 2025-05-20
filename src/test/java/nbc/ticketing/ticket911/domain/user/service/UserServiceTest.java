package nbc.ticketing.ticket911.domain.user.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

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

import nbc.ticketing.ticket911.domain.user.application.UserService;
import nbc.ticketing.ticket911.domain.user.constant.UserRole;
import nbc.ticketing.ticket911.domain.user.dto.request.SignupRequestDto;
import nbc.ticketing.ticket911.domain.user.dto.request.UpdateUserRequestDto;
import nbc.ticketing.ticket911.domain.user.dto.response.UserResponseDto;
import nbc.ticketing.ticket911.domain.user.entity.User;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
	@Mock
	private UserDomainService userDomainService;

	@InjectMocks
	private UserService userService;

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
			given(userDomainService.createUser(any(SignupRequestDto.class)))
				.willReturn(user);

			// When
			UserResponseDto responseDto = userService.signUp(signupRequestDto);

			// Then
			verify(userDomainService, times(1)).createUser(any(SignupRequestDto.class));

			assertThat(responseDto)
				.isNotNull();
		}
	}

	@Nested
	@DisplayName("유저 조회 테스트")
	class GetUserTest {
		@Test
		@DisplayName("유저 조회 성공")
		void success_getUser() {
			// Given
			given(userDomainService.findActiveUserById(anyLong()))
				.willReturn(user);

			// When
			UserResponseDto responseDto = userService.getUser(1L);

			// Then
			verify(userDomainService, times(1)).findActiveUserById(anyLong());

			assertThat(responseDto)
				.isNotNull();
		}
	}

	@Nested
	@DisplayName("유저 수정 테스트")
	class UpdateUserTest {
		@Test
		@DisplayName("유저 수정 성공")
		void success_updateUser() {
			// Given
			given(userDomainService.updateUserNicknameOrPassword(anyLong(), any(UpdateUserRequestDto.class)))
				.willReturn(user);

			// When
			UserResponseDto responseDto = userService.updateUser(1L, updateUserRequestDto);

			// Then
			verify(userDomainService, times(1)).updateUserNicknameOrPassword(anyLong(),
				any(UpdateUserRequestDto.class));

			assertThat(responseDto)
				.isNotNull();
		}
	}

	@Nested
	@DisplayName("유저 탈퇴 테스트")
	class WithdrawUserTest {
		@Test
		@DisplayName("유저 탈퇴 성공")
		void success_updateUser() {
			// Given
			given(userDomainService.withdrawUser(anyLong()))
				.willReturn(user.getId());

			// When
			Long withdrawnUserId = userService.withdrawUser(1L);

			// Then
			verify(userDomainService, times(1)).withdrawUser(anyLong());

			assertThat(withdrawnUserId)
				.isNotNull();
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

			given(userDomainService.chargePoint(any(), anyInt()))
				.willReturn(user);

			// When
			UserResponseDto responseDto = userService.chargePoint(1L, point);

			// Then
			verify(userDomainService, times(1)).chargePoint(any(), anyInt());
			assertThat(responseDto)
				.isNotNull();
		}
	}

}
