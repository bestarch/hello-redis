package com.bestarch.demo.helloredis.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import redis.clients.jedis.UnifiedJedis;
import redis.clients.jedis.search.Document;
import redis.clients.jedis.search.IndexDefinition;
import redis.clients.jedis.search.IndexOptions;
import redis.clients.jedis.search.Query;
import redis.clients.jedis.search.Schema;
import redis.clients.jedis.search.SearchResult;
import redis.clients.jedis.search.aggr.AggregationBuilder;
import redis.clients.jedis.search.aggr.AggregationResult;
import redis.clients.jedis.search.aggr.Reducers;
import redis.clients.jedis.search.aggr.Row;
import redis.clients.jedis.search.aggr.SortedField;

@Service
public class RedisSearchService {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private UnifiedJedis jedis;
	
	
	public void createIndex() {
		logger.info("Creating Index on User profile");
		Schema schema = new Schema()
				.addTextField("$.firstName", 1.0).as("firstName")
				.addTextField("$.lastName", 1.0).as("lastName")
				.addTextField("$.email", 1.0).as("email")
				.addTextField("$.phone", 1.0).as("phone")
				.addTagField("$.state").as("state")
				.addNumericField("$.experience").as("experience")
				.addNumericField("$.dateOfBirth").as("dateOfBirth")
				.addTagField("$.branchCode", false).as("branchCode")
				.addTagField("$.department", false).as("department")
				.addTagField("$.gender", false).as("gender");

		IndexDefinition rule = new IndexDefinition(IndexDefinition.Type.JSON).setPrefixes(new String[]{"user:"});

		jedis.ftCreate("user_index", IndexOptions.defaultOptions().setDefinition(rule), schema);
	}
	
	
	public void findAllUsersWithMoreThan15YearsExp() {
		logger.info("Get all users with more than 15 years of experience");
		logger.info("Search query --> \n"
				+ "ft.search user_index '@experience:[15 50]' return 10 "
				+ "firstName lastName experience email age dateOfBirth city branchCode gender department");
		
		Query q = new Query("*")
		        .addFilter(new Query.NumericFilter("experience", 15, 30))
				.returnFields("firstName", "lastName", "experience", "email", "age", 
						"dateOfBirth", "city", "branchCode", "gender", "department")
		        .limit(0, 50);

		SearchResult sr = jedis.ftSearch("user_index", q);
		if (sr != null) {
			List<Document> documents = sr.getDocuments();
			documents.forEach((d) -> {
				logger.info(d.toString());
			});
		}
		
	}
	
	
	public void findAllFemaleUsersBasedOutOfMaharashtraAndLessThan25YearsofAge() {
		logger.info("Get all female users based out of Maharashtra");
		logger.info("Search query --> \n"
				+ "ft.search user_index '@state:{Maharashtra} @gender:{Female}' return 10 "
				+ "firstName lastName experience email age dateOfBirth city branchCode gender department");
		
		Query q = new Query("@state:{Maharashtra} @gender:{Female}")
				.returnFields("firstName", "lastName", "experience", "email", "age", 
						"dateOfBirth", "city", "branchCode", "gender", "department")
		        .limit(0, 50);

		SearchResult sr = jedis.ftSearch("user_index", q);
		if (sr != null) {
			List<Document> documents = sr.getDocuments();
			documents.forEach((d) -> {
				logger.info(d.toString());
			});
		}
		
	}
	
	public void findTotalNoOfusersByState() {
		logger.info("Get count of users by state");
		logger.info("Search query --> \n"
				+ "FT.AGGREGATE user_index '*' GROUPBY 1 @state REDUCE COUNT 0 as noOfUsers SORTBY 2 @noOfUsers DESC");
		
		AggregationBuilder ab = new AggregationBuilder("*")
		        .groupBy("@state", Reducers.count().as("noOfUsers"))
		        .sortBy(20, SortedField.desc("@noOfUsers"));
		
		AggregationResult ar = jedis.ftAggregate("user_index", ab);
		if (ar != null) {
			List<Row> rows = ar.getRows();
			rows.forEach((r) -> {
				logger.info(r.toString());
			});
		}
		
	}
	
	
}
