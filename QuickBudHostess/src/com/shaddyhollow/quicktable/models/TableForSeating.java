package com.shaddyhollow.quicktable.models;

public class TableForSeating {
	public Table table;
	public int position;
	public Section section;
	
	public String getDescription() {
		return table.getTableDescription() + " (" + section.getDescription() + ")";
	}
}
