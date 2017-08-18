package com.shaddyhollow.quicktable.models;

import java.util.ArrayList;
import java.util.UUID;

import com.shaddyhollow.quicktable.models.Table.Status;

public class Section implements Identifiable {
	public UUID id;
	public int colorID;
	public String name;
	private Server[] servers;
	public boolean open;
	public Table[] tables;
	public int serviceRound;
	public int sortorder;
	public int dailysortorder;

	public String getDescription() {
		return "Section: " + name;
	}
	
	public void removeTables() {
		tables = null;
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
		this.name = name;
	}
	
	public void removeTable(Table table) {
		ArrayList<Table> newTables = new ArrayList<Table>();
		if(tables!=null) {
			for(Table sectionTable : tables) {
				if(!sectionTable.id.equals(table.id)) {
					newTables.add(sectionTable);
				} else {
					table.section_id=null;
				}
			}
		}
		tables = newTables.toArray(new Table[0]);
	}

	public void addTable(Table table) {
		if(table==null) {
			return;
		}
		Table[] tablesCopy = null;
		table.section_id = id;
		if(tables==null) {
			tablesCopy = new Table[1];
		} else {
			tablesCopy = new Table[tables.length+1];
			System.arraycopy(tables, 0, tablesCopy, 0, tablesCopy.length-1);
		}
		tablesCopy[tablesCopy.length-1] = table;
		tables = tablesCopy;
	}
	
	public Table getTable(int tableID) {
		Table foundTable = null;
		for(Table table : tables) {
			if(table.getId().equals(tableID)) {
				foundTable = table;
				break;
			}
		}
		return foundTable;
	}
	
	public int getOpenTableCount() {
		int open = 0;
		for(Table table : tables) {
			if(table.getState()==Status.OPEN && table.seated_visit==null) {
				open++;
			}
		}
		return open;
	}

	public Server[] getServers() {
		return servers;
	}

	public void setServers(Server[] servers) {
		ArrayList<Server> serverList = new ArrayList<Server>();
		
		if(servers!=null) {
			for(Server server : servers) {
				if(server==null) {
					continue;
				}
				serverList.add(server);
			}
		}
		this.servers = serverList.toArray(new Server[0]);
	}

	public Table[] getTables() {
		return tables;
	}

	public void setTables(Table[] tables) {
		ArrayList<Table> tableList = new ArrayList<Table>();
		
		if(tables!=null) {
			for(Table table : tables) {
				if(table==null) {
					continue;
				}
				tableList.add(table);
			}
		}
		this.tables = tableList.toArray(new Table[0]);
	}
}
