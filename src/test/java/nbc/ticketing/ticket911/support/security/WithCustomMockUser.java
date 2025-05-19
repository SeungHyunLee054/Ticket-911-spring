package nbc.ticketing.ticket911.support.security;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.test.context.support.WithSecurityContext;

import nbc.ticketing.ticket911.domain.user.constant.UserRole;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithCustomMockUserSecurityContextFactory.class)
public @interface WithCustomMockUser {
	long id() default 1L;

	String email() default "test@test";

	UserRole role() default UserRole.ROLE_USER;
}
