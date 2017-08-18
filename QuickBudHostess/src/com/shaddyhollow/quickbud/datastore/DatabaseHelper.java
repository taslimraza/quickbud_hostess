package com.shaddyhollow.quickbud.datastore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

@SuppressLint("UseSparseArrays")
@SuppressWarnings("nls")
public class DatabaseHelper extends SQLiteOpenHelper {
	public static final int CURRENT_DATABASE_VERSION = 34;
	private static final String DATABASE_NAME = "dinendash.db";

	public static final String KEY_ROWID = "_id";

	public static final String T_LOCATIONS = "locations";
	public static final String T_SERVERS = "servers";
	public static final String T_FLOORPLANS = "floorplans";
	public static final String T_SECTIONPLANS = "sectionplans";
	public static final String T_SECTIONS = "sections";
	public static final String T_TABLES = "tables";
	public static final String T_TABLEPOINTS = "tablepoints";
	public static final String T_SECTIONSxTABLES = "sections_x_tables";
	public static final String T_QUEUEDVISITS = "queuedvisits";
	public static final String T_TEMP_SECTIONSxTABLES = "temp_sections_x_tables";
	public static final String T_CARRYOUTS = "carryouts";
	public static final String T_SECTIONSxSERVERS = "sections_x_servers";
	
