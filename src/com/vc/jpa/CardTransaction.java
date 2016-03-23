package com.vc.jpa;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "CARD_TRANSACTIONS")
public class CardTransaction {

	@Id
	@Column(name = "ID")
	@GeneratedValue
	private int id;

	@Column(name = "CARDID")
	private String cardId;

	@Column(name = "TRANSACTIONTIME")
	private Timestamp transactionTime;

	@Column(name = "AMOUNT")
	private String amount;

	@Column(name = "NOTE")
	private String NOTE;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "STATUS_DESC")
	private String statusDescription;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCardId() {
		return cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}

	public Timestamp getTransactionTime() {
		return transactionTime;
	}

	public void setTransactionTime(Timestamp transactionTime) {
		this.transactionTime = transactionTime;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getNOTE() {
		return NOTE;
	}

	public void setNOTE(String nOTE) {
		NOTE = nOTE;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatusDescription() {
		return statusDescription;
	}

	public void setStatusDescription(String statusDescription) {
		this.statusDescription = statusDescription;
	}

}
