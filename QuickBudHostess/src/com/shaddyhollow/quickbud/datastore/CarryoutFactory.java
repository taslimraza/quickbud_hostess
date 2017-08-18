package com.shaddyhollow.quickbud.datastore;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.content.Context;
import android.database.Cursor;

import com.shaddyhollow.quicktable.models.CarryOutVisit;


public class CarryoutFactory extends BaseFactory<CarryOutVisit> {
	private static CarryoutFactory instance = null;
	public static String tableName = DatabaseHelper.T_CARRYOUTS;
	public static String[] columns = new String[] { "_id", "uuid", "visit_uuid", "order_status", "patron_status", "name", 
													"phone_number", "created_at", "removed", "comments", "cartitems", "printfailures", "order_time" };

	private CarryoutFactory(Context context) {
		this.mContext = context;
	}

	public static CarryoutFactory getInstance() {
		if(instance==null) {
			throw new RuntimeException("CarryoutFactory must be initialized first with getInstance(context)");
		}
		return instance;
	}
	
	public static CarryoutFactory getInstance(Context context) {
		if(instance==null) {
			instance = new CarryoutFactory(context);
		}
		return instance;
	}

	public void bulkDelete(String where) {
		try {
			open();
			
			List<CarryOutVisit> visits = bulkRead(where);
			for(CarryOutVisit visit : visits) {
				delete(visit.getId());
			}
		} finally {
			close();
		}
	}
	
	public List<CarryOutVisit> bulkRead(String where) {
		List<CarryOutVisit> visits = new ArrayList<CarryOutVisit>();
		Cursor cursor = null;
		try {
			open();

			cursor = mDB.query(true, getTableName(), columns, where, null, null, null, null, null);
			if(cursor.moveToFirst()) {
				do {
					CarryOutVisit element = new CarryOutVisit();
					int col = 0;
					element = new CarryOutVisit();
					col++; // ignore _id
					element.setId(UUID.fromString(cursor.getString(col++)));
					try {
						element.setVisit_id(UUID.fromString(cursor.getString(col++)));
					} catch (IllegalArgumentException e) {
						// invalid UUID, probably null or blank
					}
					element.setOrderStatus(cursor.getString(col++));
					element.setPatronStatus(cursor.getString(col++));
					element.setName(cursor.getString(col++));
					element.setPhone_number(cursor.getString(col++));
					element.setCreated_at(cursor.getString(col++));
					element.setRemoved(cursor.getInt(col++));
					element.setSpecial_requests(cursor.getString(col++));
					element.setCartItemsFromString(cursor.getString(col++));
					element.setPrintFailures(cursor.getInt(col++));
					element.setOrder_time(cursor.getString(col++));
					visits.add(element);
				} while(cursor.moveToNext());
			}
			
		} finally {
			if(cursor!=null) {
				cursor.close();
			}
			close();
		}
		return visits;

	}
	
	@Override
	public CarryOutVisit create(CarryOutVisit element) {
		return null;
	}

	@Override
	public CarryOutVisit read(UUID id) {
		CarryOutVisit element = null;
		Cursor cursor = null;
		try {
			open();

			cursor = mDB.query(true, getTableName(), columns, "uuid=?", new String[] { id.toString() }, null, null, null, null);
			if(cursor.moveToFirst()) {
				do {
					int col = 0;
					element = new CarryOutVisit();
					col++; // ignore _id
					element.setId(UUID.fromString(cursor.getString(col++)));
					try {
						element.setVisit_id(UUID.fromString(cursor.getString(col++)));
					} catch (IllegalArgumentException e) {
						// invalid UUID, probably null or blank
					}
					element.setOrderStatus(cursor.getString(col++));
					element.setPatronStatus(cursor.getString(col++));
					element.setName(cursor.getString(col++));
					element.setPhone_number(cursor.getString(col++));
					element.setCreated_at(cursor.getString(col++));
					element.setRemoved(cursor.getInt(col++));
					element.setSpecial_requests(cursor.getString(col++));
					element.setCartItemsFromString(cursor.getString(col++));
					element.setPrintFailures(cursor.getInt(col++));
					element.setOrder_time(cursor.getString(col++));
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
	public CarryOutVisit update(CarryOutVisit element) {
		return null;
	}

	@Override
	protected String getTableName() {
		return tableName;
	}

}
