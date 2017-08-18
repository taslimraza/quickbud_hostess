package com.shaddyhollow.freedom.sectionplans;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.shaddyhollow.quickbud.R;
import com.shaddyhollow.areagrid.AreaGrid;
import com.shaddyhollow.areagrid.AreaGrid.TileType;
import com.shaddyhollow.areagrid.AreaGridListener;
import com.shaddyhollow.freedom.sections.SectionsAdapter;
import com.shaddyhollow.freedom.tables.TablesAdapter;
import com.shaddyhollow.quickbud.Config;
import com.shaddyhollow.quicktable.models.Section;
import com.shaddyhollow.quicktable.models.Table;
import com.shaddyhollow.util.Point;

public class EditorGridFragment extends Fragment implements AreaGridListener {
	public AreaGrid grid;
	UUID floorplanID;
	TablesAdapter tablesAdapter;
	SectionsAdapter sectionsAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.area_grid, null);

		grid = new AreaGrid(getActivity(), Config.COLS, Config.ROWS);
		grid.setAllowMultiSelection( true );
		grid.setShowTableStatus(false);
		grid.setShowTableGroups(false);
		grid.setShowLargeTextForSelection(false);
		grid.setShowTableSections(true);
		grid.setShowTouch(false);
		grid.setListener(this);
    	grid.setSectionsAdapter(sectionsAdapter);
    	grid.setTablesAdapter(tablesAdapter);
    	grid.setFloorplanID(floorplanID);
		((FrameLayout)view.findViewById(R.id.place_tables_grid_frame)).addView(grid);
		return view;
	}

    public void setTablesAdapter(TablesAdapter adapter) {
    	this.tablesAdapter = adapter;
    	if(grid!=null) {
    		grid.setTablesAdapter(adapter);
    	}
    }
    
    public TablesAdapter getTablesAdapter() {
    	return tablesAdapter;
    }

    public void setSectionsAdapter(SectionsAdapter adapter) {
    	this.sectionsAdapter = adapter;
    	if(grid!=null) {
    		grid.setSectionsAdapter(adapter);
    	}
    }
    
    public SectionsAdapter getSectionsAdapter() {
    	return sectionsAdapter;
    }

    public void setFloorplanID(UUID floorplanID) {
    	this.floorplanID = floorplanID;
    	if(grid!=null) {
    		grid.setFloorplanID(floorplanID);
    	}
    }

    @Override
	public boolean allowMultiSelectionBetween(Point tile1, Point tile2) {
		return true;
	}
	
	private void placeSection(Point tile1, Point tile2) {
		List<Point> selectedPoints = tile1.allPointsBetween(tile2);
		List<Table> tablesInSelection = new ArrayList<Table>();
		Section currentSection = sectionsAdapter.getCurrentSelection();
		
		if(selectedPoints.size()==0) {
			return;
		}
		if(sectionsAdapter.getCurrentSelection()==null) {
			return;
		}
		
		for (Point p:  selectedPoints) {
			if(grid.isType(TileType.TABLE, p)) {
				Table table = grid.getTableInTile(p);
				if(!tablesInSelection.contains(table)) {
					tablesInSelection.add(table);
				}
			}
		}
		
		for(Table table : tablesInSelection) {
			Section oldSection = sectionsAdapter.getItemByID(table.section_id);
			if(oldSection!=null) {
				oldSection.removeTable(table);
			}
			currentSection.addTable(table);
		}
		
		sectionsAdapter.notifyDataSetChanged();
		tablesAdapter.notifyDataSetChanged();
	}

	@Override
	public void onMultiTileHit(Point tile1, Point tile2) {
//		if(grid.isType(TileType.SECTION, tile1)) {
//			onSingleTileHit(tile1);
//		} else {
			placeSection(tile1, tile2);
//		}
	}

	@Override
	public void onSingleTileHit(Point tile1) {
		
		if(grid.isType(TileType.TABLE, tile1)) {
			Table table = grid.getTableInTile(tile1);
			Section currentSection = sectionsAdapter.getCurrentSelection();
			Section initialSection = sectionsAdapter.getItemByID(table.section_id);
			
			if(initialSection!=null) {
				initialSection.removeTable(table);
			} else if(currentSection!=null) {
				currentSection.addTable(table);
			}
			
			sectionsAdapter.notifyDataSetChanged();
			tablesAdapter.notifyDataSetChanged();
		}
	}

}
