package com.vc.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "VIRTUAL_CARD_RESTRICTIONS")
public class VirtualCardRestrictions {

	@Id
	@Column(name = "ID")
	@GeneratedValue( strategy = GenerationType.SEQUENCE, generator = "VIRTUAL_CARD_RESTRICTION_SEQ" )
	@SequenceGenerator( name = "VIRTUAL_CARD_RESTRICTION_SEQ", sequenceName = "VIRTUAL_CARD_RESTRICTION_SEQ", allocationSize = 1, initialValue = 1 )
	private int id;

	@Column(name = "CARDID")
	private int cardId;

	@Column(name = "PARAMKEY")
	private String paramKey;

	@Column(name = "PARAMVAL")
	private String paramValue;

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

	public String getParamValue() {
		return paramValue;
	}

	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}

}
