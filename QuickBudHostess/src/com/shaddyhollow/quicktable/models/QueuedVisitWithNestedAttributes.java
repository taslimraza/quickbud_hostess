package com.shaddyhollow.quicktable.models;


import java.util.UUID;

import com.google.gson.Gson;

public class QueuedVisitWithNestedAttributes {
	public QueuedVisitWithNestedAttributes() {
		visit_attributes = new VisitAttributes();
	}
	
	public String toString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}

	public UUID id;
	public Integer location_id;
	public Integer tenant_id;
	public int wait_time;
	public int high_wait_time;
	public int low_wait_time;
	public int position;
	public UUID visit_id;
	public String created_at;
	public boolean walkin;

	public VisitAttributes visit_attributes;
	
	public static class VisitAttributes {
		public VisitAttributes() {
			patron_attributes = new PatronAttributes();
		}
		public UUID id;
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
	public UUID getVisit_id() {
		return visit_id;
	}
	public void setVisit_id(UUID id) {
		visit_id = id;
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

	public Integer getLocation_id() {
		return location_id;
	}

	public void setLocation_id(Integer location_id) {
		this.location_id = location_id;
	}
	
	public Integer getTenant_id() {
		return tenant_id;
	}

	public void setTenant_id(Integer location_id) {
		this.location_id = tenant_id;
	}
	
	public UUID getID() {
		return id;
	}
	public void setID(UUID id) {
		this.id = id;
	}
	public int getWait_time() {
		return wait_time;
	}
	public void setWait_time(int wait_time) {
		this.wait_time = wait_time;
	}
	public int getHigh_wait_time() {
		return high_wait_time;
	}
	public void setHigh_wait_time(int high_wait_time) {
		this.high_wait_time = high_wait_time;
	}
	public int getLow_wait_time() {
		return low_wait_time;
	}
	public void setLow_wait_time(int low_wait_time) {
		this.low_wait_time = low_wait_time;
	}
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	
	public boolean getWalkIn() {
		return walkin;
	}
	public void setWalkIn(boolean walkin) {
		this.walkin = walkin;
	}
	
	public String getCreatedAt(){
		return this.created_at;
	}
	
}
