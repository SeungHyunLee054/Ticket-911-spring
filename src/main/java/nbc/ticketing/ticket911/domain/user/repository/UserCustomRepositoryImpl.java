package nbc.ticketing.ticket911.domain.user.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import nbc.ticketing.ticket911.domain.user.entity.QUser;
import nbc.ticketing.ticket911.domain.user.entity.User;

@Repository
@RequiredArgsConstructor
public class UserCustomRepositoryImpl implements UserCustomRepository {
	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public Optional<User> findActiveUserById(Long userId) {
		QUser user = QUser.user;

		return Optional.ofNullable(jpaQueryFactory.selectFrom(user)
			.where(
				user.id.eq(userId),
				user.isDeleted.eq(false)
			).fetchOne());
	}
}
