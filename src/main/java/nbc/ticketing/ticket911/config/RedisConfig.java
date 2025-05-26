package nbc.ticketing.ticket911.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.lettuce.core.RedisClient;

@Configuration
public class RedisConfig {

	@Value("${data.redis.host}")
	private String redisHost;

	@Value("${data.redis.port}")
	private int redisPort;

	@Bean
	public RedissonClient redissonClient() {
		Config config = new Config();
		config.useSingleServer()
			.setAddress("redis://" + redisHost + ":" + redisPort)
			.setConnectionMinimumIdleSize(1)
			.setConnectionPoolSize(5)
			.setConnectTimeout(10000)
			.setTimeout(10000);
		return Redisson.create(config);
	}

	@Bean
	public RedisClient redisClient() {
		return RedisClient.create("redis://" + redisHost + ":" + redisPort);
	}
}
