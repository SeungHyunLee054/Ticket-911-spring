package nbc.ticketing.ticket911.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import nbc.ticketing.ticket911.domain.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
