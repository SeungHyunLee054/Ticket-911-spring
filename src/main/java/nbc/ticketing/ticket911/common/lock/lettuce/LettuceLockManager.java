package nbc.ticketing.ticket911.common.lock.lettuce;

import org.springframework.stereotype.Component;

import io.lettuce.core.RedisClient;
import io.lettuce.core.ScriptOutputType;
import io.lettuce.core.SetArgs;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LettuceLockManager {

	private final RedisClient redisClient;

	public boolean tryLock(String key, String value, long expireMillis) {
		try (StatefulRedisConnection<String, String> connection = redisClient.connect()) {
			RedisCommands<String, String> commands = connection.sync();
			String result = commands.set(
				key,
				value,
				SetArgs.Builder.nx().px(expireMillis)
			);
			return "OK".equals(result);
		}
	}

	public void unlock(String key, String value) {
		String luaScript = """
				if redis.call("get", KEYS[1]) == ARGV[1]
				then
					return redis.call("del", KEYS[1])
				else
					return 0
				end
			""";
		try (StatefulRedisConnection<String, String> connection = redisClient.connect()) {
			RedisCommands<String, String> commands = connection.sync();
			commands.eval(luaScript, ScriptOutputType.INTEGER, new String[] {key}, value);
		}
	}
}
