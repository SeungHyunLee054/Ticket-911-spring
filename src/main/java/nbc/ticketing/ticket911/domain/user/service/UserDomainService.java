package nbc.ticketing.ticket911.domain.user.service;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import nbc.ticketing.ticket911.domain.user.constant.UserRole;
import nbc.ticketing.ticket911.domain.user.entity.User;
import nbc.ticketing.ticket911.domain.user.exception.UserException;
import nbc.ticketing.ticket911.domain.user.exception.code.UserExceptionCode;

@Component
public class UserDomainService {
	public void validateSignup(boolean isEmailExists, boolean isNicknameExists) {
		if (isEmailExists) {
			throw new UserException(UserExceptionCode.ALREADY_EXISTS_EMAIL);
		}

		if (isNicknameExists) {
			throw new UserException(UserExceptionCode.ALREADY_EXISTS_NICKNAME);
		}
	}

	public User createUser(String email, String encodedPassword, String nickname, Set<String> roles) {
		return User.builder()
			.email(email)
			.password(encodedPassword)
			.nickname(nickname)
			.point(0)
			.roles(roles.stream()
				.map(UserRole::from)
				.collect(Collectors.toSet()))
			.build();
	}

	public void validateAndUpdateNickname(User user, String newNickname, boolean isNicknameExists) {
		if (isNicknameExists) {
			throw new UserException(UserExceptionCode.ALREADY_EXISTS_NICKNAME);
		}

		user.changeNickname(newNickname);
	}

	public void validateAndUpdatePassword(User user, String newEncodedPassword, boolean isPasswordCorrect) {
		if (!isPasswordCorrect) {
			throw new UserException(UserExceptionCode.WRONG_PASSWORD);
		}

		user.changePassword(newEncodedPassword);
	}

	public void validateWithdrawnUser(User user) {
		if (user.isDeleted()) {
			throw new UserException(UserExceptionCode.WITHDRAWN_USER);
		}
	}

	public void validateAndWithdrawUser(User user) {
		validateWithdrawnUser(user);

		user.withdraw();
	}

	public void chargePoint(User user, int point) {
		user.addPoint(point);
	}

}
