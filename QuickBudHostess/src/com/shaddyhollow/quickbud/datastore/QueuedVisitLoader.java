package com.shaddyhollow.quickbud.datastore;

import java.util.UUID;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;

import com.commonsware.cwac.loaderex.SQLiteCursorLoader;
import com.shaddyhollow.quicktable.models.QueuedVisit;

public class QueuedVisitLoader extends SQLiteCursorLoader {
	public static String tableName = "queuedvisits";
	public static String[] columns = new String[] { "_id", "uuid", "visit_uuid", "status", "party_size", 
											 "name", "phone_number", "low_wait_time", "high_wait_time", 
											 "order_in", "wheelchair_access", "high_chairs", "booster_seats", 
											 "created_at", "removed", "fromserver", "comments", "walkin" };
	
	public QueuedVisitLoader(Context context, SQLiteOpenHelper db) {
		super(context, db, "SELECT _id, uuid, visit_uuid, status, party_size, " +  
				  		   "name, phone_number, low_wait_time, high_wait_time, " + 
				  		   "order_in, wheelchair_access, high_chairs, " +
				  		   "booster_seats, created_at, removed, fromserver, comments, walkin " + 
				  		   "FROM " + DatabaseHelper.T_QUEUEDVISITS + " " +
				  		   "WHERE removed=0 order by created_at", null);
	}

	public QueuedVisit create(QueuedVisit element) {
		ContentValues args = new ContentValues();
		int col = 1;
		if(element.getId()==null) {
			element.setId(UUID.randomUUID());
		}
		args.put(columns[col++], String.valueOf(element.getId()));
		if(element.getVisit_id()!=null) {
			args.put(columns[col++], String.valueOf(element.getVisit_id()));
		} else {
			col++;
		}
		args.put(columns[col++], element.getStatus());
		args.put(columns[col++], element.getParty_size());
		args.put(columns[col++], element.getName());
		args.put(columns[col++], element.getPhone_number());
		args.put(columns[col++], element.getLow_wait_time());
		args.put(columns[col++], element.getHigh_wait_time());
		args.put(columns[col++], element.isOrder_in() ? 1 : 0);
		args.put(columns[col++], element.isWheel_chair_access() ? 1 : 0);
		args.put(columns[col++], element.getHigh_chairs());
		args.put(columns[col++], element.getBooster_seats());
		args.put(columns[col++], element.getCreated_at());
		args.put(columns[col++], 0);
		args.put(columns[col++], element.getFromServer());
		args.put(columns[col++], element.getSpecialRequests());
		args.put(columns[col++], element.getWalkIn());
		
		insert(tableName, null, args);
		return element;
	}

	public void markRemoved(UUID id) {
		ContentValues args = new ContentValues();
		args.put("removed", 1);
		update(tableName, args, "uuid=?", new String[] { id.toString() } );
	}

	public static QueuedVisit convertToObject(Cursor cursor) {
		QueuedVisit element = new QueuedVisit();
		int col=1;
		
		element.setId(UUID.fromString(cursor.getString(col++)));
		try {
			element.setVisit_id(UUID.fromString(cursor.getString(col++)));
		} catch (Exception e) {
			// ignore invalid visitid
		}
		element.setStatus(cursor.getString(col++));
		element.setParty_size(cursor.getInt(col++));
		element.setName(cursor.getString(col++));
		element.setPhone_number(cursor.getString(col++));
		element.setLow_wait_time(String.valueOf(cursor.getInt(col++)));
		element.setHigh_wait_time(String.valueOf(cursor.getInt(col++)));
		element.setOrder_in(cursor.getInt(col++)!=0);
		element.setWheel_chair_access(cursor.getInt(col++)!=0);
		element.setHigh_chairs(cursor.getInt(col++));
		element.setBooster_seats(cursor.getInt(col++));
		element.setCreated_at(cursor.getString(col++));
		element.setRemoved(cursor.getInt(col++));
		element.setFromServer(cursor.getInt(col++));
		element.setSpecialRequests(cursor.getString(col++));
		element.setWalkIn(cursor.getInt(col++)>0);
		return element;
	}
	
	public QueuedVisit update(QueuedVisit element) {
		ContentValues args = new ContentValues();
		int col = 1;
		
		col++; // ignore id
		if(element.getVisit_id()!=null) {
			args.put(columns[col++], String.valueOf(element.getVisit_id()));
		} else {
			col++;
		}
		args.put(columns[col++], element.getStatus());
		args.put(columns[col++], element.getParty_size());
		args.put(columns[col++], element.getName());
		args.put(columns[col++], element.getPhone_number());
		args.put(columns[col++], element.getLow_wait_time());
		args.put(columns[col++], element.getHigh_wait_time());
		args.put(columns[col++], element.isOrder_in() ? 1 : 0);
		args.put(columns[col++], element.isWheel_chair_access() ? 1 : 0);
		args.put(columns[col++], element.getHigh_chairs());
		args.put(columns[col++], element.getBooster_seats());
		col++; // don't update the created at field
//		args.put(columns[col++], element.getCreated_at());
		args.put(columns[col++], element.getRemoved());
		args.put(columns[col++], element.getFromServer());
		args.put(columns[col++], element.getSpecialRequests());
		args.put(columns[col++], element.getWalkIn() ? 1: 0);
		update(tableName, args, "uuid=?", new String[] { String.valueOf(element.getId()) } );
		return element;
	}
	
	public void bulkDelete(String where) {
		delete(tableName, where, null);
	}

	public void delete(UUID id) {
		delete(tableName, "uuid=?", new String[] {id.toString()});
	}

}
