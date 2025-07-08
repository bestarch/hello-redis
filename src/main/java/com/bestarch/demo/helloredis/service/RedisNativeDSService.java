package com.bestarch.demo.helloredis.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Service;

import redis.clients.jedis.UnifiedJedis;

@Service
public class RedisNativeDSService {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	//@Autowired
	private UnifiedJedis jedis;
	
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	
	public void testAll() {
		testStringOperation();
		testHashOperation();
		testSetOperation();
		testSortedSetOperation();
		testSortedSetOperation();
		testListOperation();
		testStreamsOperation();
	}
	
	
	public String testStringOperation() {
		logger.info("Testing String operation");
		String key = "data";
		stringRedisTemplate.opsForValue().set(key, "hello everyone, good morning.");
		return stringRedisTemplate.opsForValue().get(key);
	}
	
	
	public Map<?, ?> testHashOperation() {
		logger.info("Testing Hash operation");
		String key = "user:rohit006";
		Map<String, String> map = Map.of("username","rohit006", "fname","Rohit", "lname","Sharma", "dept", "Finance", "age", "27");
		stringRedisTemplate.opsForHash().putAll(key, map);
		return stringRedisTemplate.opsForHash().entries(key);
		
	}
	
	public Set<String> testSetOperation() {
		logger.info("Testing Set operation");
		String key = "cities";
		stringRedisTemplate.opsForSet().add(key, "Delhi", "Mumbai", "Chennai", "Bangalore", "Noida", "Lucknow", "Mumbai");
		return stringRedisTemplate.opsForSet().members(key);
	}
	
	public Set<TypedTuple<String>> testSortedSetOperation() {
		logger.info("Testing SortedSet operation");
		String key = "leaderboard";
		Set<TypedTuple<String>> tuples = Set.of(
				new DefaultTypedTuple<String>("user1", 10.0),
				new DefaultTypedTuple<String>("user5", 23.0),
				new DefaultTypedTuple<String>("user10", 10.0),
				new DefaultTypedTuple<String>("user8", 100.0),
				new DefaultTypedTuple<String>("user2", 65.0));
		stringRedisTemplate.opsForZSet().add(key, tuples);
		return stringRedisTemplate.opsForZSet().rangeByScoreWithScores(key, 0, 100.0);
	}
	
	public void testListOperation() {
		logger.info("Testing List operation");
		String key = "queue";
		
		logger.info("Pushing job1 in queue");
		stringRedisTemplate.opsForList().leftPush(key, "job1");
		logger.info("Pushing job2 in queue");
		stringRedisTemplate.opsForList().leftPush(key, "job2");
		logger.info("Pushing job3 in queue");
		stringRedisTemplate.opsForList().leftPush(key, "job3");
		logger.info("Pushing job4 in queue");
		stringRedisTemplate.opsForList().leftPush(key, "job4");
		
		logger.info("Poping job1 from queue");
		stringRedisTemplate.opsForList().rightPop(key);
		logger.info("Poping job2 from queue");
		stringRedisTemplate.opsForList().rightPop(key);
		logger.info("Poping job3 from queue");
		stringRedisTemplate.opsForList().rightPop(key);
		
		logger.info("Get size of the list");
		stringRedisTemplate.opsForList().size(key);
	}
	
	public void testStreamsOperation() {
		logger.info("Testing Stream operation");
		String key = "user-events";
		
		Map<String, Object> message1 = new HashMap<>();
        message1.put("userId", "user009");
        message1.put("action", "login");
        message1.put("timestamp", System.currentTimeMillis());
        
        Map<String, Object> message2 = new HashMap<>();
        message2.put("userId", "user029");
        message2.put("action", "searched-mobile-phones");
        message2.put("timestamp", System.currentTimeMillis());
        
        Map<String, Object> message3 = new HashMap<>();
        message3.put("userId", "user0222");
        message3.put("action", "added-item-in-cart");
        message3.put("timestamp", System.currentTimeMillis());
       
		StreamOperations<String, Object, Object> streamOps = redisTemplate.opsForStream();
		streamOps.add(key, message1);
		streamOps.add(key, message2);
		streamOps.add(key, message3);
	}
	
	public void testWait() {
		
		System.out.println("Adding keys to Redis...");
		stringRedisTemplate.opsForValue().set("test:key1", "value1");
		stringRedisTemplate.opsForValue().set("test:key2", "value2");
		stringRedisTemplate.opsForValue().set("test:key3", "value3");
        
        System.out.println("Executing WAIT command...");
        long startTime = System.currentTimeMillis();
        
        // Wait for 1 replica with 2 second timeout
        Long replicasAcked = stringRedisTemplate.execute((RedisCallback<Long>) connection -> {
			Object result = connection.execute("WAIT", String.valueOf(1).getBytes(), String.valueOf(2000).getBytes());
			return (Long) result;
		});
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        System.out.println("WAIT command completed:");
        System.out.println("- Replicas acknowledged: " + replicasAcked);
        System.out.println("- Time taken: " + duration + " ms");
		
	}
	
}
