package com.shaddyhollow.quickbud.datastore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.shaddyhollow.quickbud.Config;
import com.shaddyhollow.quicktable.models.Section;
import com.shaddyhollow.quicktable.models.SectionPlan;
import com.shaddyhollow.quicktable.models.Server;
import com.shaddyhollow.quicktable.models.Table;

public class SectionPlanFactory extends BaseFactory<SectionPlan> {
	public String[] columns = new String[] { "uuid", "location_uuid", "name" };
	public String[] sectionColumns = new String[] { "uuid", "sectionplan_uuid", "name", "colorid", "status", "serviceRound", "sortorder", "dailysortorder" };
	public String[] sectionTableColumns = new String[] { "sectionplan_uuid", "section_uuid", "table_uuid" };
	public String[] serverColumns = new String[] { "server_uuid", "section_uuid" };
	
	private static SectionPlanFactory instance = null;
	
	private SectionPlanFactory(Context context) {
		this.mContext = context;
	}
	
	public static SectionPlanFactory getInstance() {
		if(instance==null) {
			throw new RuntimeException("SectionPlanFactory must be initialized first with getInstance(context)");
		}
		return instance;
	}
	
	public static SectionPlanFactory getInstance(Context context) {
		if(instance==null) {
			instance = new SectionPlanFactory(context);
		}
		return instance;
	}

	
	@Override
	protected String getTableName() {
		return DatabaseHelper.T_SECTIONPLANS;
	}

	@Override
	public SectionPlan create(SectionPlan element) {
		try {
			open();
			ContentValues args = new ContentValues();
			int col = 0;
			if(element.getId()==null) {
				element.id = getNextID();
			}
			args.put(columns[col++], String.valueOf(element.id));
			args.put(columns[col++], String.valueOf(Config.getLocationID()));
			args.put(columns[col++], element.getName());
			
			mDB.insert(getTableName(), null, args);
			if(element.sections!=null) {
				createOrUpdateSections(element.getId(), element.sections);
			}
		} finally {
			close();
		}
		return element;
	}

	@Override
	public SectionPlan read(UUID id) {
		SectionPlan element = null;
		Cursor cursor = null;
		try {
			open();
			cursor = mDB.query(true, getTableName(), columns, "uuid=?", new String[] { String.valueOf(id)}, null, null, null, null);
			if(cursor.moveToFirst()) {
				int col = 0;
				element = new SectionPlan();
				element.setId(UUID.fromString(cursor.getString(col++)));
				col++; // ignore location_id
				element.setName(cursor.getString(col++));
				readSections(element);
			}

		} finally {
			if(cursor!=null) {
				cursor.close();
			}
			close();
		}
		return element;
	}

	@Override
	public SectionPlan update(SectionPlan element) {
		try {
			open();
			ContentValues args = new ContentValues();
			args.put(columns[2], element.getName());
			
			mDB.update(getTableName(), args, "uuid=?", new String[] { String.valueOf(element.getId()) } );
			createOrUpdateSections(element.getId(), element.sections);
		} finally {
			close();
		}
		return element;
	}

	public List<SectionPlan> bulkRead(String where) {
		List<SectionPlan> sectionPlans = new ArrayList<SectionPlan>();
		Cursor cursor = null;

		try {
			open();
			
			cursor = mDB.query(true, getTableName(), columns, where, null, null, null, null, null);
			if(cursor.moveToFirst()) {
				do {
					SectionPlan element = null;
	
					int col = 0;
					element = new SectionPlan();
					element.setId(UUID.fromString(cursor.getString(col++)));
					col++; // ignore location_id
					element.setName(cursor.getString(col++));
					sectionPlans.add(element);
				} while(cursor.moveToNext());
			}
			for(SectionPlan plan : sectionPlans) {
				readSections(plan);
			}

		} finally {
			if(cursor!=null) {
				cursor.close();
			}
			close();
		}
		return sectionPlans;
	}

