package com.vc.jpa;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "BANK")
public class Bank implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	// primary key
	@Column(name = "bankId")
	@GeneratedValue
	private Integer id;
	@Column(name = "name")
	private String name;

	public Bank() {
	}

	public Bank(Integer id) {
		this.id = id;
	}

	public Bank(Integer id, String name, int managerId) {
		this.id = id;
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are
		// not set
		if (!(object instanceof Bank)) {
			return false;
		}
		Bank other = (Bank) object;
		if ((this.id == null && other.id != null)
				|| (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Bank[ id=" + id + " ]";
	}

}
