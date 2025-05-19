package nbc.ticketing.ticket911.application.auth.service;

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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import nbc.ticketing.ticket911.domain.auth.dto.request.SignInRequestDto;
import nbc.ticketing.ticket911.domain.auth.dto.response.SignInResponseDto;
import nbc.ticketing.ticket911.domain.user.constant.UserRole;
import nbc.ticketing.ticket911.domain.user.entity.User;
import nbc.ticketing.ticket911.domain.user.exception.UserException;
import nbc.ticketing.ticket911.domain.user.exception.code.UserExceptionCode;
import nbc.ticketing.ticket911.domain.user.repository.UserRepository;
import nbc.ticketing.ticket911.infrastructure.security.jwt.JwtUtil;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
	@Mock
	private UserRepository userRepository;

	@Mock
	private JwtUtil jwtUtil;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private AuthService authService;

	@Spy
	private User user;

	private static final String TOKEN = "testToken";

	private final SignInRequestDto signInRequestDto = new SignInRequestDto("test@test.com", "testPassword");

	@BeforeEach
	void setUp() {
		user = User.builder()
			.id(1L)
			.email("test@test.com")
			.password("testPassword")
			.nickname("test")
			.roles(Set.of(UserRole.ROLE_ADMIN))
			.point(0)
			.build();
	}

	@Nested
	@DisplayName("로그인 테스트")
	class SignInTest {
		@Test
		@DisplayName("로그인 성공")
		void success_signin() {
			// Given
			given(userRepository.findByEmail(anyString()))
				.willReturn(Optional.ofNullable(user));
			given(passwordEncoder.matches(anyString(), anyString()))
				.willReturn(true);
			given(jwtUtil.generateAccessToken(any()))
				.willReturn(TOKEN);

			// When
			SignInResponseDto responseDto = authService.signIn(signInRequestDto);

			// Then
			assertThat(responseDto)
				.isNotNull();
			assertThat(responseDto.getAccessToken())
				.isEqualTo(TOKEN);

		}

		@Test
		@DisplayName("로그인 실패 - 유저를 찾을 수 없음")
		void fail_signin_userNotFound() {
			// Given
			given(userRepository.findByEmail(anyString()))
				.willReturn(Optional.empty());

			// When
			UserException exception = assertThrows(UserException.class, () -> authService.signIn(signInRequestDto));

			// Then
			assertThat(exception.getErrorCode())
				.isEqualTo(UserExceptionCode.USER_NOT_FOUND);
			assertThat(exception.getMessage())
				.isEqualTo(UserExceptionCode.USER_NOT_FOUND.getMessage());
			assertThat(exception.getHttpStatus())
				.isEqualTo(UserExceptionCode.USER_NOT_FOUND.getStatus());

		}

		@Test
		@DisplayName("로그인 실패 - 비밀번호 불일치")
		void fail_signin_wrongPassword() {
			// Given
			given(userRepository.findByEmail(anyString()))
				.willReturn(Optional.ofNullable(user));
			given(passwordEncoder.matches(anyString(), anyString()))
				.willReturn(false);

			// When
			UserException exception = assertThrows(UserException.class, () -> authService.signIn(signInRequestDto));

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
	@DisplayName("로그아웃 테스트")
	class SignOutTest {
		@Test
		@DisplayName("로그아웃 성공")
		void success_signOut() {
			// Given
			SecurityContext context = SecurityContextHolder.createEmptyContext();
			Authentication authentication = new UsernamePasswordAuthenticationToken("test@test.com", "password");
			context.setAuthentication(authentication);
			SecurityContextHolder.setContext(context);

			// When
			authService.signOut();

			// Then
			assertThat(SecurityContextHolder.getContext().getAuthentication())
				.isNull();

		}
	}

}
