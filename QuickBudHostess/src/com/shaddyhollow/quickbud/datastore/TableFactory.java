package com.shaddyhollow.quickbud.datastore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.shaddyhollow.quicktable.models.SeatedVisit;
import com.shaddyhollow.quicktable.models.Table;
import com.shaddyhollow.quicktable.models.Table.Status;
import com.shaddyhollow.util.Point;

public class TableFactory extends BaseFactory<Table> {
	public String[] columns = new String[] { "uuid", "floorplan_uuid", "name", "type", "seats", "status", "group_uuid" };
	public String[] seatedVisitColumns = new String[] { "uuid", "table_uuid", "server_uuid", "name", "party_size", "seating_time", "comments", "order_in" };
	@SuppressLint("SimpleDateFormat")
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static TableFactory instance = null;
	
	private TableFactory(Context context) {
		this.mContext = context;
	}

	public static TableFactory getInstance() {
		if(instance==null) {
			throw new RuntimeException("TableFactory must be initialized first with getInstance(context)");
		}
		return instance;
	}
	
	public static TableFactory getInstance(Context context) {
		if(instance==null) {
			instance = new TableFactory(context);
		}
		return instance;
	}
	
	@Override
	protected String getTableName() {
		return DatabaseHelper.T_TABLES;
	}

	@Override
	public Table create(Table element) {
		throw new RuntimeException("Tables must be created from Floorplans");
	}
	
	public void create(UUID floorplan_id, Table element) {
		try {
			open();
			ContentValues args = new ContentValues();
			if(element.getId()==null) {
				element.id = getNextID();
			}
			int col=0;
			args.put(columns[col++], String.valueOf(element.id));
			args.put(columns[col++], String.valueOf(floorplan_id));
			args.put(columns[col++], element.getName());
			args.put(columns[col++], element.table_type);
			args.put(columns[col++], element.seats);
			args.put(columns[col++], element.status);
			args.put(columns[col++], String.valueOf(element.group_id));
			
			mDB.insert(getTableName(), null, args);
			updateTablePoints(element);
		} finally {
			close();
		}
	}

	@Override
	public Table read(UUID id) {
		Table table = null;
		Cursor cursor = null;

		try {
			open();

			cursor = mDB.query(true, getTableName(), columns, "uuid=?", new String[] { String.valueOf(id)}, null, null, null, null, null);
			if(cursor.moveToFirst()) {
				table = readCols(cursor);
				table.position = readTablePoints(table.getId());

				readSeatedVisit(table);
			}
			
		} finally {
			if(cursor!=null) {
				cursor.close();
			}
			close();
		}
		return table;
	}

	@Override
	public Table update(Table element) {
		try {
			open();
			
			ContentValues args = new ContentValues();
			args.put(columns[1], String.valueOf(element.floorplan_id));
			args.put(columns[2], element.getName());
			args.put(columns[3], element.table_type);
			args.put(columns[4], element.seats);
			args.put(columns[5], element.status);
			args.put(columns[6], String.valueOf(element.group_id));
			
			mDB.update(getTableName(), args, "uuid=?", new String[] { String.valueOf(element.getId()) } );
			updateTablePoints(element);
			createSeatedVisit(element.getId(), element.seated_visit);
		} finally {
			close();
		}
		return element;
	}

	@SuppressLint("UseSparseArrays")
	public Map<UUID, Table> bulkRead(String where) {
		Map<UUID, Table> tables = new HashMap<UUID, Table>();
		Cursor cursor = null;
		try {
			open();

			cursor = mDB.query(true, getTableName(), columns, where, null, null, null, null, null, null);
			if(cursor.moveToFirst()) {
				do {
					Table table = readCols(cursor);
					tables.put(table.id, table);
					readSeatedVisit(table);
				} while(cursor.moveToNext());
			}
			
			for(Table table : tables.values()) {
				table.position = readTablePoints(table.getId());
			}
		} finally {
			if(cursor!=null) {
				cursor.close();
			}
			close();
		}
		return tables;
	}

	private Table readCols(Cursor cursor) {
		int col = 0;
		Table table = new Table();
		
		table.setId(UUID.fromString(cursor.getString(col++)));
		table.floorplan_id = UUID.fromString(cursor.getString(col++));
		table.name = cursor.getString(col++);
		table.table_type = cursor.getString(col++);
		table.seats = cursor.getInt(col++);
		table.status = cursor.getString(col++);
		try {
			table.group_id = UUID.fromString(cursor.getString(col++));
		} catch (Exception e) {
			table.group_id = table.id;
		}
		
		if(table.table_type==null) {
			table.table_type="Table";
		}
		return table;
	}

	@Override
	public void delete(UUID id) {
		try {
			open();
			Table table = read(id);
			if(table!=null) {
				deleteTablePoints(table);
				clearVisit(table.getId());
				mDB.delete(DatabaseHelper.T_TEMP_SECTIONSxTABLES, "table_uuid=?", new String[] { String.valueOf(id) });
				mDB.delete(DatabaseHelper.T_SECTIONSxTABLES, "table_uuid=?", new String[] { String.valueOf(id) });
				mDB.delete(DatabaseHelper.T_SEATEDVISITS, "table_uuid=?", new String[] { String.valueOf(id) });
				mDB.delete(getTableName(), "uuid=?", new String[] { String.valueOf(id) });
			}
		} finally {
			close();
		}
	}
	
