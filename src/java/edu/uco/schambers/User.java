package edu.uco.schambers;


import java.util.List;


/**
 *
 * @author Steve
 */
public class User {
	private long id;
	private String username;
	private String firstName;
	private String lastName;
	private String email;
	private String password;
	private String phoneNum;
	private String gender;
	private String hometown;
	private boolean cplusplus;
	private boolean java;
	private boolean csharp;
	private boolean swift;
	private boolean python;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}


	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getHometown() {
		return hometown;
	}

	public void setHometown(String hometown) {
		this.hometown = hometown;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public boolean isCplusplus() {
		return cplusplus;
	}

	public void setCplusplus(boolean cplusplus) {
		this.cplusplus = cplusplus;
	}

	public boolean isJava() {
		return java;
	}

	public void setJava(boolean java) {
		this.java = java;
	}

	public boolean isCsharp() {
		return csharp;
	}

	public void setCsharp(boolean csharp) {
		this.csharp = csharp;
	}

	public boolean isSwift() {
		return swift;
	}

	public void setSwift(boolean swift) {
		this.swift = swift;
	}

	public boolean isPython() {
		return python;
	}

	public void setPython(boolean python) {
		this.python = python;
	}

}
