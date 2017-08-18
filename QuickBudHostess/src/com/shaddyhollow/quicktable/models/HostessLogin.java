package com.shaddyhollow.quicktable.models;

public class HostessLogin {
	private String email;
	private String password;
	private String device_id;
	
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
	
	public String getSerailId() {
		return device_id;
	}
	public void setSerailId(String device_id) {
		this.device_id = device_id;
	}
}