	public void bulkDelete(String where) {
		try {
			open();
			Collection<Table> tables = bulkRead(where).values();
			for(Table table : tables) {
				delete(table.id);
			}
		} finally {
			close();
		}
	}
		
	private List<Point> readTablePoints(UUID table_id) {
		String[] columns = new String[]{ "row", "col" };
		List<Point> points = new ArrayList<Point>();
		Cursor cursor = mDB.query(true, DatabaseHelper.T_TABLEPOINTS, columns , "table_uuid=?", new String[] { String.valueOf(table_id)}, null, null, null, null, null);
		while(cursor.moveToNext()) {
			int row = cursor.getInt(0);
			int col = cursor.getInt(1);
			
			points.add(new Point(row, col));
		}
		if(cursor!=null) {
			cursor.close();
		}
		return points;
	}
	
	private void updateTablePoints(Table table) {
		deleteTablePoints(table);
		
		for(Point point : table.position) {
			ContentValues args = new ContentValues();
			args.put("table_uuid", String.valueOf(table.getId()));
			args.put("row", point.row);
			args.put("col", point.column);
			mDB.insert(DatabaseHelper.T_TABLEPOINTS, null, args);
		}
	}
	
	private void deleteTablePoints(Table table) {
		mDB.delete(DatabaseHelper.T_TABLEPOINTS, "table_uuid=?", new String[] { String.valueOf(table.getId()) });
	}
	
	private void readSeatedVisit(Table table) {
		Cursor cursor = mDB.query(true, DatabaseHelper.T_SEATEDVISITS, seatedVisitColumns, "table_uuid=?", new String[] { String.valueOf(table.getId()) }, null, null, null, null, null);
		if(cursor.moveToFirst()) {
			SeatedVisit visit = new SeatedVisit();
			int col = 0;
			
			visit.id = UUID.fromString(cursor.getString(col++));
			col++; // skip table_id
			visit.server_id = UUID.fromString(cursor.getString(col++));
			visit.name = cursor.getString(col++);
			visit.party_size = cursor.getInt(col++);
			try {
				String dateText = cursor.getString(col++);
//				visit.seating_time = dateFormat.parse(dateText);
				visit.seating_time = dateText;
			} catch(Exception e) {
			}
			visit.comment = cursor.getString(col++);
			visit.order_in = cursor.getInt(col++)>0;
			table.seated_visit = visit;
		}
		if(cursor!=null) {
			cursor.close();
		}
	}
	
	public void createSeatedVisit(UUID table_id, SeatedVisit visit) {
		try {
			open();
			
			clearVisit(table_id);
			if(visit!=null) {
				ContentValues args = new ContentValues();
				if(visit.id==null) {
					visit.id = getNextID(DatabaseHelper.T_SEATEDVISITS);
				}
				int col=0;
				args.put(seatedVisitColumns[col++], String.valueOf(visit.id));
				args.put(seatedVisitColumns[col++], String.valueOf(table_id));
				args.put(seatedVisitColumns[col++], String.valueOf(visit.server_id));
				args.put(seatedVisitColumns[col++], visit.name);
				args.put(seatedVisitColumns[col++], visit.party_size);
				args.put(seatedVisitColumns[col++], dateFormat.format(new Date()));
				args.put(seatedVisitColumns[col++], visit.comment);
				args.put(seatedVisitColumns[col++], visit.order_in ? 1 : 0);
				mDB.insert(DatabaseHelper.T_SEATEDVISITS, null, args);
			}
		} finally {
			close();
		}
	}
	
	public void updateSeatedVisit(UUID table_id, SeatedVisit visit) {
		try {
			open();
			
			clearVisit(table_id);
			if(visit!=null) {
				ContentValues args = new ContentValues();
				if(visit.id==null) {
					return;
				}
				int col=0;
				col++; // skip visit_id
				args.put(seatedVisitColumns[col++], String.valueOf(table_id));
				args.put(seatedVisitColumns[col++], String.valueOf(visit.server_id));
				args.put(seatedVisitColumns[col++], visit.name);
				args.put(seatedVisitColumns[col++], visit.party_size);
				args.put(seatedVisitColumns[col++], dateFormat.format(new Date()));
				args.put(seatedVisitColumns[col++], visit.comment);
				args.put(seatedVisitColumns[col++], visit.order_in ? 1 : 0);

				mDB.update(DatabaseHelper.T_SEATEDVISITS, args, "uuid=?", new String[] { String.valueOf(visit.id) } );
			}
		} finally {
			close();
		}
	}
	
	private void clearVisit(UUID table_id) {
		mDB.delete(DatabaseHelper.T_SEATEDVISITS, "table_uuid=?", new String[] { String.valueOf(table_id) });
	}
	
	public void resetTables() {
		try {
			open();
			mDB.delete(DatabaseHelper.T_TEMP_SECTIONSxTABLES, null, null);
			mDB.delete(DatabaseHelper.T_SECTIONSxSERVERS, null, null);
			mDB.delete(DatabaseHelper.T_SEATEDVISITS, null, null);
			Collection<Table> tables = bulkRead(null).values();
			for(Table table : tables) {
				table.setState(Status.OPEN);
				table.group_id = table.id;
				table.seated_visit = null;
				update(table);
			}
		} finally {
			close();
		}
	}
}
