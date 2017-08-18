package com.shaddyhollow.quicktable.models;

import java.util.UUID;

import com.google.gson.Gson;

public class LocationEntry {
	private Integer location_id = 0;
	private Integer tenant_id = 0;
	private String name;
	private String address;
	private String city;
	private String state;
	private String zip;
	private String phone_number;
	private int low_wait_time;
	private int high_wait_time;
	private String user_id;
	private String session_key;
	private String csrf_token;

	public Integer getLocationId() {
		return location_id;
	}
	
	public void setLocationId(Integer location_id) {
		this.location_id = location_id;
	}
	
	public Integer getTenantId() {
		return tenant_id;
	}
	
	public void setTenantId(Integer tenant_id) {
		this.tenant_id = tenant_id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getCity() {
		return city;
	}
	
	public void setCity(String city) {
		this.city = city;
	}
	
	public String getState() {
		return state;
	}
	
	public void setState(String state) {
		this.state = state;
	}
	
	public String getZip() {
		return zip;
	}
	
	public void setZip(String zip) {
		this.zip = zip;
	}
	
	public String getPhone_number() {
		return phone_number;
	}
	
	public void setPhone_number(String phone_number) {
		this.phone_number = phone_number;
	}
	
	public int getLow_wait_time() {
		return low_wait_time;
	}
	public void setLow_wait_time(int low_wait_time) {
		this.low_wait_time = low_wait_time;
	}

	public int getHigh_wait_time() {
		return high_wait_time;
	}
	public void setHigh_wait_time(int high_wait_time) {
		this.high_wait_time = high_wait_time;
	}
	
	public String getSessionKeys() {
		return session_key;
	}
	
	public void setSessionKeys(String session_key) {
		this.session_key = session_key;
	}
	
	public String getCsrfToken() {
		return csrf_token;
	}
	
	public void setCsrfToken(String csrf_token) {
		this.csrf_token = csrf_token;
	}
	
	public void setUserId(String user_id){
		this.user_id = user_id;
	}
	public String getUserId(){
		return this.user_id;
	}

	public String toString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}

}
