package com.vc.jpa;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "CUSTOMER_OTP")
public class CustomerOTP {

	@Id
	@Column(name = "ID")
	@GeneratedValue( strategy = GenerationType.SEQUENCE, generator = "CUST_OTP_SEQ" )
	@SequenceGenerator( name = "CUST_OTP_SEQ", sequenceName = "CUST_OTP_SEQ", allocationSize = 1, initialValue = 1 )
	private int id;

	@Column(name = "CUSTOMERID")
	private String customerId;

	@Column(name = "OTP")
	private String otp;

	@Column(name = "ISVALID")
	private char isValid;
	
	@Column(name = "GENERATED_TIME")
	private Timestamp createdTime;
	
	public Timestamp getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Timestamp createdTime) {
		this.createdTime = createdTime;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

	public char getIsValid() {
		return isValid;
	}

	public void setIsValid(char isValid) {
		this.isValid = isValid;
	}

}