	private void readSections(SectionPlan plan) {
		Cursor cursor = null;
		
		HashMap<UUID, Server> servers = new HashMap<UUID, Server>();
		
		List<Server> serverList = ServerFactory.getInstance().bulkRead(null);
		for(Server server : serverList) {
			servers.put(server.getId(), server);
		}
		
		// load each section in plan
		List<Section> sectionsInPlan = new ArrayList<Section>();
		cursor = mDB.query(true, DatabaseHelper.T_SECTIONS, sectionColumns, "sectionplan_uuid=?", new String[] { String.valueOf(plan.getId()) }, null, null, "sortorder", null);
		if(cursor.moveToFirst()) {
			do {
				int col = 0;
				Section section = new Section();
				section.id = UUID.fromString(cursor.getString(col++));
				col++; // ignore sectionplan_id
				section.name = cursor.getString(col++);
				section.colorID = cursor.getInt(col++);
				section.open = cursor.getInt(col++)!=0;
				section.serviceRound = cursor.getInt(col++);
				section.sortorder = cursor.getInt(col++); // ignore given sort order and assign a new one.  this deals with database restores where sort order may be 0
				section.dailysortorder = cursor.getInt(col++);
				sectionsInPlan.add(section);
			} while(cursor.moveToNext());
		}
		plan.sections = sectionsInPlan.toArray(new Section[0]);
		if(cursor!=null) {
			cursor.close();
		}

		// load section specific data
		for(Section section : plan.sections) {
			// load tables for section
			List<Table> tablesInSection = new ArrayList<Table>();
			cursor = mDB.query(true, DatabaseHelper.T_SECTIONSxTABLES, new String[] { "table_uuid" }, "section_uuid=? and sectionplan_uuid=?", new String[] { String.valueOf(section.getId()), String.valueOf(plan.getId()) }, null, null, null, null);
			if(cursor.moveToFirst()) {
				do {
					Table table = TableFactory.getInstance().read(UUID.fromString(cursor.getString(0)));
					if(table!=null) {
						tablesInSection.add(table);
					}
				} while(cursor.moveToNext());
			}
			if(cursor!=null) {
				cursor.close();
			}
			section.tables = tablesInSection.toArray(new Table[0]);
			
			// load servers for section
			List<UUID> serverIDsInSection = readServerIDsForSection(section.getId());
			List<Server> serversInSection = new ArrayList<Server>();
			for(UUID server_id : serverIDsInSection) {
				Server server = servers.get(server_id);
				serversInSection.add(server);
			}
			section.setServers(serversInSection.toArray(new Server[0]));
		}
	}
	
	public void updateDailySortOrder(List<Section> sections) {
		try {
			open();
			for(int i=0;i<sections.size();i++) {
				sections.get(i).sortorder = i;
				ContentValues args = new ContentValues();
				args.put("dailysortorder", i);
				mDB.update(DatabaseHelper.T_SECTIONS, args, "name=?", new String[] { String.valueOf(sections.get(i).getName()) });
			}
		} finally {
			close();
		}

	}
	
	public List<UUID> readServerIDsForSection(UUID section_id) {
		Cursor cursor = null;
		List<UUID> serversIDsInSection = null;
		try {
			open();
			serversIDsInSection = new ArrayList<UUID>();
			cursor = mDB.query(true, DatabaseHelper.T_SECTIONSxSERVERS, new String[] { "server_uuid" }, "section_uuid=?", new String[] { String.valueOf(section_id) }, null, null, null, null);
			if(cursor.moveToFirst()) {
				do {
					serversIDsInSection.add(UUID.fromString(cursor.getString(0)));
				} while(cursor.moveToNext());
			}
		} finally {
			close();
		}
		if(cursor!=null) {
			cursor.close();
		}
		
		return serversIDsInSection;
	}
	
	@Override
	public void delete(UUID plan_id) {
		try {
			open();
			deleteSections(plan_id);
			super.delete(plan_id);
		} finally {
			close();
		}
	}
	
