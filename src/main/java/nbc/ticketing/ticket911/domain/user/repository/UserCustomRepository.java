package nbc.ticketing.ticket911.domain.user.repository;

import java.util.Optional;

import nbc.ticketing.ticket911.domain.user.entity.User;

public interface UserCustomRepository {
	Optional<User> findActiveUserById(Long userId);
}
