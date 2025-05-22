package nbc.ticketing.ticket911.domain.lock;

import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RedisConnectionTest {

	@Autowired
	private RedissonClient redissonClient;

	@Test
	void redisPingTest() {
		String result = (String) redissonClient.getBucket("ping").get();
		System.out.println("Redis 연결 정상. ping 결과: " + result);
	}
}
