package com.bestarch.demo.helloredis.config;

import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.bestarch.demo.model.UserProfile;
import com.google.gson.Gson;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import jakarta.annotation.PostConstruct;
import redis.clients.jedis.UnifiedJedis;

//@Component
public class DataLoader {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    final Gson gson = new Gson();
    
    @Autowired
    UnifiedJedis unifiedJedis;
    
    @PostConstruct
	public void loadData() {

		try (CSVReader reader = new CSVReaderBuilder(
				new InputStreamReader(new ClassPathResource("user-profiles.csv").getInputStream()))
				.withSkipLines(1) // Skip header
				.build()) {

			List<String[]> records = reader.readAll();
			System.out.println("Starting to load " + records.size() + " user profiles to Redis...");

			int count = 0;
			for (String[] record : records) {
				UserProfile userProfile = new UserProfile();

				userProfile.setId(record[0]);
				userProfile.setFirstName(record[1]);
				userProfile.setLastName(record[2]);
				userProfile.setEmail(record[3]);
				userProfile.setPhone(record[4]);

				LocalDate dob = LocalDate.parse(record[5], DATE_FORMATTER);
				userProfile
						.setDateOfBirth(dob.atStartOfDay(ZoneId.of("Asia/Kolkata")).toInstant().toEpochMilli() / 1000);

				userProfile.setGender(record[6]);
				userProfile.setCity(record[7]);
				userProfile.setState(record[8]);
				userProfile.setPincode(record[9]);
				userProfile.setOccupation(record[10]);
				userProfile.setDepartment(record[11]);
				userProfile.setExperience(Integer.parseInt(record[12]));
				userProfile.setBankName(record[13]);
				userProfile.setBranchCode(record[14]);
				userProfile.setIfscCode(record[15]);

				LocalDate joinDt = LocalDate.parse(record[16], DATE_FORMATTER);
				userProfile
						.setJoinDate(joinDt.atStartOfDay(ZoneId.of("Asia/Kolkata")).toInstant().toEpochMilli() / 1000);

				userProfile.setStatus(record[17]);

				String key = "user:" + record[0];
				unifiedJedis.jsonSet(key, gson.toJson(userProfile));

				count++;
				if (count % 10 == 0) {
					System.out.println("Loaded " + count + " profiles...");
				}
			}

			logger.info("Successfully loaded all " + count + " user profiles to Redis!");
			logger.info("Data stored as JSON with keys in format: user:{id}");

		} catch (Exception e) {
			logger.error("Error loading data: " + e.getMessage());
			e.printStackTrace();
		}
	}

}
