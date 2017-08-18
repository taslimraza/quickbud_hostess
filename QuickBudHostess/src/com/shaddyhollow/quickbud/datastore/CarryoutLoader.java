package com.shaddyhollow.quickbud.datastore;

import java.util.UUID;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;

import com.commonsware.cwac.loaderex.SQLiteCursorLoader;
import com.shaddyhollow.quicktable.models.CarryOutVisit;

public class CarryoutLoader extends SQLiteCursorLoader {
	public static String tableName = DatabaseHelper.T_CARRYOUTS;
	public static String[] columns = new String[] { "_id", "uuid", "visit_uuid", "order_status", "patron_status", "name", 
													"phone_number", "created_at", "removed", "comments", "cartitems", "address_line", "city", "state", "zip",
													"printfailures", "order_time"};
	
	public CarryoutLoader(Context context, SQLiteOpenHelper db) {
		super(context, db, "SELECT _id, uuid, visit_uuid, order_status, patron_status, name, phone_number, " +  
				  		   "created_at, removed, comments, cartitems, address_line, city, state, zip, printfailures, order_time " + 
				  		   "FROM " + tableName + " " +
				  		   "WHERE removed=0 order by created_at", null);
	}

	public CarryOutVisit create(CarryOutVisit element) {
		ContentValues args = new ContentValues();
		int col = 1; // skip _id
		if(element.getId()==null) {
			element.setId(UUID.randomUUID());
		}
		args.put(columns[col++], String.valueOf(element.getId()));
		args.put(columns[col++], String.valueOf(element.getVisit_id()));
		args.put(columns[col++], element.getOrderStatus());
		args.put(columns[col++], element.getPatronStatus());
		args.put(columns[col++], element.getName());
		args.put(columns[col++], element.getPhone_number());
		args.put(columns[col++], element.getCreated_at());
		args.put(columns[col++], element.getRemoved());
		args.put(columns[col++], element.getSpecial_requests());
		args.put(columns[col++], element.getCartItemsString());
		args.put(columns[col++], element.getAddressLine());
		args.put(columns[col++], element.getCity());
		args.put(columns[col++], element.getState());
		args.put(columns[col++], element.getZip());
		args.put(columns[col++], element.getPrintFailures());
		args.put(columns[col++], element.getOrder_time());
		
		insert(tableName, null, args);
		return element;
	}
	
	public CarryOutVisit update(CarryOutVisit element) {
		ContentValues args = new ContentValues();
		int col = 1;  // skip _id
		
		args.put(columns[col++], String.valueOf(element.getId()));
		args.put(columns[col++], String.valueOf(element.getVisit_id()));
		args.put(columns[col++], element.getOrderStatus());
		args.put(columns[col++], element.getPatronStatus());
		args.put(columns[col++], element.getName());
		args.put(columns[col++], element.getPhone_number());
		args.put(columns[col++], element.getCreated_at());
		args.put(columns[col++], element.getRemoved());
		args.put(columns[col++], element.getSpecial_requests());
		args.put(columns[col++], element.getCartItemsString());
		args.put(columns[col++], element.getAddressLine());
		args.put(columns[col++], element.getCity());
		args.put(columns[col++], element.getState());
		args.put(columns[col++], element.getZip());
		args.put(columns[col++], element.getPrintFailures());
		args.put(columns[col++], element.getOrder_time());
		update(tableName, args, "uuid=?", new String[] { String.valueOf(element.getId()) } );
		return element;
	}
	

	public void markRemoved(UUID id) {
		ContentValues args = new ContentValues();
		args.put("removed", 1);
		update(tableName, args, "uuid=?", new String[] { id.toString() } );
	}

	public static CarryOutVisit convertToObject(Cursor cursor) {
		CarryOutVisit element = new CarryOutVisit();
		int col=1; // skip _id
		
		element.setId(UUID.fromString(cursor.getString(col++)));
		try {
			element.setVisit_id(UUID.fromString(cursor.getString(col++)));
		} catch (IllegalArgumentException e) {
			// ignore invalid visitid
		}

		element.setOrderStatus(cursor.getString(col++));
		element.setPatronStatus(cursor.getString(col++));
		element.setName(cursor.getString(col++));
		element.setPhone_number(cursor.getString(col++));
		element.setCreated_at(cursor.getString(col++));
		element.setRemoved(cursor.getInt(col++));
		element.setSpecial_requests(cursor.getString(col++));
		element.setCartItemsFromString(cursor.getString(col++));
		element.setAddressLine(cursor.getString(col++));
		element.setCity(cursor.getString(col++));
		element.setState(cursor.getString(col++));
		element.setZip(cursor.getString(col++));
		element.setPrintFailures(cursor.getInt(col++));
		element.setOrder_time(cursor.getString(col++));
		return element;
	}
	
	public void updateStatus(UUID id, String order_status) {
		ContentValues args = new ContentValues();
		args.put("order_status", order_status);
		update(tableName, args, "uuid=?", new String[] { id.toString() } );
	}
	
	public void bulkDelete(String where) {
		delete(tableName, where, null);
	}

	public void delete(UUID id) {
		delete(tableName, "uuid=?", new String[] {id.toString()});
	}

}
