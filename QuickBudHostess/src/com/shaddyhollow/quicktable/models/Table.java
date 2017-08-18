package com.shaddyhollow.quicktable.models;

import java.util.List;
import java.util.UUID;

import android.graphics.Color;

import com.shaddyhollow.util.Point;

public class Table implements Identifiable {
	public UUID id;
	public UUID floorplan_id;
	public UUID section_id;
	public int number;
	public String table_type;
	public int seats;
	public SeatedVisit seated_visit;
	public String status;
	public String name;
	public List<Point> position;
	public UUID group_id;

	public enum Status { OPEN, CLOSED, HELD	}

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

	public String getTableDescription() {
		switch (getState()) {
		case CLOSED:
			return getTableNumber() + " : Closed " + table_type + " " + Integer.toString(seats);
		case HELD:
			return getTableNumber() + " : Held " + table_type + " " + Integer.toString(seats);
		case OPEN:
			if (seated_visit != null)
			{
				String name = new String();
				
				if (seated_visit.name != null) {
					name = seated_visit.name;
				} else {
					name = "Guest";
				}
				return getTableNumber() + " : " + name + " " + Integer.toString(seated_visit.party_size);
			}
			else
			{
				return getTableNumber() + " : Open " + table_type + " " + Integer.toString(seats);
			}
		}
		return "";
	}
	
	public String getTableName() {
		return table_type + " "+ getTableNumber();
	}

	private String getTableNumber() {
		String name = this.name;
		if (name == null || name.isEmpty()) {
			name = Integer.toString(number);
		}
		return name;
	}

	public Status getState() {
		if(status==null) {
			status = "open";
		}
		if (status.equalsIgnoreCase("closed")) {
			return Status.CLOSED;
		} else if (status.equalsIgnoreCase("held")) {
			return Status.HELD;
		}
		return Status.OPEN;
	}

	public void setState(Status status) {
		switch(status) {
		case OPEN:
			this.status = "open";
			break;
		case CLOSED:
			this.status = "closed";
			break;
		case HELD:
			this.status = "held";
			break;
		}
	}
	
	public String getDisplayText(boolean includeSeatedVisit) {
		String text = "#" + name;
		
		if(includeSeatedVisit && seated_visit!=null) {
			text += "\n" + seated_visit.name + " ";
			text += "\n(" + seated_visit.party_size + ")";
		} else {
			text += "\n(" + seats + ")";
		}
		return text;
	}
	
	public int getStatusColor() {
		int statusColor = 0xFFDEDEDE; // default color (lt gray) when seated
		
		if(getState().equals(Status.HELD)) {
			statusColor = 0xFFCCCCCC;
		} else if(getState().equals(Status.CLOSED)) {
			statusColor = 0xFF999999;
		} else {
			if(seated_visit==null) {
				statusColor = Color.WHITE;
			}
		}
		
		return statusColor;
	}

}
