package nbc.ticketing.ticket911.domain.user.constant;

import java.util.Arrays;

import nbc.ticketing.ticket911.domain.user.exception.UserException;
import nbc.ticketing.ticket911.domain.user.exception.code.UserExceptionCode;

public enum UserRole {
	ROLE_USER, ROLE_ADMIN;

	public static UserRole from(String role) {
		return Arrays.stream(UserRole.values())
			.filter(r -> r.name().equalsIgnoreCase(role))
			.findFirst()
			.orElseThrow(() -> new UserException(UserExceptionCode.WRONG_ROLES));
	}
}
