package com.shaddyhollow.freedom.floorplans;

import java.util.List;
import java.util.UUID;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.shaddyhollow.quickbud.R;
import com.shaddyhollow.areagrid.AreaGrid;
import com.shaddyhollow.areagrid.AreaGrid.TileType;
import com.shaddyhollow.areagrid.AreaGridListener;
import com.shaddyhollow.freedom.tables.TablesAdapter;
import com.shaddyhollow.quickbud.Config;
import com.shaddyhollow.quicktable.models.Table;
import com.shaddyhollow.util.Point;

public class EditorGridFragment extends Fragment implements AreaGridListener {
	public AreaGrid grid;
	public UUID currentSelection;
	TablesAdapter adapter;
	public UUID floorplanID;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.area_grid, container);

		grid = new AreaGrid(getActivity(), Config.COLS, Config.ROWS);
		grid.setAllowMultiSelection( true );
		grid.setShowTableGroups(false);
		grid.setShowLargeTextForSelection(false);
		grid.setShowTableSections(false);
		grid.setShowTableStatus(false);
		grid.setListener(this);
		grid.setDefaultTableColor(Color.GRAY);
		((FrameLayout)view.findViewById(R.id.place_tables_grid_frame)).addView(grid);
		return view;
	}

    public void setAdapter(TablesAdapter adapter) {
    	this.adapter = adapter;
    	grid.setTablesAdapter(adapter);
    }
    
    public TablesAdapter getAdapter() {
    	return adapter;
    }

    public void setFloorplanID(UUID floorplanID) {
    	this.floorplanID = floorplanID;
    	grid.setFloorplanID(floorplanID);
    }
    
	@Override
	public boolean allowMultiSelectionBetween(Point tile1, Point tile2) {
		return true;
	}
	
	private void placeTable(Point tile1, Point tile2) {
		List<Point> selectedPoints = tile1.allPointsBetween(tile2);
		if(selectedPoints.size()==0) {
			return;
		}
		for (Point p:  selectedPoints) {
			if ( grid.isType(TileType.TABLE, p)) {
				return;
			}
		}
		adapter.add(floorplanID, selectedPoints);
		adapter.notifyDataSetChanged();
	}

	private void selectTable(Point tile1) {
		if(grid.isType(TileType.TABLE, tile1)) {
			Table tableInTile = grid.getTableInTile(tile1);
			currentSelection = tableInTile.id;
			
			adapter.setSelectionByID(tableInTile.id);
			adapter.notifyDataSetChanged();

			EditorListFragment listFragment = (EditorListFragment)getActivity().getFragmentManager().findFragmentById(R.id.list);
			listFragment.ensureSelectionVisible();
		}
	}
	
	@Override
	public void onMultiTileHit(Point tile1, Point tile2) {
		if(grid.isType(TileType.TABLE, tile1)) {
			selectTable(tile1);
		} else {
			placeTable(tile1, tile2);
		}
	}

	@Override
	public void onSingleTileHit(Point tile1) {
		if(grid.isType(TileType.TABLE, tile1)) {
			selectTable(tile1);
		} else {
			placeTable(tile1, tile1);
		}
	}


}
