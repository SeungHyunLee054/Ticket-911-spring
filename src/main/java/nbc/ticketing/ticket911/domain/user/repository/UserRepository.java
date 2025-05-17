package nbc.ticketing.ticket911.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import nbc.ticketing.ticket911.domain.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	boolean existsByEmail(String email);

	boolean existsByNickname(String nickname);

	@EntityGraph(attributePaths = {"roles"})
	Optional<User> findByEmail(String email);
}
