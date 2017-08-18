package com.shaddyhollow.quicktable.models;

import java.util.UUID;

public interface Identifiable {
	public UUID getId();
	public String getName();
	
	public void setId(UUID ID);
	public void setName(String name);
}
