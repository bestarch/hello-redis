package com.bestarch.demo.helloredis.config;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.ObjectUtils;

import redis.clients.jedis.Connection;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.DefaultJedisClientConfig.Builder;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisClientConfig;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.UnifiedJedis;


/**
 * No need to define this configuration file.
 * The spring boot auto-configures the Redis Connection factory based on the presence of 
 * spring-boot-data-redis dependency and
 * the following properties:
 * 		spring.data.redis.host
 * 		spring.data.redis.port
 * 		spring.data.redis.password
 * 
 * But, for more customizations, we can still define this config file and define the beans here
 */
@Configuration
public class RedisConfig {
	
	/**
	 * Explicit RedisTemplate configuration
	 * @param connectionFactory
	 * @return
	 */
	@Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        
        template.afterPropertiesSet();
        return template;
    }
	
	
	@Bean
	public UnifiedJedis unifiedJedis(
			@Value("${spring.data.redis.host:localhost}") String url,
			@Value("${spring.data.redis.port:6379}") Integer port,
			@Value("${spring.data.redis.password}") String password,
			@Value("${spring.data.redis.jedis.pool.max-active:8}") Integer maxActive,
			@Value("${spring.data.redis.jedis.pool.max-idle:4}") Integer maxIdle,
			@Value("${spring.data.redis.jedis.pool.min-idle:0}") Integer minIdle) {
		
		GenericObjectPoolConfig<Connection> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMaxTotal(maxActive);
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMinIdle(minIdle);
        
        // Create client configuration
        Builder clientConfigBuilder = DefaultJedisClientConfig.builder()
                .connectionTimeoutMillis(2000)
                .socketTimeoutMillis(2000);
        
        if (!ObjectUtils.isEmpty(password)) {
            clientConfigBuilder.password(password);
        }
        
        JedisClientConfig clientConfig = clientConfigBuilder.build();
        
        // Create connection provider with pool
        HostAndPort hostAndPort = new HostAndPort(url, port);
        JedisPooled pooled = new JedisPooled(poolConfig, hostAndPort, clientConfig);
        return pooled;
	}

}
