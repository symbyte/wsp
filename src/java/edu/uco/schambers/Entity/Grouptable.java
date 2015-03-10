/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uco.schambers.Entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Steve
 */
@Entity
@Table(name = "GROUPTABLE")
@XmlRootElement
@NamedQueries({
	@NamedQuery(name = "Grouptable.findAll", query = "SELECT g FROM Grouptable g"),
	@NamedQuery(name = "Grouptable.findById", query = "SELECT g FROM Grouptable g WHERE g.id = :id"),
	@NamedQuery(name = "Grouptable.findByGroupname", query = "SELECT g FROM Grouptable g WHERE g.groupname = :groupname"),
	@NamedQuery(name = "Grouptable.findByUsername", query = "SELECT g FROM Grouptable g WHERE g.username = :username")})
public class Grouptable implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Basic(optional = false)
        @Column(name = "ID")
	private Integer id;
	@Size(max = 255)
        @Column(name = "GROUPNAME")
	private String groupname;
	@Size(max = 255)
        @Column(name = "USERNAME")
	private String username;

	public Grouptable() {
	}
	public Grouptable(String username, String group)
	{
		this.username = username;
		this.groupname = group;
	}
	public Grouptable(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getGroupname() {
		return groupname;
	}

	public void setGroupname(String groupname) {
		this.groupname = groupname;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are not set
		if (!(object instanceof Grouptable)) {
			return false;
		}
		Grouptable other = (Grouptable) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "edu.uco.schambers.Entity.Grouptable[ id=" + id + " ]";
	}
	
}
