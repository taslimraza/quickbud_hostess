package com.shaddyhollow.quicktable.models;

import java.util.UUID;

public class FloorplanToSave {
	private UUID location_id;
	public String name;
	public Table[] tables_attributes;

	public FloorplanToSave()
	{
		this.tables_attributes = new Table[1];
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public UUID getLocation_id() {
		return location_id;
	}

	public void setLocation_id(UUID location_id) {
		this.location_id = location_id;
	}
}
