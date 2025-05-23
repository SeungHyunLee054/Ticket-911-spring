package nbc.ticketing.ticket911.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.lettuce.core.RedisClient;


@Configuration
public class LettuceConfig {

	@Value("${data.redis.host}")
	private String redisHost;

	@Value("${data.redis.port}")
	private int redisPort;

	@Bean
	public RedisClient redisClient() {
		return RedisClient.create("redis://" + redisHost + ":" + redisPort);
	}
}
