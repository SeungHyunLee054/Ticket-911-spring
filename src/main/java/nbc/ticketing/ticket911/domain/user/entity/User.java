package nbc.ticketing.ticket911.domain.user.entity;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nbc.ticketing.ticket911.common.audit.BaseEntity;
import nbc.ticketing.ticket911.domain.user.constant.UserRole;

@Entity
@Getter
@Builder(toBuilder = true)
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@SQLRestriction("is_deleted = false ")
public class User extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true)
	private String email;

	private String password;

	@Column(unique = true)
	private String nickname;

	private int point;

	@ElementCollection
	@Builder.Default
	private Set<UserRole> roles = new HashSet<>();

	private boolean isDeleted;

	public void changeNickname(String nickname) {
		this.nickname = nickname;
	}

	public void changePassword(String password) {
		this.password = password;
	}

	public void withdraw() {
		this.isDeleted = true;
	}

	public void addPoint(int point) {
		this.point += point;
	}

	public void minusPoint(int point) {
		this.point -= point;
	}
}
