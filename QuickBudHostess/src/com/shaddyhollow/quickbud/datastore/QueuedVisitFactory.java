package com.shaddyhollow.quickbud.datastore;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.content.Context;
import android.database.Cursor;

import com.shaddyhollow.quicktable.models.QueuedVisit;


public class QueuedVisitFactory extends BaseFactory<QueuedVisit> {
	private static QueuedVisitFactory instance = null;
	public static String tableName = "queuedvisits";
	public static String[] columns = new String[] { "_id", "uuid", "visit_uuid", "status", "party_size", 
											 "name", "phone_number", "low_wait_time", "high_wait_time", 
											 "order_in", "wheelchair_access", "high_chairs", "booster_seats", 
											 "created_at", "removed", "fromserver", "comments" };

	private QueuedVisitFactory(Context context) {
		this.mContext = context;
	}

	public static QueuedVisitFactory getInstance() {
		if(instance==null) {
			throw new RuntimeException("TableFactory must be initialized first with getInstance(context)");
		}
		return instance;
	}
	
	public static QueuedVisitFactory getInstance(Context context) {
		if(instance==null) {
			instance = new QueuedVisitFactory(context);
		}
		return instance;
	}

	public void bulkDelete(String where) {
		try {
			open();
			
			List<QueuedVisit> visits = bulkRead(where);
			for(QueuedVisit visit : visits) {
				delete(visit.getId());
			}
		} finally {
			close();
		}
	}
	
	public List<QueuedVisit> bulkRead(String where) {
		List<QueuedVisit> queuedVisits = new ArrayList<QueuedVisit>();
		Cursor cursor = null;
		try {
			open();

			cursor = mDB.query(true, getTableName(), columns, where, null, null, null, null, null);
			if(cursor.moveToFirst()) {
				do {
					QueuedVisit element = new QueuedVisit();
					int col = 0;
					element = new QueuedVisit();
					col++; // ignore _id
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
					element.setLow_wait_time(cursor.getString(col++));
					element.setHigh_wait_time(cursor.getString(col++));
					element.setOrder_in(cursor.getInt(col++)!=0);
					element.setWheel_chair_access(cursor.getInt(col++)!=0);
					element.setHigh_chairs(cursor.getInt(col++));
					element.setBooster_seats(cursor.getInt(col++));
					element.setCreated_at(cursor.getString(col++));
					element.setRemoved(cursor.getInt(col++));
					element.setFromServer(cursor.getInt(col++));
					element.setSpecialRequests(cursor.getString(col++));
					queuedVisits.add(element);
				} while(cursor.moveToNext());
			}
			
		} finally {
			if(cursor!=null) {
				cursor.close();
			}
			close();
		}
		return queuedVisits;

	}
	
	@Override
	public QueuedVisit create(QueuedVisit element) {
		return null;
	}

	@Override
	public QueuedVisit read(UUID id) {
		QueuedVisit element = null;
		Cursor cursor = null;
		try {
			open();

			cursor = mDB.query(true, getTableName(), columns, "uuid=?", new String[] { id.toString() }, null, null, null, null);
			if(cursor.moveToFirst()) {
				do {
					int col = 0;
					element = new QueuedVisit();
					col++; // ignore _id
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
				} while(cursor.moveToNext());
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
	public QueuedVisit update(QueuedVisit element) {
		return null;
	}

	@Override
	protected String getTableName() {
		return tableName;
	}

}
