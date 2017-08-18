package com.shaddyhollow.quicktable.models;

import java.util.UUID;

public class SectionPlanToSave {
	private UUID location_id;
	public String name;
	public Section[] sections;

	public SectionPlanToSave() {
		this.sections = new Section[1];
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
