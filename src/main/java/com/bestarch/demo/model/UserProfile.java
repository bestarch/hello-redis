package com.bestarch.demo.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {
	private String id;
	private String firstName;
	private String lastName;
	private String email;
	private String phone;

	@JsonFormat(pattern = "yyyy-MM-dd")
	public Long dateOfBirth;

	private String gender;
	private String city;
	private String state;
	private String pincode;
	private String occupation;
	private String department;
	private Integer experience;
	private String bankName;
	private String branchCode;
	private String ifscCode;

	@JsonFormat(pattern = "yyyy-MM-dd")
	public Long joinDate;

	private String status;
}
