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
import javax.persistence.Transient;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Steve
 */
@Entity
@Table(name = "USERS")
@XmlRootElement
@NamedQueries({
	@NamedQuery(name = "Users.findAll", query = "SELECT u FROM Users u"),
	@NamedQuery(name = "Users.findById", query = "SELECT u FROM Users u WHERE u.id = :id"),
	@NamedQuery(name = "Users.findByUsername", query = "SELECT u FROM Users u WHERE u.username = :username"),
	@NamedQuery(name = "Users.findByFirstname", query = "SELECT u FROM Users u WHERE u.firstname = :firstname"),
	@NamedQuery(name = "Users.findByLastname", query = "SELECT u FROM Users u WHERE u.lastname = :lastname"),
	@NamedQuery(name = "Users.findByEmail", query = "SELECT u FROM Users u WHERE u.email = :email"),
	@NamedQuery(name = "Users.findByPassword", query = "SELECT u FROM Users u WHERE u.password = :password"),
	@NamedQuery(name = "Users.findByPhonenum", query = "SELECT u FROM Users u WHERE u.phonenum = :phonenum"),
	@NamedQuery(name = "Users.findByAddress", query = "SELECT u FROM Users u WHERE u.address = :address")})
public class Users implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "ID")
	private Integer id;
	@Size(max = 255)
	@Column(name = "USERNAME")
	private String username;
	@Size(max = 20, message = "First name must be shorter than 20 characters")
	@Pattern(regexp = "[a-zA-Z][a-zA-Z]*", message = "First name must have only alphabetical characters")
	@Column(name = "FIRSTNAME")
	private String firstname;
	@Size(max = 20, message = "First name must be shorter than 20 characters")
	@Pattern(regexp = "[a-zA-Z][a-zA-Z]*", message = "First name must have only alphabetical characters")
	@Column(name = "LASTNAME")
	private String lastname;
	// @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
	@Size(max = 50)
	@Column(name = "EMAIL")
	@Pattern(regexp = ".*@.*[.].*", message = "The address provided is not valid.")
	private String email;
	@Size(min = 3, max = 64, message = "password must be between 3 and 63 digits long")
	@Column(name = "PASSWORD")
	private String password;
	@Size(max = 12)
	@Pattern(regexp = "^[1-9][0-9]{2}-[0-9]{3}-[0-9]{4}", message = "Phone number must be in format: XXX-XXX-XXXX")
	@Column(name = "PHONENUM")
	private String phonenum;
	@Size(max = 255)
	@Column(name = "ADDRESS")
	private String address;

	@Transient
	private boolean editable;
	@Transient
	private boolean admin;
	@Transient
	private boolean cust;

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	public boolean isCust() {
		return cust;
	}

	public void setCust(boolean cust) {
		this.cust = cust;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public Users() {
	}

	public Users(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPhonenum() {
		return phonenum;
	}

	public void setPhonenum(String phonenum) {
		this.phonenum = phonenum;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
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
		if (!(object instanceof Users)) {
			return false;
		}
		Users other = (Users) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "edu.uco.schambers.Entity.Users[ id=" + id + " ]";
	}

}
