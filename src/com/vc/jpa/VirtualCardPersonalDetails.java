package com.vc.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "VIRTUAL_CARD_PERSONAL_DETAIL")
public class VirtualCardPersonalDetails {

	@Id
	@Column(name = "ID")
	@GeneratedValue( strategy = GenerationType.SEQUENCE, generator = "VIRTUAL_CARD_PRSNL_DETAILS_SEQ" )
	@SequenceGenerator( name = "VIRTUAL_CARD_PRSNL_DETAILS_SEQ", sequenceName = "VIRTUAL_CARD_PRSNL_DETAILS_SEQ", allocationSize = 1, initialValue = 1 )
	private int id;

	@Column(name = "CARDID")
	private int cardId;

	@Column(name = "NAME")
	private String name;

	@Column(name = "GENDER")
	private char gender;

	@Column(name = "MOBILENO")
	private String mobileNo;

	@Column(name = "EMAIL")
	private String email;

	@Column(name = "STATUS")
	private String status;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCardId() {
		return cardId;
	}

	public void setCardId(int cardId) {
		this.cardId = cardId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public char getGender() {
		return gender;
	}

	public void setGender(char gender) {
		this.gender = gender;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