	public static final String T_SEATEDVISITS = "visits";
	
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, CURRENT_DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		onUpgrade(db, 0, DatabaseHelper.CURRENT_DATABASE_VERSION);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if(oldVersion<10) {
			db.execSQL("DROP TABLE IF EXISTS " + T_LOCATIONS);
			db.execSQL("DROP TABLE IF EXISTS " + T_SERVERS);
			db.execSQL("DROP TABLE IF EXISTS " + T_FLOORPLANS);
			db.execSQL("DROP TABLE IF EXISTS " + T_SECTIONPLANS);
			db.execSQL("DROP TABLE IF EXISTS " + T_SECTIONS);
			db.execSQL("DROP TABLE IF EXISTS " + T_TABLES);
			db.execSQL("DROP TABLE IF EXISTS " + T_TABLEPOINTS);
			db.execSQL("DROP TABLE IF EXISTS " + T_SECTIONSxTABLES);
			db.execSQL("DROP TABLE IF EXISTS " + T_SEATEDVISITS);

			db.execSQL("DROP TABLE IF EXISTS " + T_SECTIONSxSERVERS);
			

			db.execSQL("create table " + T_LOCATIONS + " (id integer, name text);");
			db.execSQL("create table " + T_SERVERS + " (id integer, location_id integer, name text, table_count integer);");
			db.execSQL("create table " + T_FLOORPLANS + " (id integer, location_id integer, name text);");
			db.execSQL("create table " + T_SECTIONPLANS + " (id integer, location_id integer, name text);");
			db.execSQL("create table " + T_SECTIONS + " (id integer, sectionplan_id integer, name text, colorid integer, status integer);");
			db.execSQL("create table " + T_TABLES + " (id integer, floorplan_id integer, name text, type string, seats integer, status string);");
			db.execSQL("create table " + T_TABLEPOINTS + " (_id integer primary key autoincrement, table_id integer, row integer, col integer);");
			db.execSQL("create table " + T_SECTIONSxTABLES + " (sectionplan_id integer, section_id integer, table_id integer);");
			db.execSQL("create table " + T_SEATEDVISITS + " (id integer, table_id integer, server_id integer, name text, party_size integer, seating_time datetime)");
			db.execSQL("create table " + T_SECTIONSxSERVERS + " (_id integer primary key autoincrement, server_id integer, section_id integer);");
		}
		if(oldVersion<11) {
			db.execSQL("alter table " + T_SERVERS + " add column colorstate integer");
			db.execSQL("update " + T_SERVERS + " set colorstate=0");
		}
		if(oldVersion<12) {
			// convert ids to uuids
			
			// add new uuid fields
			db.execSQL("alter table " + T_LOCATIONS + " add column uuid text");
			db.execSQL("alter table " + T_SERVERS + " add column uuid text");
			db.execSQL("alter table " + T_SERVERS + " add column location_uuid text");
			db.execSQL("alter table " + T_FLOORPLANS + " add column uuid text");
			db.execSQL("alter table " + T_FLOORPLANS + " add column location_uuid text");
			db.execSQL("alter table " + T_SECTIONPLANS + " add column uuid text");
			db.execSQL("alter table " + T_SECTIONPLANS + " add column location_uuid text");
			db.execSQL("alter table " + T_SECTIONS + " add column uuid text");
			db.execSQL("alter table " + T_SECTIONS + " add column sectionplan_uuid text");
			db.execSQL("alter table " + T_TABLES + " add column uuid text");
			db.execSQL("alter table " + T_TABLES + " add column floorplan_uuid text");
			db.execSQL("alter table " + T_TABLEPOINTS + " add column table_uuid text");
			db.execSQL("alter table " + T_SEATEDVISITS + " add column uuid text");
			db.execSQL("alter table " + T_SEATEDVISITS + " add column table_uuid text");
			db.execSQL("alter table " + T_SEATEDVISITS + " add column server_uuid text");
			db.execSQL("alter table " + T_SECTIONSxTABLES + " add column sectionplan_uuid text");
			db.execSQL("alter table " + T_SECTIONSxTABLES + " add column section_uuid text");
			db.execSQL("alter table " + T_SECTIONSxTABLES + " add column table_uuid text");
			db.execSQL("alter table " + T_SECTIONSxSERVERS + " add column server_uuid text");
			db.execSQL("alter table " + T_SECTIONSxSERVERS + " add column section_uuid text");

			String[] tablesWithID = new String[] { T_LOCATIONS, T_SERVERS, T_FLOORPLANS, T_SECTIONPLANS, T_SECTIONS, T_TABLES };
			String[] tablesWithLocationID = new String[] { T_FLOORPLANS, T_SECTIONPLANS };
			String[] tablesWithSectionPlanID = new String[] { T_SECTIONS, T_SECTIONSxTABLES };
			String[] tablesWithSectionID = new String[] { T_SECTIONSxTABLES };
			String[] tablesWithTableID = new String[] { T_TABLEPOINTS, T_SECTIONSxTABLES };
			String[] tablesWithFloorplanID = new String[] { T_TABLES };
			
			// delete data that is going to be downloaded
			db.delete(T_SERVERS, null, null);
			
			// delete data that is temporal
			db.delete(T_SEATEDVISITS, null, null);
			db.delete(T_SECTIONSxSERVERS, null, null);
			
			// map original ids to new UUIDs
			HashMap<String, HashMap<Integer, UUID>> remap = new HashMap<String, HashMap<Integer, UUID>>();
			
			for(String table : tablesWithID) {
				HashMap<Integer, UUID> keyMap = new HashMap<Integer, UUID>();
				remap.put(table, keyMap);
				
				Cursor cursor =	db.rawQuery("select id from " + table, null);
				if(cursor.moveToFirst()) {
					do {
						keyMap.put(cursor.getInt(0), UUID.randomUUID());
					} while(cursor.moveToNext());
				}
				if(cursor!=null) {
					cursor.close();
				}
			}
			
			// update primary ids
			for(String table : tablesWithID) {
				HashMap<Integer, UUID> remapValues = remap.get(table);
				for(int id : remapValues.keySet()) {
					ContentValues args = new ContentValues();
					args.put("uuid", String.valueOf(remapValues.get(id)));
					db.update(table, args, "id=?", new String[] { String.valueOf(id) } );
				}
			}
			// update location ids
			for(String table : tablesWithLocationID) {
				HashMap<Integer, UUID> remapValues = remap.get(T_LOCATIONS);
				for(int id : remapValues.keySet()) {
					ContentValues args = new ContentValues();
					args.put("location_uuid", String.valueOf(remapValues.get(id)));
					db.update(table, args, "location_id=?", new String[] { String.valueOf(id) } );
				}
			}
			// update sectionplan ids
			for(String table : tablesWithSectionPlanID) {
				HashMap<Integer, UUID> remapValues = remap.get(T_SECTIONPLANS);
				for(int id : remapValues.keySet()) {
					ContentValues args = new ContentValues();
					args.put("sectionplan_uuid", String.valueOf(remapValues.get(id)));
					db.update(table, args, "sectionplan_id=?", new String[] { String.valueOf(id) } );
				}
			}
			// update sectionplan ids
			for(String table : tablesWithSectionID) {
				HashMap<Integer, UUID> remapValues = remap.get(T_SECTIONS);
				for(int id : remapValues.keySet()) {
					ContentValues args = new ContentValues();
					args.put("section_uuid", String.valueOf(remapValues.get(id)));
					db.update(table, args, "section_id=?", new String[] { String.valueOf(id) } );
				}
			}
			// update floorplan ids
			for(String table : tablesWithFloorplanID) {
				HashMap<Integer, UUID> remapValues = remap.get(T_FLOORPLANS);
				for(int id : remapValues.keySet()) {
					ContentValues args = new ContentValues();
					args.put("floorplan_uuid", String.valueOf(remapValues.get(id)));
					db.update(table, args, "floorplan_id=?", new String[] { String.valueOf(id) } );
				}
			}
			// update tables ids
			for(String table : tablesWithTableID) {
				HashMap<Integer, UUID> remapValues = remap.get(T_TABLES);
				for(int id : remapValues.keySet()) {
					ContentValues args = new ContentValues();
					args.put("table_uuid", String.valueOf(remapValues.get(id)));
					db.update(table, args, "table_id=?", new String[] { String.valueOf(id) } );
				}
			}

		}
		if(oldVersion<13) {
			// create queued visits
			db.execSQL("create table " + T_QUEUEDVISITS + " (uuid text, visit_uuid text, status text, party_size text, name text, phone_number text, low_wait_time integer, high_wait_time integer, order_in integer, wheelchair_access integer, high_chairs integer, booster_seats integer, created_at string);");
		}
		if(oldVersion<14) {
			db.execSQL("alter table " + T_TABLES + " add column group_uuid text");
			db.execSQL("update " + T_TABLES + " set group_uuid = uuid");
		}
		if(oldVersion<15) {
			db.execSQL("alter table " + T_QUEUEDVISITS + " add column removed integer default 0");
		}
		if(oldVersion<22) {
			// add _id field to queued visits to use SQLiteCursorLoader
			String tablename = T_QUEUEDVISITS;
			String selectColumns = "uuid, visit_uuid, status, party_size, name, phone_number, low_wait_time, high_wait_time, order_in, wheelchair_access, high_chairs, booster_seats, created_at, removed";
			db.execSQL("ALTER TABLE " + tablename + " RENAME TO " + tablename + "_backup");
			db.execSQL("CREATE TABLE " + tablename + "(_id integer primary key autoincrement, uuid text, visit_uuid text, status text, party_size text, name text, phone_number text, low_wait_time integer, high_wait_time integer, order_in integer, wheelchair_access integer, high_chairs integer, booster_seats integer, created_at string, removed integer default 0);");
			db.execSQL("INSERT INTO " + tablename + "(" + selectColumns + ") SELECT " + selectColumns + "  FROM " + tablename + "_backup");
			db.execSQL("DROP TABLE " + tablename + "_backup");
		}
		if(oldVersion<23) {
			db.execSQL("alter table " + T_QUEUEDVISITS + " add column fromserver integer default 0");
		}
		if(oldVersion<24) {
			db.execSQL("DROP TABLE IF EXISTS " + T_TEMP_SECTIONSxTABLES);
			db.execSQL("create table " + T_TEMP_SECTIONSxTABLES + " (section_uuid text, table_uuid text);");
		}
		if(oldVersion<25) {
			db.execSQL("ALTER TABLE " + T_QUEUEDVISITS + " add column comments text");
		}
		if(oldVersion<26) {
			db.execSQL("ALTER TABLE " + T_SEATEDVISITS + " add column comments text");
		}
		if(oldVersion<27) {
			db.execSQL("ALTER TABLE " + T_SEATEDVISITS + " add column order_in integer default 0");
		}
		if(oldVersion<28) {
//			This can be skipped now because it is completely replaced by v29
//			db.execSQL("CREATE TABLE " + T_CARRYOUTS + "(_id integer primary key autoincrement, uuid text, visit_uuid text, name text, phone_number text, status text, cartitems text, comments text, created_at string, removed integer default 0);");
		}
		if(oldVersion<29) {
			db.execSQL("DROP TABLE IF EXISTS " + T_CARRYOUTS);
			db.execSQL("CREATE TABLE " + T_CARRYOUTS + "(_id integer primary key autoincrement, uuid text, visit_uuid text, name text, phone_number text, order_status text, patron_status text, cartitems text, comments text, created_at string, address_line text, city text, state text, zip text, removed integer default 0);");
		}
		if(oldVersion<30) {
			db.execSQL("ALTER TABLE " + T_CARRYOUTS + " add column printfailures integer default 0");
		}
		if(oldVersion<31) {
			db.execSQL("ALTER TABLE " + T_SECTIONS + " add column sortorder integer");

			TreeMap<UUID, ArrayList<UUID>> sectiongroups = new TreeMap<UUID, ArrayList<UUID>>();
			Cursor cursor =	db.rawQuery("select sectionplan_uuid, uuid from " + T_SECTIONS, null);
			if(cursor.moveToFirst()) {
				do {
					UUID sectionPlanID = UUID.fromString(cursor.getString(0));
					UUID sectionID = UUID.fromString(cursor.getString(1));

					if(!sectiongroups.containsKey(sectionPlanID)) {
						sectiongroups.put(sectionPlanID, new ArrayList<UUID>());
					}
					ArrayList<UUID> sections = sectiongroups.get(sectionPlanID);
					sections.add(sectionID);
				} while(cursor.moveToNext());
			}
			if(cursor!=null) {
				cursor.close();
			}
			
			for(UUID sectionPlanID : sectiongroups.keySet()) {
				ArrayList<UUID> sections = sectiongroups.get(sectionPlanID);
				int position = 0;
				for(UUID sectionID : sections) {
					ContentValues args = new ContentValues();
					args.put("sortorder", position++);
					db.update(T_SECTIONS, args, "uuid=?", new String[] { String.valueOf(sectionID) } );
				}
			}
		}
		if(oldVersion<32) {
			db.execSQL("ALTER TABLE " + T_SECTIONS + " add column serviceround integer");
			db.execSQL("ALTER TABLE " + T_SECTIONS + " add column dailysortorder integer");
			TreeMap<UUID, ArrayList<UUID>> sectiongroups = new TreeMap<UUID, ArrayList<UUID>>();
			Cursor cursor =	db.rawQuery("select sectionplan_uuid, uuid from " + T_SECTIONS, null);
			if(cursor.moveToFirst()) {
				do {
					UUID sectionPlanID = UUID.fromString(cursor.getString(0));
					UUID sectionID = UUID.fromString(cursor.getString(1));

					if(!sectiongroups.containsKey(sectionPlanID)) {
						sectiongroups.put(sectionPlanID, new ArrayList<UUID>());
					}
					ArrayList<UUID> sections = sectiongroups.get(sectionPlanID);
					sections.add(sectionID);
				} while(cursor.moveToNext());
			}
			if(cursor!=null) {
				cursor.close();
			}
			
			for(UUID sectionPlanID : sectiongroups.keySet()) {
				ArrayList<UUID> sections = sectiongroups.get(sectionPlanID);
				int position = 0;
				for(UUID sectionID : sections) {
					ContentValues args = new ContentValues();
					args.put("sortorder", position);
					args.put("dailysortorder", position);
					args.put("serviceround", 0);
					position++;
					db.update(T_SECTIONS, args, "uuid=?", new String[] { String.valueOf(sectionID) } );
				}
			}
		}
		if(oldVersion<33) {
			db.execSQL("ALTER TABLE " + T_CARRYOUTS + " add column order_time text");
		}
		if(oldVersion<34){
			db.execSQL("ALTER TABLE " + T_QUEUEDVISITS + " add column walkin integer default 0");
		}
	}
	
}


