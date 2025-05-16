package nbc.ticketing.ticket911.application.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import nbc.ticketing.ticket911.domain.auth.dto.request.SignInRequestDto;
import nbc.ticketing.ticket911.domain.auth.dto.response.SignInResponseDto;
import nbc.ticketing.ticket911.domain.auth.vo.AuthUser;
import nbc.ticketing.ticket911.domain.user.entity.User;
import nbc.ticketing.ticket911.domain.user.exception.UserException;
import nbc.ticketing.ticket911.domain.user.exception.code.UserExceptionCode;
import nbc.ticketing.ticket911.domain.user.repository.UserRepository;
import nbc.ticketing.ticket911.infrastructure.security.jwt.JwtUtil;

@Service
@RequiredArgsConstructor
public class AuthService {
	private final UserRepository userRepository;
	private final JwtUtil jwtUtil;
	private final PasswordEncoder passwordEncoder;

	public SignInResponseDto signIn(SignInRequestDto signInRequestDto) {
		User user = userRepository.findByEmail(signInRequestDto.getEmail())
			.orElseThrow(() -> new UserException(UserExceptionCode.USER_NOT_FOUND));

		if (!passwordEncoder.matches(signInRequestDto.getPassword(), user.getPassword())) {
			throw new UserException(UserExceptionCode.WRONG_PASSWORD);
		}

		AuthUser authUser = AuthUser.builder()
			.id(user.getId())
			.email(user.getEmail())
			.roles(user.getRoles())
			.build();

		String token = jwtUtil.generateAccessToken(authUser);

		return SignInResponseDto.builder()
			.accessToken(token)
			.build();
	}
}
