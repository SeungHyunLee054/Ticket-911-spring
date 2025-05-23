package nbc.ticketing.ticket911.infrastructure.lettuce;

import java.util.List;
import java.util.UUID;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class LettuceMultiLock {

	private final List<String> keys;
	private final long expireMillis;
	private final LettuceLockManager lockManager;

	private final String lockValue = UUID.randomUUID().toString();

	public boolean tryLockAll() {
		for (String key : keys) {
			boolean locked = lockManager.tryLock(key, lockValue, expireMillis);
			log.debug("Seat {} lock status: {}", key, locked);
			if (!locked) {
				unlockAll();
				return false;
			}
		}
		return true;
	}

	public void unlockAll() {
		for (String key : keys) {
			lockManager.unlock(key, lockValue);
		}
	}
}
