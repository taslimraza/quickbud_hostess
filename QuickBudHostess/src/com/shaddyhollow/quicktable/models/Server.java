package com.shaddyhollow.quicktable.models;

import java.util.UUID;

public class Server implements Identifiable {
	public UUID id;
	public String name;
	private int tables_served;
	public int colorstate; 
	public int min_party;
	public int max_party;
	
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
		this.name = name;
	}

	public int getTables_served() {
		return tables_served;
	}

	public void setTables_served(int tables_served) {
		this.tables_served = tables_served;
	}
}
