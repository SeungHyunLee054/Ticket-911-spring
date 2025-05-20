package nbc.ticketing.ticket911.domain.auth.application;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import nbc.ticketing.ticket911.domain.auth.dto.request.SignInRequestDto;
import nbc.ticketing.ticket911.domain.auth.dto.response.SignInResponseDto;
import nbc.ticketing.ticket911.domain.auth.vo.AuthUser;
import nbc.ticketing.ticket911.domain.user.entity.User;
import nbc.ticketing.ticket911.domain.user.service.UserDomainService;
import nbc.ticketing.ticket911.infrastructure.security.jwt.JwtUtil;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
	private final UserDomainService userDomainService;
	private final JwtUtil jwtUtil;

	public SignInResponseDto signIn(SignInRequestDto signInRequestDto) {
		User user = userDomainService.findUserByEmailOrElseThrow(signInRequestDto.getEmail());

		userDomainService.validatePassword(signInRequestDto.getPassword(), user.getPassword());

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

	public void signOut() {
		SecurityContextHolder.clearContext();
	}
}
