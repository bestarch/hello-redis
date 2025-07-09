package com.bestarch.demo.helloredis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.bestarch.demo.helloredis.service.RedisNativeDSService;
import com.bestarch.demo.helloredis.service.RedisSearchService;


@SpringBootApplication
public class HelloRedisApplication implements CommandLineRunner{
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	RedisNativeDSService redisNativeDSService;
	
	@Autowired
	RedisSearchService redisSearchService;
	
	public static void main(String[] args) {
		SpringApplication.run(HelloRedisApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		
//		logger.info("Executing some Redis commands");
//		redisNativeDSService.testAll();
		
		try {
			redisSearchService.createIndex();
		} catch (Exception e) {
			logger.error("Index user_index might exist");
			e.printStackTrace();
		}
		redisSearchService.findAllUsersWithPagination(0, 10);
		redisSearchService.findAllUsersWithMoreThan15YearsExp();
		redisSearchService.findAllFemaleUsersBasedOutOfMaharashtraAndLessThan25YearsofAge();
		redisSearchService.findTotalNoOfusersByState();
	}
	
}
