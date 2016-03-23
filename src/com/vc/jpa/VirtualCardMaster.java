package com.vc.jpa;

import java.sql.Timestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "VIRTUAL_CARD_MASTER")
public class VirtualCardMaster {

	@Id
	@Column(name = "ID")
	@GeneratedValue( strategy = GenerationType.SEQUENCE, generator = "VIRTUAL_CARD_SEQ" )
	@SequenceGenerator( name = "VIRTUAL_CARD_SEQ", sequenceName = "VIRTUAL_CARD_SEQ", allocationSize = 1, initialValue = 1 )
	private int ID;

	@Column(name = "CREATED_TIME")
	private Timestamp createdTime;

	@Column(name = "VIRTUAL_ADDRESS")
	private String virtualAddress;

	@Column(name = "CARD_NO")
	private String cardNo;

	@Column(name = "ACCOUNTNO")
	private String accountNo;

	@Column(name = "ISACTIVE")
	private char isActive;
	
	//@OneToOne(cascade=CascadeType.ALL)
	//@JoinColumn(name="ID", unique= true, nullable=true, insertable=true, updatable=true)
	/*private VirtualCardPersonalDetails personalDetails;
	
	public VirtualCardPersonalDetails getPersonalDetails() {
		return personalDetails;
	}

	public void setPersonalDetails(VirtualCardPersonalDetails personalDetails) {
		this.personalDetails = personalDetails;
	}*/

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
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

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public char getIsActive() {
		return isActive;
	}

	public void setIsActive(char isActive) {
		this.isActive = isActive;
	}

}
