package nbc.ticketing.ticket911.common.lock.lettuce;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LettuceMultiLock {

	private final List<String> keys;
	private final long expireMillis;
	private final LettuceLockManager lockManager;

	private final String lockValue = UUID.randomUUID().toString();

	public boolean tryLockAll() {
		for (String key : keys) {
			boolean locked = lockManager.tryLock(key, lockValue, expireMillis);
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
