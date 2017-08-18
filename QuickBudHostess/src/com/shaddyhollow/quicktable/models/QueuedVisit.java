package com.shaddyhollow.quicktable.models;

import java.util.UUID;

public class QueuedVisit implements Identifiable {
	private UUID id;
	private UUID visit_id;
	private String status;
	private int party_size;
	private String name;
	private String phone_number;
	private String low_wait_time;
	private String high_wait_time;
	private boolean order_in;
	private boolean wheel_chair_access;
	private int high_chairs;
	private int booster_seats;
	private String created_at;
	private String special_requests;
	private int removed;
	private int fromServer;
	private boolean walkin;
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	@Override
	public UUID getId() {
		return id;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override 
	public void setId(UUID id) {
		this.id = id;
	}
	
	@Override
	public void setName(String name) {
		if(name==null) {
			name = "Guest";
		}
		name = name.trim();
		if(name.length()==0) {
			name = "Guest";
		}
		String[] parts = name.split(" ");
		if(parts.length>0) {
			this.name = parts[0];
			if(parts.length>1 && parts[1].length()>0) {
				this.name = this.name + " " + parts[1].substring(0, 1) + ".";
			}
		}
	}

	public int getParty_size() {
		return party_size;
	}
	public void setParty_size(int party_size) {
		this.party_size = party_size;
	}
	public UUID getVisit_id() {
		return visit_id;
	}
	public void setVisit_id(UUID visit_id) {
		this.visit_id = visit_id;
	}
	public Boolean isOrder_in() {
		return order_in;
	}
	public void setOrder_in(Boolean order_in) {
		this.order_in = order_in;
	}
	public String getHigh_wait_time() {
		return high_wait_time;
	}
	public void setHigh_wait_time(String high_wait_time) {
		this.high_wait_time = high_wait_time;
	}
	public String getLow_wait_time() {
		return low_wait_time;
	}
	public void setLow_wait_time(String low_wait_time) {
		this.low_wait_time = low_wait_time;
	}
	public int getBooster_seats() {
		return booster_seats;
	}
	public void setBooster_seats(int booster_seats) {
		this.booster_seats = booster_seats;
	}
	public int getHigh_chairs() {
		return high_chairs;
	}
	public void setHigh_chairs(int high_chairs) {
		this.high_chairs = high_chairs;
	}
	public Boolean isWheel_chair_access() {
		return wheel_chair_access;
	}
	public void setWheel_chair_access(Boolean wheel_chair_access) {
		this.wheel_chair_access = wheel_chair_access;
	}
	public String getPhone_number() {
		return phone_number;
	}
	public void setPhone_number(String phone_number) {
		this.phone_number = phone_number;
	}
	public String getCreated_at() {
		return created_at;
	}
	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}
	public int getRemoved() {
		return removed;
	}
	public void setRemoved(int removed) {
		this.removed = removed;
	}
	public int getFromServer() {
		return fromServer;
	}
	public void setFromServer(int fromServer) {
		this.fromServer = fromServer;
	}
	public String getSpecialRequests() {
		return special_requests;
	}
	public void setSpecialRequests(String special_requests) {
		this.special_requests = special_requests;
	}
	
	public boolean getWalkIn() {
		return walkin;
	}
	public void setWalkIn(boolean walkin) {
		this.walkin = walkin;
	}
}
