package com.shaddyhollow.quicktable.models;

import java.util.UUID;

import com.google.gson.Gson;

public class QueuedVisitRequest {
	public QueuedVisitRequest() {
		visit_attributes = new VisitAttributes();
	}
	private UUID id;
	private UUID visit_id;
	private Integer location_id;
	private Integer tenant_id;
	private String status;
	private VisitAttributes visit_attributes;
	
	public static class VisitAttributes {
		public VisitAttributes() {
			patron_attributes = new PatronAttributes();
		}
		
		public String special_requests;
		public int party_size;
		public int high_chairs;
		public int booster_seats;
		public boolean wheel_chair_access;
		public PatronAttributes patron_attributes;
		
		public static class PatronAttributes {
			public String name;
			public String phone_number;
		}
	}
	
	public String getName() {
		return visit_attributes.patron_attributes.name;
	}
	public void setName(String name) {
		this.visit_attributes.patron_attributes.name = name;
	}

	public String getPhone_number() {
		return visit_attributes.patron_attributes.phone_number;
	}
	public void setPhone_number(String phone_number) {
		this.visit_attributes.patron_attributes.phone_number = phone_number;
	}
	
	public String getSpecialRequests() {
		return visit_attributes.special_requests;
	}
	public void setSpecialRequests(String special_requests) {
		this.visit_attributes.special_requests = special_requests;
	}
	
	public boolean isWheel_chair_access() {
		return visit_attributes.wheel_chair_access;
	}

	public void setWheel_chair_access(boolean wheel_chair_access) {
		this.visit_attributes.wheel_chair_access = wheel_chair_access;
	}
	
	public int getBooster_seats() {
		return visit_attributes.booster_seats;
	}

	public void setBooster_seats(int booster_seats) {
		this.visit_attributes.booster_seats = booster_seats;
	}	
		
	public int getHigh_chairs() {
		return visit_attributes.high_chairs;
	}

	public void setHigh_chairs(int high_chairs) {
		this.visit_attributes.high_chairs = high_chairs;
	}	
		
	public int getParty_size() {
		return visit_attributes.party_size;
	}

	public void setParty_size(int party_size) {
		this.visit_attributes.party_size = party_size;
	}

	public String toString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}


	public Integer getLocation_id() {
		return location_id;
	}


	public void setLocation_id(Integer location_id) {
		this.location_id = location_id;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public VisitAttributes getVisit_attributes() {
		return visit_attributes;
	}
	public void setVisit_attributes(VisitAttributes visit_attributes) {
		this.visit_attributes = visit_attributes;
	}
	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}
	public UUID getVisit_id() {
		return visit_id;
	}
	public void setVisit_id(UUID visit_id) {
		this.visit_id = visit_id;
	}
	public Integer getTenant_id() {
		return tenant_id;
	}
	public void setTenant_id(Integer tenant_id) {
		this.tenant_id = tenant_id;
	}

}
