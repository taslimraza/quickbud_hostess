package com.shaddyhollow.quicktable.models;

import java.util.UUID;

public class SectionPlan implements Identifiable {
	public UUID id;
	public String name;
	public Section[] sections;
	
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
}
