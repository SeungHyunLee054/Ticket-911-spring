package nbc.ticketing.ticket911.application.user.service;

import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import nbc.ticketing.ticket911.domain.user.constant.UserRole;
import nbc.ticketing.ticket911.domain.user.dto.request.SignupRequestDto;
import nbc.ticketing.ticket911.domain.user.dto.response.UserResponseDto;
import nbc.ticketing.ticket911.domain.user.entity.User;
import nbc.ticketing.ticket911.domain.user.exception.UserException;
import nbc.ticketing.ticket911.domain.user.exception.code.UserExceptionCode;
import nbc.ticketing.ticket911.domain.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	public UserResponseDto signUp(SignupRequestDto signupRequestDto) {
		if (userRepository.existsByEmail(signupRequestDto.getEmail())) {
			throw new UserException(UserExceptionCode.ALREADY_EXISTS_EMAIL);
		}

		if (userRepository.existsByNickname(signupRequestDto.getNickname())) {
			throw new UserException(UserExceptionCode.ALREADY_EXISTS_NICKNAME);
		}

		User user = User.builder()
			.email(signupRequestDto.getEmail())
			.password(passwordEncoder.encode(signupRequestDto.getPassword()))
			.nickname(signupRequestDto.getNickname())
			.point(0)
			.roles(signupRequestDto.getRoles().stream()
				.map(UserRole::from)
				.collect(Collectors.toSet()))
			.build();

		User savedUser = userRepository.save(user);

		return UserResponseDto.from(savedUser);
	}

}