	public void bulkDelete(String where) {
		List<SectionPlan> sectionPlans = bulkRead(where);
		for(SectionPlan sectionPlan : sectionPlans) {
			delete(sectionPlan.getId());
		}
	}
	
	private void deleteSections(UUID plan_id) {
		Cursor cursor = null;
		
		cursor = mDB.query(true, DatabaseHelper.T_SECTIONS, new String[] {"uuid"}, "sectionplan_uuid=?", new String[] { String.valueOf(plan_id) }, null, null, null, null);
		if(cursor.moveToFirst()) {
			do {
				int section_id = cursor.getInt(0);
				mDB.delete(DatabaseHelper.T_TEMP_SECTIONSxTABLES, "section_uuid=?", new String[] { String.valueOf(section_id) });
				mDB.delete(DatabaseHelper.T_SECTIONSxTABLES, "sectionplan_uuid=? and section_uuid=?", new String[] { String.valueOf(plan_id), String.valueOf(section_id) });
				mDB.delete(DatabaseHelper.T_SECTIONSxSERVERS, "section_uuid=?", new String[] { String.valueOf(section_id) });
			} while(cursor.moveToNext());
		}
		mDB.delete(DatabaseHelper.T_SECTIONS, "sectionplan_uuid=?", new String[] { String.valueOf(plan_id) });
		if(cursor!=null) {
			cursor.close();
		}
	}
	
	private void createOrUpdateSections(UUID plan_id, Section[] sections) {
		deleteSections(plan_id);
		if(sections==null) {
			return;
		}
		int sortOrder = 0;
		for(Section section : sections) {
			if(section.getId()==null) {
				section.id = getNextID(DatabaseHelper.T_SECTIONS);
			}
			section.sortorder = sortOrder;
			section.dailysortorder = sortOrder;
			sortOrder++;
			createSection(plan_id, section);
			if(section.tables!=null) {
				for(Table table : section.tables) {
					assignTable(plan_id, section, table);
				}
			}
		}
	}
	
	private void createSection(UUID plan_id, Section section) {
		ContentValues args = new ContentValues();
		int col = 0;
		args.put(sectionColumns[col++], String.valueOf(section.id));
		args.put(sectionColumns[col++], String.valueOf(plan_id));
		args.put(sectionColumns[col++], section.name);
		args.put(sectionColumns[col++], section.colorID);
		args.put(sectionColumns[col++], section.open ? 1 : 0);
		args.put(sectionColumns[col++], section.serviceRound);
		args.put(sectionColumns[col++], section.sortorder);
		args.put(sectionColumns[col++], section.dailysortorder);
		mDB.insert(DatabaseHelper.T_SECTIONS, null, args);
	}
	
	public void updateSection(Section section) {
		try {
			open();
			ContentValues args = new ContentValues();
			
			int col = 0;
			col++; // skip id
			col++; // skip plan_id
			args.put(sectionColumns[col++], section.name);
			args.put(sectionColumns[col++], section.colorID);
			args.put(sectionColumns[col++], section.open ? 1 : 0);
			args.put(sectionColumns[col++], section.serviceRound);
			args.put(sectionColumns[col++], section.sortorder);
			args.put(sectionColumns[col++], section.dailysortorder);

			mDB.update(DatabaseHelper.T_SECTIONS, args, "uuid=?", new String[] { String.valueOf(section.getId()) });
		} finally {
			close();
		}

	}
	
	public void updateServers(Section section) {
		Cursor cursor = null;
		try {
			open();

			cursor = mDB.query(true, DatabaseHelper.T_SECTIONS, new String[] { "uuid" }, "name=?", new String[] { section.name }, null, null, null, null);

			if(cursor.moveToFirst()) {
				do {
					UUID section_id = UUID.fromString(cursor.getString(0));
					copySectionServerAssignments(section_id, section.getServers());
				} while(cursor.moveToNext());
			}

		} finally {
			if(cursor!=null) {
				cursor.close();
			}
			close();
		}
	}
	
