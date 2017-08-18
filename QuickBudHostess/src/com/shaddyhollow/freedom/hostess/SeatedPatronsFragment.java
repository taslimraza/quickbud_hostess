package com.shaddyhollow.freedom.hostess;

import java.util.List;
import java.util.UUID;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.shaddyhollow.quickbud.R;
import com.shaddyhollow.areagrid.AreaGrid;
import com.shaddyhollow.areagrid.AreaGridListener;
import com.shaddyhollow.freedom.sectionplans.SectionPlanAdapter;
import com.shaddyhollow.freedom.tables.TablesAdapter;
import com.shaddyhollow.quickbud.Config;
import com.shaddyhollow.quicktable.models.Section;
import com.shaddyhollow.quicktable.models.SectionPlan;
import com.shaddyhollow.quicktable.models.Table;
import com.shaddyhollow.util.Point;

public class SeatedPatronsFragment extends Fragment implements AreaGridListener, OnItemClickListener {
	public final static String KEY_AREAID = "AREA_KEY";
	public final static String KEY_FLOORPLANID = "FLOORPLAN_KEY";
	AreaGrid grid = null;
	SectionPlan area = null;
	boolean selectClosesTable = false;

	TablesAdapter tablesAdapter = null;
	DiningSectionsAdapter sectionsAdapter = null;

	public static SeatedPatronsFragment newInstance(Context context, TablesAdapter tablesAdapter, DiningSectionsAdapter sectionsAdapter) {
		SeatedPatronsFragment fragment = new SeatedPatronsFragment();
		fragment.tablesAdapter = tablesAdapter;
		fragment.sectionsAdapter = sectionsAdapter;
		return fragment;
	}

	public TablesAdapter getTablesAdapter() {
		return tablesAdapter;
	}
	
	public DiningSectionsAdapter getSectionsAdapter() {
		return sectionsAdapter;
	}
	
	public void setArea(SectionPlan area) {
		this.area = area;
	}
	
	public SectionPlan getArea() {
		return area;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		UUID areaID = null;

		super.onCreate(savedInstanceState);
		View view = inflater.inflate(R.layout.seated_patrons_grid, null);

		grid = (AreaGrid)view.findViewById(R.id.grid);
		grid.setSize(Config.ROWS, Config.COLS);
		grid.setGridVisible(false);
		grid.setShowTouch(false);
		
		grid.setAllowMultiSelection( true );
		
		// init adapters
		Bundle data = getArguments();
        if(data!=null) {
        	areaID = UUID.fromString(data.getString(KEY_AREAID));
        	UUID floorplanID = UUID.fromString(data.getString(KEY_FLOORPLANID));
        	
			SectionPlanAdapter areasAdapter = new SectionPlanAdapter(getActivity());
			area = areasAdapter.getItemByID(areaID);
			
        	copySectionIDsToTableList();

        	grid.setFloorplanID(floorplanID);
        	grid.setSectionsAdapter(sectionsAdapter);
        	grid.setTablesAdapter(tablesAdapter);
        }		
        
		grid.setListener(this);
		return view;
	}
	
	private void copySectionIDsToTableList() {
		if(sectionsAdapter==null) {
			return;
		}
    	for(Section section : sectionsAdapter.getAll()) {
    		if(section.tables!=null) {
    			for(Table table : section.tables) {
    				Table floorplanTable = tablesAdapter.getItemByID(table.getId());
    				if(floorplanTable!=null) {// if floorplanTable is null, then the table is in another floorplan
    					floorplanTable.section_id = section.getId();
    				}
    			}
    		}
    	}
	}
	
	@Override
	public void onSingleTileHit(Point tile) {
		sectionsAdapter.clearSelectionPosition();

		Table table = null;
		Section section = null;

		if(grid.isType(AreaGrid.TileType.TABLE, tile)) {
			table = grid.getTableInTile(tile);
		}
		if(table==null && selectClosesTable) {
			table = findFirstTable(tile);
		}
		if(table!=null) {
			section = sectionsAdapter.getItemByID(table.section_id);
		}

		if(((HostessActivity)getActivity()).mode.equals(Mode.PATRON_SEATING)) {
			if(grid.isType(AreaGrid.TileType.TABLE, tile)) {
				if(table.seated_visit!=null || section==null || !section.open) {
					return;
				}
				tablesAdapter.setSelectionByID(table.group_id);
				((HostessActivity)getActivity()).finishSeatingPatron(table.group_id);
			}			
		} else if(((HostessActivity)getActivity()).mode.equals(Mode.PATRON_MOVE)) {
			if(grid.isType(AreaGrid.TileType.TABLE, tile)) {
				if(table.seated_visit!=null || section==null || !section.open) {
					return;
				}
			
				Table originalTable = tablesAdapter.getCurrentSelection();
				tablesAdapter.setSelectionByID(table.group_id);
				((HostessActivity)getActivity()).movePatron(originalTable, table);
			}			
		} else if(((HostessActivity)getActivity()).mode.equals(Mode.COMBINE_TABLES)) {
			Table selectedTable = tablesAdapter.getCurrentSelection();
			if(grid.isType(AreaGrid.TileType.TABLE, tile)) {
				if(!table.group_id.equals(selectedTable.group_id)){
					table.group_id = selectedTable.getId();
				} else {
					table.group_id = table.id;
				}
			}			
		} else {
			if(grid.isType(AreaGrid.TileType.TABLE, tile)) {
				tablesAdapter.setSelectionByID(table.getId());
				((HostessActivity)getActivity()).updateDetails(Mode.TABLE_SINGLE);
			} else {
				tablesAdapter.clearSelectionPosition();
				if(table!=null) {
					sectionsAdapter.setSelectionByID(table.section_id);
				} else {
					sectionsAdapter.clearSelectionPosition();
				}
				((HostessActivity)getActivity()).updateDetails(Mode.SECTION_LIST);
			}
		}
		tablesAdapter.notifyDataSetChanged();
		sectionsAdapter.notifyDataSetChanged();
	}

	private Table findFirstTable(Point origin) {
		Point closestPoint = null;
		double minDist = 1000;
		
		// get first point of each table
		List<Table> tables = tablesAdapter.getAll();
		for(Table table : tables) {
			Point point = table.position.get(0);
			double distance = Math.sqrt((origin.row-point.row)*(origin.row-point.row) + (origin.column-point.column)*(origin.column-point.column));
			minDist = Math.min(minDist, distance);
			if(minDist==distance) {
				closestPoint = point;
			}
		}
		Table closestTable = grid.getTableInTile(closestPoint);
		return closestTable;
	}
	
	@Override
	public void onMultiTileHit(Point tile1, Point tile2) {
		onSingleTileHit(tile1);
	}

	@Override
	public boolean allowMultiSelectionBetween(Point tile1, Point tile2) {
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
		sectionsAdapter.setSelectionPosition(position);
		sectionsAdapter.notifyDataSetChanged();
	}
	

}
