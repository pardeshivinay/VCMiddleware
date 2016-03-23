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
@Table(name = "CUSTOMER_VIRTUAL_ADDRESS")
public class VirtualAddress {

	@Id
	@Column(name = "ID")
	@GeneratedValue( strategy = GenerationType.SEQUENCE, generator = "VIRTUAL_ADDRESS_SEQ" )
	@SequenceGenerator( name = "VIRTUAL_ADDRESS_SEQ", sequenceName = "VIRTUAL_ADDRESS_SEQ", allocationSize = 1, initialValue = 1 )
	private int id;

	@Column(name = "CUSTOMERID")
	private String customerId;

	@Column(name = "CREATED_TIME")
	private Timestamp createdTime;

	@Column(name = "VIRTUAL_ADDRESS")
	private String virtualAddress;

	@Column(name = "ACCOUNTNO")
	private String accountNo;

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

	public Timestamp getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Timestamp createdTime) {
		this.createdTime = createdTime;
	}

	public String getVirtualAddress() {
		return virtualAddress;
	}

	public void setVirtualAddress(String virtualAddress) {
		this.virtualAddress = virtualAddress;
	}

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	@Override
	public String toString() {
		return "{id:" + id + ", customerId:" + customerId
				+ ", createdTime:" + createdTime + ", virtualAddress:"
				+ virtualAddress + ", accountNo:" + accountNo + "}";
	}
	
	
	
	
}