	private void copySectionServerAssignments(UUID section_id, Server[] servers) {
		try {
			open();
			mDB.delete(DatabaseHelper.T_SECTIONSxSERVERS, "section_uuid=?", new String[] { String.valueOf(section_id) });

			ContentValues args = new ContentValues();
			
			if(servers!=null) {
				for(Server server : servers) {
					int col = 0;
					args.put(serverColumns[col++], String.valueOf(server.getId()));
					args.put(serverColumns[col++], String.valueOf(section_id));

					mDB.insert(DatabaseHelper.T_SECTIONSxSERVERS, null, args);
				}
			}
		} finally {
			close();
		}
	}
	
	public void assignTable(UUID plan_id, Section section, Table table) {
		try {
			open();
			mDB.delete(DatabaseHelper.T_SECTIONSxTABLES, "table_uuid=? and sectionplan_uuid=?", new String[] { String.valueOf(table.id), String.valueOf(plan_id) });
			ContentValues args = new ContentValues();
			int col = 0;
			args.put(sectionTableColumns[col++], String.valueOf(plan_id));
			args.put(sectionTableColumns[col++], String.valueOf(section.id));
			args.put(sectionTableColumns[col++], String.valueOf(table.id));
			mDB.insert(DatabaseHelper.T_SECTIONSxTABLES, null, args);
		} finally {
			close();
		}
	}
	
	public void reassignTable(UUID plan_id, Section section, Table table) {
		try {
			open();
			mDB.delete(DatabaseHelper.T_TEMP_SECTIONSxTABLES, "table_uuid=?", new String[] { String.valueOf(table.id) });
			ContentValues args = new ContentValues();
			args.put("section_uuid", String.valueOf(section.id));
			args.put("table_uuid", String.valueOf(table.id));
			mDB.insert(DatabaseHelper.T_TEMP_SECTIONSxTABLES, null, args);
		} finally {
			close();
		}
	}

	public void removeTemporaryTableAssignments(UUID table_id) {
		try {
			open();
			mDB.delete(DatabaseHelper.T_TEMP_SECTIONSxTABLES, "table_uuid=?", new String[] { String.valueOf(table_id) });
		} finally {
			close();
		}
	}
	
	public UUID readTemporarySectionForTable(UUID table_id) {
		UUID section_id = null;
		Cursor cursor = null;
		try {
			open();
			cursor = mDB.query(true, DatabaseHelper.T_TEMP_SECTIONSxTABLES, new String[] { "section_uuid" }, "table_uuid=?", new String[] { String.valueOf(table_id)}, null, null, null, null);
			if(cursor.moveToFirst()) {
				section_id = UUID.fromString(cursor.getString(0));
			}

		} catch(Exception e) {
			
		} finally {
			if(cursor!=null) {
				cursor.close();
			}
			close();
		}
		return section_id;
	}

	public Map<UUID, UUID> readTemporaryTableAssignments() {
		Map<UUID, UUID> assignments = new HashMap<UUID, UUID>();
		Cursor cursor = null;
		try {
			open();
			cursor = mDB.query(true, DatabaseHelper.T_TEMP_SECTIONSxTABLES, new String[] { "table_uuid, section_uuid" }, null, null, null, null, null, null);
			if(cursor.moveToFirst()) {
				do {
					assignments.put(UUID.fromString(cursor.getString(0)), UUID.fromString(cursor.getString(1)));
				} while(cursor.moveToNext());
			}

		} catch(Exception e) {
			
		} finally {
			if(cursor!=null) {
				cursor.close();
			}
			close();
		}
		return assignments;
	}

	public void dailyReset() {
		try {
			open();
			
			Cursor cursor = mDB.rawQuery("update sections set serviceround=0, dailysortorder=sortorder", null);
			cursor.moveToFirst();
			cursor.close();   
			
		} finally {
			close();
		}
	}
}
