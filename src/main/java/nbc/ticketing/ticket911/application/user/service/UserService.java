package nbc.ticketing.ticket911.application.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;
import nbc.ticketing.ticket911.domain.user.dto.request.SignupRequestDto;
import nbc.ticketing.ticket911.domain.user.dto.request.UpdateUserRequestDto;
import nbc.ticketing.ticket911.domain.user.dto.response.UserResponseDto;
import nbc.ticketing.ticket911.domain.user.entity.User;
import nbc.ticketing.ticket911.domain.user.exception.UserException;
import nbc.ticketing.ticket911.domain.user.exception.code.UserExceptionCode;
import nbc.ticketing.ticket911.domain.user.repository.UserRepository;
import nbc.ticketing.ticket911.domain.user.service.UserDomainService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
	private final UserRepository userRepository;
	private final UserDomainService userDomainService;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	public UserResponseDto signUp(SignupRequestDto signupRequestDto) {
		userDomainService.validateSignup(
			userRepository.existsByEmail(signupRequestDto.getEmail()),
			checkNicknameExists(signupRequestDto.getNickname())
		);

		User user = userDomainService.createUser(
			signupRequestDto.getEmail(),
			passwordEncoder.encode(signupRequestDto.getPassword()),
			signupRequestDto.getNickname(),
			signupRequestDto.getRoles()
		);

		User savedUser = userRepository.save(user);

		return UserResponseDto.from(savedUser);
	}

	public UserResponseDto getUser(Long userId) {
		User user = findUserByUserIdOrElseThrow(userId);

		return UserResponseDto.from(user);
	}

	@Transactional
	public UserResponseDto updateUser(Long userId, UpdateUserRequestDto updateUserRequestDto) {
		User user = findUserByUserIdOrElseThrow(userId);

		userDomainService.validateWithdrawnUser(user);

		if (!StringUtils.hasText(updateUserRequestDto.getNickname())
			&& updateUserRequestDto.getPasswordUpdateForm() == null) {
			throw new UserException(UserExceptionCode.NOT_CHANGED);
		}

		if (StringUtils.hasText(updateUserRequestDto.getNickname())) {
			userDomainService.validateAndUpdateNickname(
				user,
				updateUserRequestDto.getNickname(),
				checkNicknameExists(updateUserRequestDto.getNickname())
			);
		}

		if (updateUserRequestDto.getPasswordUpdateForm() != null) {
			userDomainService.validateAndUpdatePassword(
				user,
				passwordEncoder.encode(updateUserRequestDto.getOldPassword()),
				passwordEncoder.encode(updateUserRequestDto.getNewPassword())
			);
		}

		return UserResponseDto.from(user);
	}

	@Transactional
	public Long withdrawUser(Long userId) {
		User user = findUserByUserIdOrElseThrow(userId);

		userDomainService.validateAndWithdrawUser(user);

		return user.getId();
	}

	@Transactional
	public UserResponseDto chargePoint(Long userId, int point) {
		User user = findUserByUserIdOrElseThrow(userId);

		userDomainService.validateWithdrawnUser(user);

		userDomainService.chargePoint(user, point);

		return UserResponseDto.from(user);
	}

	private User findUserByUserIdOrElseThrow(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new UserException(UserExceptionCode.USER_NOT_FOUND));
	}

	private boolean checkNicknameExists(String nickname) {
		return userRepository.existsByNickname(nickname);
	}

}
