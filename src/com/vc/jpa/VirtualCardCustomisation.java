package com.vc.jpa;

import java.sql.Clob;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "VIRTUAL_CARD_CUSTOMIZATION")
public class VirtualCardCustomisation {

	@Id
	@Column(name = "ID")
	private int id;

	@Column(name = "CARDID")
	private int cardId;

	@Column(name = "PARAMKEY")
	private String paramKey;

	@Column(name = "PARAMVAL")
	private Clob paramValue;

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

	public String getParamKey() {
		return paramKey;
	}

	public void setParamKey(String paramKey) {
		this.paramKey = paramKey;
	}

	public Clob getParamValue() {
		return paramValue;
	}

	public void setParamValue(Clob paramValue) {
		this.paramValue = paramValue;
	}

}
