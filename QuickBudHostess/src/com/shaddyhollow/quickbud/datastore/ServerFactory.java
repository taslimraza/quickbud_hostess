package com.shaddyhollow.quickbud.datastore;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.shaddyhollow.quickbud.Config;
import com.shaddyhollow.quicktable.models.Server;

public class ServerFactory extends BaseFactory<Server> {
	public String[] columns = new String[] { "uuid", "location_uuid", "name", "table_count", "colorstate" };
	private static ServerFactory instance = null;
	
	private ServerFactory(Context context) {
		this.mContext = context;
	}

	public static ServerFactory getInstance() {
		if(instance==null) {
			throw new RuntimeException("TableFactory must be initialized first with getInstance(context)");
		}
		return instance;
	}
	
	public static ServerFactory getInstance(Context context) {
		if(instance==null) {
			instance = new ServerFactory(context);
		}
		return instance;
	}
	

	@Override
	protected String getTableName() {
		return DatabaseHelper.T_SERVERS;
	}

	@Override
	public Server create(Server element) {
		try {
			open();
			ContentValues args = new ContentValues();
			int col = 0;
			if(element.getId()==null) {
				element.id = getNextID();
			}
			args.put(columns[col++], String.valueOf(element.getId()));
			args.put(columns[col++], String.valueOf(Config.getLocationID()));
			args.put(columns[col++], element.getName());
			args.put(columns[col++], element.getTables_served());
			args.put(columns[col++], element.colorstate);
			
			mDB.insert(getTableName(), null, args);
		} finally {
			close();
		}
		return element;
	}

	@Override
	public Server read(UUID id) {
		Server element = null;
		Cursor cursor = null;

		try {
			open();
			cursor = mDB.query(true, getTableName(), columns, "uuid=?", new String[] { String.valueOf(id)}, null, null, null, null);
			if(cursor.moveToFirst()) {
				int col = 0;
				element = new Server();
				element.setId(UUID.fromString(cursor.getString(col++)));
				col++; // ignore location_id
				element.setName(cursor.getString(col++));
				element.setTables_served(cursor.getInt(col++));
				element.colorstate = cursor.getInt(col++);
			}
		} finally {
			if(cursor!=null) {
				cursor.close();
			}
			close();
		}
		return element;
	}

	public List<Server> bulkRead(String where) {
		List<Server> servers = new ArrayList<Server>();
		Cursor cursor = null;
		try {
			open();

			cursor = mDB.query(true, getTableName(), columns, where, null, null, null, null, null);
			if(cursor.moveToFirst()) {
				do {
					Server element = new Server();
					int col = 0;
					element = new Server();
					element.setId(UUID.fromString(cursor.getString(col++)));
					col++; // ignore location_id
					element.setName(cursor.getString(col++));
					element.setTables_served(cursor.getInt(col++));
					element.colorstate = cursor.getInt(col++);
					servers.add(element);
				} while(cursor.moveToNext());
			}
			
		} finally {
			if(cursor!=null) {
				cursor.close();
			}
			close();
		}
		return servers;
	}


	@Override
	public Server update(Server element) {
		try {
			open();
			ContentValues args = new ContentValues();
			int col = 0;
			
			col++; // ignore id
			col++; // ignore location_id
			args.put(columns[col++], element.getName());
			args.put(columns[col++], element.getTables_served());
			args.put(columns[col++], element.colorstate);
			mDB.update(getTableName(), args, "uuid=?", new String[] { String.valueOf(element.getId()) } );
		} finally {
			close();
		}
		return element;
	}
	
	public void bulkDelete(String where) {
		try {
			open();
			
			List<Server> servers = bulkRead(where);
			for(Server server : servers) {
				delete(server.getId());
			}
		} finally {
			close();
		}
	}
	
	public void dailyReset() {
		try {
			open();
			
			List<Server> servers = bulkRead(null);
			for(Server server : servers) {
				server.colorstate = 0;
				server.min_party = 0;
				server.max_party = 0;
				server.setTables_served(0);
				update(server);
			}
			
			mDB.delete(DatabaseHelper.T_SECTIONSxSERVERS, null, null);

		} finally {
			close();
		}
	}
}
