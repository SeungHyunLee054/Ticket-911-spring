package nbc.ticketing.ticket911.domain.user.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import nbc.ticketing.ticket911.domain.user.dto.request.SignupRequestDto;
import nbc.ticketing.ticket911.domain.user.dto.request.UpdateUserRequestDto;
import nbc.ticketing.ticket911.domain.user.dto.response.UserResponseDto;
import nbc.ticketing.ticket911.domain.user.entity.User;
import nbc.ticketing.ticket911.domain.user.service.UserDomainService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
	private final UserDomainService userDomainService;

	@Transactional
	public UserResponseDto signUp(SignupRequestDto signupRequestDto) {
		User savedUser = userDomainService.createUser(signupRequestDto);

		return UserResponseDto.from(savedUser);
	}

	public UserResponseDto getUser(Long userId) {
		User user = userDomainService.findActiveUserById(userId);

		return UserResponseDto.from(user);
	}

	@Transactional
	public UserResponseDto updateUser(Long userId, UpdateUserRequestDto updateUserRequestDto) {
		User user = userDomainService.updateUserNicknameOrPassword(userId, updateUserRequestDto);

		return UserResponseDto.from(user);
	}

	@Transactional
	public Long withdrawUser(Long userId) {
		return userDomainService.withdrawUser(userId);
	}

	@Transactional
	public UserResponseDto chargePoint(Long userId, int point) {
		User user = userDomainService.findActiveUserById(userId);
		User pointChargedUser = userDomainService.chargePoint(user, point);

		return UserResponseDto.from(pointChargedUser);
	}

}
