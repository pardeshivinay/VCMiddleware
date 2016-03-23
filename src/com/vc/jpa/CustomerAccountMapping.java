package com.vc.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "CUSTOMER_ACCOUNT_MAPPING")
public class CustomerAccountMapping {

	@Id
	@Column(name = "CUSTOMERID")
	private String customerId;

	@Column(name = "ACCOUNTNO")
	private String accountNo;

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

}
