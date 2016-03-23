package com.vc.jpa;

import javax.persistence.Column;
import javax.persistence.Id;

public class LimitMaster {
	@Id
	@Column(name = "ID")	
	private int id;
	
	@Column(name="LIMIT_TYPE")
	private String limiType;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLimiType() {
		return limiType;
	}

	public void setLimiType(String limiType) {
		this.limiType = limiType;
	}

	@Override
	public String toString() {
		return "{id" +id  + ", limiType:" + limiType+"}";
	}
	
	
	
	
	
}
