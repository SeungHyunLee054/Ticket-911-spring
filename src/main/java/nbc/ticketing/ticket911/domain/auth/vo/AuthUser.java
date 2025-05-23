package nbc.ticketing.ticket911.domain.auth.vo;

import java.util.Collection;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import lombok.Builder;
import lombok.Getter;
import nbc.ticketing.ticket911.domain.user.constant.UserRole;

@Getter
@Builder
public class AuthUser {
	private Long id;
	private String email;
	private Set<UserRole> roles;

	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.roles.stream()
			.map(r -> new SimpleGrantedAuthority(r.name()))
			.toList();
	}

	public static AuthUser of(Long id, String email, Set<UserRole> roles) {
		return new AuthUser(id, email, roles);
	}
}
