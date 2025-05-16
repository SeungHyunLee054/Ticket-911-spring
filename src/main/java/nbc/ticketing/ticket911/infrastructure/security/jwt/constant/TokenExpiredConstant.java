package nbc.ticketing.ticket911.infrastructure.security.jwt.constant;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TokenExpiredConstant {
	private static final long MILLISECOND = 1000L;

	@Value("${jwt.token.access.second}")
	private long accessSecond;

	@Value("${jwt.token.access.minute}")
	private long accessMinute;

	@Value("${jwt.token.access.hour}")
	private long accessHour;

	public long getAccessTokenExpiredTime() {
		return accessHour * accessMinute * accessSecond * MILLISECOND;
	}

	public Date getAccessTokenExpiredDate(Date date) {
		return new Date(date.getTime() + getAccessTokenExpiredTime());
	}
}
