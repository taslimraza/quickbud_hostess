package com.shaddyhollow.quickbud.datastore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.shaddyhollow.quickbud.Config;
import com.shaddyhollow.quicktable.models.Floorplan;
import com.shaddyhollow.quicktable.models.SectionPlan;
import com.shaddyhollow.quicktable.models.Table;

public class FloorplanFactory extends BaseFactory<Floorplan> {
	public String[] columns = new String[] { "uuid", "location_uuid", "name" };
	private static FloorplanFactory instance = null;
	
	private FloorplanFactory(Context context) {
		this.mContext = context;
	}
	
	public static FloorplanFactory getInstance() {
		if(instance==null) {
			throw new RuntimeException("FloorplanFactory must be initialized first with getInstance(context)");
		}
		return instance;
	}
	
	public static FloorplanFactory getInstance(Context context) {
		if(instance==null) {
			instance = new FloorplanFactory(context);
		}
		return instance;
	}

	@Override
	protected String getTableName() {
		return DatabaseHelper.T_FLOORPLANS;
	}

	@Override
	public Floorplan create(Floorplan element) {
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
			
			mDB.insert(getTableName(), null, args);
			if(element.tables!=null) {
				for(Table table : element.tables) {
					TableFactory.getInstance().create(element.id, table);
				}
			}
		} finally {
			close();
		}
		
		return element;
	}

	@Override
	public Floorplan read(UUID id) {
		Floorplan element = null;
		Cursor cursor = null;

		try {
			open();
			
			// read floorplan metadata
			cursor = mDB.query(true, getTableName(), columns, "uuid=?", new String[] { String.valueOf(id)}, null, null, null, null, null);
			if(cursor.moveToFirst()) {
				int col = 0;
				element = new Floorplan();
				element.setId(UUID.fromString(cursor.getString(col++)));
				col++; // ignore location_id
				element.setName(cursor.getString(col++));

				// read tables and positions
				Collection<Table> tables = TableFactory.getInstance().bulkRead("floorplan_uuid='" + id + "'").values();
				element.tables = tables.toArray(new Table[0]);
			}
			
			
		} finally {
			if(cursor!=null) {
				cursor.close();
			}
			close();
		}
		return element;
	}

	public List<Floorplan> bulkRead(String where) {
		List<Floorplan> floorplans = new ArrayList<Floorplan>();
		Cursor cursor = null;

		try {
			open();
			
			cursor = mDB.query(true, getTableName(), columns, where, null, null, null, null, null, null);
			if(cursor.moveToFirst()) {
				do {
					Floorplan element = null;
	
					int col = 0;
					element = new Floorplan();
					element.setId(UUID.fromString(cursor.getString(col++)));
					col++; // ignore location_id
					element.setName(cursor.getString(col++));
	
					// read tables and positions
					Collection<Table> tables = TableFactory.getInstance().bulkRead("floorplan_uuid='" + element.getId() + "'").values();
					element.tables = tables.toArray(new Table[0]);
	
					floorplans.add(element);
				} while(cursor.moveToNext());
			}
			
			
		} finally {
			if(cursor!=null) {
				cursor.close();
			}
			close();
		}
		return floorplans;
	}

	@Override
	public Floorplan update(Floorplan element) {
		try {
			open();
			ContentValues args = new ContentValues();
			args.put(columns[2], element.getName());
			
			mDB.update(getTableName(), args, "uuid=?", new String[] { String.valueOf(element.getId()) } );
			
			for(Table table : element.tables) {
				if(TableFactory.getInstance().exists(table.id)){
					TableFactory.getInstance().update(table);
				} else {
					TableFactory.getInstance().create(element.getId(), table);
				}
			}
		} finally {
			close();
		}
		return element;
	}

	@Override
	public void delete(UUID floorplan_id) {
		String sql = "uuid=?";
		try {
			open();
			
			List<SectionPlan> sectionPlans = SectionPlanFactory.getInstance().bulkRead(null);
			for(SectionPlan sectionPlan : sectionPlans) {
				SectionPlanFactory.getInstance().delete(sectionPlan.id);
			}
			TableFactory.getInstance().bulkDelete("floorplan_uuid='" + floorplan_id + "'");
			String args[] = new String[] { String.valueOf(floorplan_id) };
			mDB.delete(getTableName(), sql, args);
		} finally {
			close();
		}
	}
	
	public void bulkDelete(String where) {
		try {
			open();
			
			List<Floorplan> floorplans = bulkRead(where);
			for(Floorplan floorplan : floorplans) {
				delete(floorplan.getId());
			}
		} finally {
			close();
		}
	}
	
	
}
