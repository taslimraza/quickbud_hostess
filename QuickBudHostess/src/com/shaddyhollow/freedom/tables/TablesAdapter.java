package com.shaddyhollow.freedom.tables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.shaddyhollow.quickbud.R;
import com.shaddyhollow.freedom.data.BaseDataAdapter;
import com.shaddyhollow.quicktable.models.Table;
import com.shaddyhollow.util.Point;

public class TablesAdapter extends BaseDataAdapter<Table> {
	int maxID = 0;
	
	private int getNextID() {
		return maxID++;
	}

	public TablesAdapter(Context context) {
		super(context);
		
	}
	
	private String getNextTableName() {
		int maxNameVal = 0;
		List<Table> tables = getAll();
		for(Table table : tables) {
			int nameVal;
			try {
				nameVal = Integer.parseInt(table.name);
			} catch (NumberFormatException e) {
				nameVal = getNextID();
			}
			maxNameVal = Math.max(maxNameVal, nameVal);
		}
		return String.valueOf(maxNameVal+1);
	}
	
	public void add(UUID floorplanID, List<Point> selectedPoints) {
		Table table = new Table();
		table.id = UUID.randomUUID();
		table.name = getNextTableName();
		table.seats = calculateSeats(selectedPoints);
		table.table_type = "Table";
		table.position = selectedPoints;
		table.floorplan_id = floorplanID;
		table.group_id = table.id;
		
		add(table);
		setSelectionPosition(getCount()-1);
	}
	
	private int calculateSeats(List<Point> points) {
		int seat = 0;
		for(Point point : points) {
			int surroundingPoints = 0;
			surroundingPoints += points.contains(new Point(point.row-1, point.column)) ? 1 : 0;
			surroundingPoints += points.contains(new Point(point.row+1, point.column)) ? 1 : 0;
			surroundingPoints += points.contains(new Point(point.row, point.column-1)) ? 1 : 0;
			surroundingPoints += points.contains(new Point(point.row, point.column+1)) ? 1 : 0;
			
			if(surroundingPoints<4) {
				seat++;
			}
		}
		if(seat==0) {
			seat = 1;
		}
		return seat;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(getCurrentSelectionPosition()==position) {
			return getDetailView(position, convertView, parent);
		} else {
			return getSummaryView(position, convertView, parent);
		}
	}

	private View getSummaryView(int position, View convertView, ViewGroup parent) {
		SummaryViewHolder holder = null;
		Table table = getItem(position);
		View view = convertView;
		
		if(view==null || !(view.getTag() instanceof SummaryViewHolder)) {
			holder = new SummaryViewHolder();
			view = layoutInflater.inflate(R.layout.floorplan_tablesummary, null);
			holder.tableName = (TextView)view.findViewById(R.id.tablename);
			holder.seatCount = (TextView)view.findViewById(R.id.seatcount);
			holder.tableType = (TextView)view.findViewById(R.id.tabletype);
			view.setTag(holder);
		}
		holder = (SummaryViewHolder)view.getTag();

		holder.tableName.setText("# " + table.name);
		holder.tableType.setText(table.table_type);
		holder.seatCount.setText("(" + table.seats + ")");

		return view;
	}

	private View getDetailView(int position, View convertView, ViewGroup parent) {
		DetailViewHolder holder = null;
		Table table = getItem(position);
		View view = convertView;
		
		if(view==null || !(view.getTag() instanceof DetailViewHolder)) {
			holder = new DetailViewHolder();
			view = layoutInflater.inflate(R.layout.floorplan_tabledetail, null);
			holder.tableName = (TextView)view.findViewById(R.id.tablename);
			holder.seatCount = (TextView)view.findViewById(R.id.seatcount);
			holder.tableType = (TextView)view.findViewById(R.id.tabletype);
			holder.deleteButton = (ImageButton)view.findViewById(R.id.btn_delete);
			holder.editButton = (ImageButton)view.findViewById(R.id.btn_edit);
			view.setTag(holder);
		}
		holder = (DetailViewHolder)view.getTag();

		holder.tableName.setText("# " + table.name);
		holder.seatCount.setText(table.seats + (table.seats==1 ? " seat" : " seats"));
		if(table.table_type==null || table.table_type.length()==0) {
			holder.tableType.setVisibility(View.GONE);
		} else {
			holder.tableType.setVisibility(View.VISIBLE);
			holder.tableType.setText(table.table_type);
		}
		
		return view;
	}

	public Collection<Table> getTablesByFloorplan(UUID floorplan_id) {
		List<Table> tablesInFloorplan = new ArrayList<Table>();
		for(Table table : getAll()) {
			if(table.floorplan_id.equals(floorplan_id)) {
				tablesInFloorplan.add(table);
			}
		}
		return tablesInFloorplan;
	}
	
	public Collection<UUID> getGroupedTables(Collection<Table> tables) {
		Set<UUID> groups = new HashSet<UUID>();
		Map<UUID, Integer> tableIDs = new HashMap<UUID, Integer>();
		
		for(Table table : tables) {
			if(!tableIDs.containsKey(table.group_id)) {
				tableIDs.put(table.group_id, 1);
			} else {
				groups.add(table.group_id);
			}
		}
		
		return groups;
	}
	
	static class SummaryViewHolder {
		TextView tableName;
		TextView seatCount;
		TextView tableType;
	}
	static class DetailViewHolder {
		TextView tableName;
		TextView seatCount;
		TextView tableType;
		ImageButton deleteButton;
		ImageButton editButton;
	}
}
