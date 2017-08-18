package com.shaddyhollow.quickbud.datastore;

import java.util.UUID;

import android.content.ContentValues;
import android.database.Cursor;

import com.shaddyhollow.quicktable.models.Location;

public class LocationFactory extends BaseFactory<Location> {
	public String[] columns = new String[] { "uuid", "name" };

	@Override
	protected String getTableName() {
		return DatabaseHelper.T_LOCATIONS;
	}

	@Override
	public Location create(Location element) {
		try {
			open();
			UUID nextID = getNextID();
			
			ContentValues args = new ContentValues();
			args.put(columns[0], String.valueOf(nextID));
			args.put(columns[1], element.getName());
			
			mDB.insert(getTableName(), null, args);
			element.setId(nextID);
		} finally {
			close();
		}
		return element;
	}

	@Override
	public Location read(UUID id) {
		Location loc = null;
		Cursor cursor = null;
		try {
			open();

			cursor = mDB.query(true, getTableName(), columns, "uuid=?", new String[] { String.valueOf(id)}, null, null, null, null, null);
			if(cursor.moveToFirst()) {
				int col = 0;
				loc = new Location();
				loc.setId(UUID.fromString(cursor.getString(col++)));
				loc.setName(cursor.getString(col++));
			}
		} finally {
			if(cursor!=null) {
				cursor.close();
			}
			close();
		}
		return loc;
	}

	@Override
	public Location update(Location element) {
		try {
			open();

			ContentValues args = new ContentValues();
			args.put(columns[1], element.getName());
			
			mDB.update(getTableName(), args, "uuid=?", new String[] { String.valueOf(element.getId()) } );
		} finally {
			close();
		}
		return element;
	}

}
