package nbc.ticketing.ticket911.infrastructure.security.jwt.filter;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nbc.ticketing.ticket911.common.response.CommonResponse;
import nbc.ticketing.ticket911.infrastructure.security.jwt.filter.exception.JwtFilterException;

@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
	private final ObjectMapper objectMapper;

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException authException) throws IOException {
		if (authException instanceof JwtFilterException jwtFilterException) {
			log.error("exception : {}", jwtFilterException.getMessage(), jwtFilterException);
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			objectMapper.writeValue(response.getWriter(),
				CommonResponse.of(false, jwtFilterException.getHttpStatus().value(),
					jwtFilterException.getMessage()));

			return;
		}

		log.error("exception : {}", authException.getMessage(), authException);
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		objectMapper.writeValue(response.getWriter(),
			CommonResponse.of(false, HttpStatus.UNAUTHORIZED.value(), authException.getMessage()));
	}
}
