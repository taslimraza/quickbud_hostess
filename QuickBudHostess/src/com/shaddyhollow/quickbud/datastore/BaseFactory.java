package com.shaddyhollow.quickbud.datastore;

import java.util.UUID;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.shaddyhollow.quicktable.models.Identifiable;

public abstract class BaseFactory<T extends Identifiable> {
	private static Boolean mutex = Boolean.TRUE;
	protected Context mContext;
	private static Integer opencount = 0;
	private static DatabaseHelper mDbHelper;
	protected static SQLiteDatabase mDB;

	public abstract T create(T element);
	public abstract T read(UUID id);
	public abstract T update(T element);

	protected abstract String getTableName();
	
	public T createOrUpdate(T element) {
		T updatedElement;
		if(exists(element.getId())) {
			updatedElement = update(element);
		} else {
			updatedElement = create(element);
		}
		return updatedElement;
	}
	
	public void delete(UUID id) {
		String sql = "uuid=?";
		try {
			open();
			String args[] = new String[] { String.valueOf(id) };
			mDB.delete(getTableName(), sql, args);
		} finally {
			close();
		}
	}
	
	public boolean exists(UUID id) {
		return read(id)!=null;
	}
	
	protected UUID getNextID(String tableName) {
		return UUID.randomUUID();
	}
	
	public UUID getNextID() {
		return getNextID(getTableName());
	}
	
	protected void open() throws SQLException {
		synchronized(mutex) {
			opencount++;
			if(mDbHelper==null) {
				mDbHelper = new DatabaseHelper(mContext);
				mDbHelper.close();
				mDB = mDbHelper.getWritableDatabase();
			}
		}
	}

	protected void close() {
		synchronized(mutex) {
			if(--opencount==0) {
				if(mDbHelper!=null) {
					mDbHelper.close();
					mDbHelper = null;
				}
				if(mDB!=null) {
					mDB.close();
				}
			}
		}
	}


}
