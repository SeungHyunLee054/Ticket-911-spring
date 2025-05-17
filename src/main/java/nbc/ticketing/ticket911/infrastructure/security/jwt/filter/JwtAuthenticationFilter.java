package nbc.ticketing.ticket911.infrastructure.security.jwt.filter;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import nbc.ticketing.ticket911.domain.auth.vo.AuthUser;
import nbc.ticketing.ticket911.domain.user.constant.UserRole;
import nbc.ticketing.ticket911.infrastructure.security.jwt.JwtUtil;
import nbc.ticketing.ticket911.infrastructure.security.jwt.constant.JwtConstants;
import nbc.ticketing.ticket911.infrastructure.security.jwt.filter.exception.JwtFilterException;
import nbc.ticketing.ticket911.infrastructure.security.jwt.filter.exception.code.JwtFilterExceptionCode;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final JwtUtil jwtUtil;
	private final CustomAuthenticationEntryPoint entryPoint;

	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
		@NonNull FilterChain filterChain) throws ServletException, IOException {
		String uri = request.getRequestURI();

		if (JwtConstants.PUBLIC_URLS.stream().anyMatch(uri::contains)) {
			filterChain.doFilter(request, response);
			return;
		}

		try {
			String accessHeader = request.getHeader(JwtConstants.AUTH_HEADER);
			if (accessHeader == null || accessHeader.isBlank()) {
				throw new JwtFilterException(JwtFilterExceptionCode.EMPTY_TOKEN);
			}

			String accessToken = resolveToken(accessHeader);

			if (jwtUtil.isTokenExpired(accessToken)) {
				throw new JwtFilterException(JwtFilterExceptionCode.TOKEN_EXPIRED);
			}

			Claims claims = jwtUtil.parseToken(accessToken);
			List<?> objects = claims.get("roles", List.class);
			Set<UserRole> roles = objects.stream()
				.map(String::valueOf)
				.map(UserRole::from)
				.collect(Collectors.toSet());
			AuthUser authUser = AuthUser.builder()
				.id(Long.valueOf(claims.getId()))
				.email(claims.getSubject())
				.roles(roles)
				.build();
			Authentication authentication = new UsernamePasswordAuthenticationToken(authUser, accessToken,
				authUser.getAuthorities());

			SecurityContextHolder.getContext().setAuthentication(authentication);

			filterChain.doFilter(request, response);
		} catch (JwtFilterException jwtFilterException) {
			SecurityContextHolder.clearContext();
			entryPoint.commence(request, response, jwtFilterException);
		}
	}

	private String resolveToken(String authorization) {
		if (!authorization.startsWith(JwtConstants.TOKEN_PREFIX)) {
			throw new JwtFilterException(JwtFilterExceptionCode.MALFORMED_JWT_REQUEST);
		}

		return authorization.substring(JwtConstants.TOKEN_PREFIX.length());
	}
}
