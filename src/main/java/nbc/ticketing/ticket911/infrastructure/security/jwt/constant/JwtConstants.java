package nbc.ticketing.ticket911.infrastructure.security.jwt.constant;

import java.util.List;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class JwtConstants {
	public static final String AUTH_HEADER = "Authorization";
	public static final String TOKEN_PREFIX = "Bearer ";
	public static final String KEY_ROLES = "roles";
	public static final List<String> PUBLIC_URLS = List.of(
		"/auth/signin", "/users/signup", "/swagger-ui", "/swagger-ui.html", "/v3/api-docs", "/swagger-resources",
		"/webjars", "/actuator", "/favicon.ico", "/metrics", "/api/v1"
	);
}
