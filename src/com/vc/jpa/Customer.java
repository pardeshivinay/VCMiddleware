package com.vc.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "CUSTOMER")
public class Customer {

	@Id
	@Column(name = "CUSTOMERID")
	@GeneratedValue( strategy = GenerationType.SEQUENCE, generator = "CUST_SEQ" )
	@SequenceGenerator( name = "CUST_SEQ", sequenceName = "CUST_SEQ", allocationSize = 1, initialValue = 1 )	
	private String customerId;

	@Column(name = "FIRSTNAME")
	private String firstName;

	@Column(name = "LASTNAME")
	private String lastName;

	@Column(name = "MOBILENO")
	private String mobileNo;

	@Column(name = "GENDER")
	private String gender;

	@Column(name = "PIN")
	private String pin;
	
	@Column(name = "USERID")
	private String userId;
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	@Override
	public String toString() {
		return "Customer [customerId=" + customerId + ", firstName="
				+ firstName + ", lastName=" + lastName + ", mobileNo="
				+ mobileNo + ", gender=" + gender + ", pin=" + pin
				+ ", userId=" + userId + "]";
	}

	
}
