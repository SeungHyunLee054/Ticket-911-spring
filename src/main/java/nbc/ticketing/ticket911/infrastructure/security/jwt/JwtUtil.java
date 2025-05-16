package nbc.ticketing.ticket911.infrastructure.security.jwt;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import nbc.ticketing.ticket911.domain.auth.vo.AuthUser;
import nbc.ticketing.ticket911.infrastructure.security.jwt.constant.JwtConstants;
import nbc.ticketing.ticket911.infrastructure.security.jwt.constant.TokenExpiredConstant;
import nbc.ticketing.ticket911.infrastructure.security.jwt.exception.JwtTokenException;
import nbc.ticketing.ticket911.infrastructure.security.jwt.exception.code.JwtTokenExceptionCode;

@Component
public class JwtUtil {
	private final SecretKey secretKey;
	private final TokenExpiredConstant tokenExpiredConstant;

	public JwtUtil(@Value("${jwt.secret.key}") String secretKey, TokenExpiredConstant tokenExpiredConstant) {
		this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes());
		this.tokenExpiredConstant = tokenExpiredConstant;
	}

	public String generateAccessToken(AuthUser authUser) {
		Date now = new Date();

		return Jwts.builder()
			.subject(authUser.getEmail())
			.id(authUser.getId().toString())
			.claim(JwtConstants.KEY_ROLES, authUser.getRoles())
			.issuedAt(now)
			.expiration(tokenExpiredConstant.getAccessTokenExpiredDate(now))
			.signWith(secretKey, Jwts.SIG.HS256)
			.compact();
	}

	public boolean isTokenExpired(String token) {
		Claims claims = parseToken(token);
		return claims.getExpiration().before(new Date());
	}

	public Claims parseToken(String token) {
		try {
			return Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token)
				.getPayload();
		} catch (ExpiredJwtException expiredJwtException) {
			throw new JwtTokenException(JwtTokenExceptionCode.EXPIRED_JWT_TOKEN);
		} catch (MalformedJwtException malformedJwtException) {
			throw new JwtTokenException(JwtTokenExceptionCode.NOT_VALID_JWT_TOKEN);
		} catch (SignatureException signatureException) {
			throw new JwtTokenException(JwtTokenExceptionCode.NOT_VALID_SIGNATURE);
		} catch (UnsupportedJwtException unsupportedJwtException) {
			throw new JwtTokenException(JwtTokenExceptionCode.NOT_VALID_CONTENT);
		} catch (Exception e) {
			throw new JwtTokenException(JwtTokenExceptionCode.BAD_REQUEST);
		}
	}
}
