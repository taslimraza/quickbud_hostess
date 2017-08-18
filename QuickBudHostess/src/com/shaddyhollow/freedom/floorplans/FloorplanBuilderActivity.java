package com.shaddyhollow.freedom.floorplans;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.shaddyhollow.quickbud.R;
import com.shaddyhollow.freedom.data.BackupBundle;
import com.shaddyhollow.freedom.dinendashhostess.requests.UpdateHostessConfigRequest;
import com.shaddyhollow.freedom.tables.TablesAdapter;
import com.shaddyhollow.quickbud.Config;
import com.shaddyhollow.quickbud.datastore.FloorplanFactory;
import com.shaddyhollow.quicktable.models.Floorplan;
import com.shaddyhollow.quicktable.models.HostessConfig;
import com.shaddyhollow.quicktable.models.Table;
import com.shaddyhollow.quicktable.models.TableComparator;
import com.shaddyhollow.robospice.BaseListener;
import com.shaddyhollow.robospice.BaseRoboSpiceActivity;
import com.shaddyhollow.util.AlertDialogFragment;

public class FloorplanBuilderActivity extends BaseRoboSpiceActivity {
	private static final String _TAG = "LayoutAreaActivity";

	public int currentSection;
	Floorplan floorplan = null;
	TablesAdapter adapter = null;
	UUID locationID;
	String dataDir = Config.getStorageDir("floorplans");

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.v(_TAG, "LayoutAreaActivity: onCreate()");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.floorplan_tablemap);
//		locationID = Config.location.getId();

        adapter = new TablesAdapter(this);
        
        EditorListFragment tableListFragment = (EditorListFragment)getFragmentManager().findFragmentById(R.id.list);
		tableListFragment.setTablesAdapter(adapter);
		
		Intent myIntent = getIntent();
		Floorplan existingPlan = FloorplanFactory.getInstance().read((UUID)myIntent.getExtras().get("KEY_ID"));
		if(existingPlan!=null) {
			floorplan = existingPlan;
			if(existingPlan.tables!=null) {
				List<Table> allTables = Arrays.asList(floorplan.tables);
				Collections.sort(allTables, new TableComparator());
				adapter.addAll(allTables);
				adapter.notifyDataSetChanged();
			}
		} else {
	        floorplan = new Floorplan();
	        floorplan.id = (UUID)myIntent.getExtras().get("KEY_ID");
		}
		
        EditorGridFragment gridFragment = (EditorGridFragment)getFragmentManager().findFragmentById(R.id.grid);
        gridFragment.setAdapter(adapter);
        gridFragment.setFloorplanID(floorplan.getId());

	}
	
	public void deleteTable(View view) {
        EditorListFragment tableListFragment = (EditorListFragment)getFragmentManager().findFragmentById(R.id.list);

        AlertDialogFragment dlg = new AlertDialogFragment();
		dlg.setTitle("Layout Editor");
		dlg.setCancelable(true);
		dlg.setMessage("Are you sure you want to delete the table?");
		dlg.setPositiveText("YES");
		dlg.setNegativeText("NO");
		dlg.setTargetFragment(tableListFragment, EditorListFragment.DELETE_TABLE);
		dlg.show(getFragmentManager(), "AlertDialog");
	}

	public void editTable(View view) {
		Table table = adapter.getCurrentSelection();
		EditorDetailFragment dlg = EditorDetailFragment.newInstance();
		if(table!=null) {
			Bundle bundle = new Bundle();
			bundle.putString(EditorDetailFragment.KEY_ID, String.valueOf(table.id));
			bundle.putString(EditorDetailFragment.KEY_NAME, table.name);
			bundle.putInt(EditorDetailFragment.KEY_SEATS, table.seats);
			bundle.putString(EditorDetailFragment.KEY_TYPE, table.table_type);
			dlg.setArguments(bundle);
		}
		
        EditorListFragment tableListFragment = (EditorListFragment)getFragmentManager().findFragmentById(R.id.list);
		dlg.setTargetFragment(tableListFragment, EditorListFragment.EDIT_TABLE);
		dlg.show(getFragmentManager(), "SectionEditorDialog");
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_tablelist, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean handled = false;
		
		switch (item.getItemId()) {
		case R.id.action_save:
			onSave();
			handled = true;
			break;
		default:
			handled = super.onOptionsItemSelected(item);
		}
		return handled;
	}

	public void onSave() {
        EditorGridFragment gridFragment = (EditorGridFragment)getFragmentManager().findFragmentById(R.id.grid);
        TablesAdapter tablesAdapter = gridFragment.getAdapter();
        floorplan.tables = tablesAdapter.getAll().toArray(new Table[0]);
        
        FloorplanFactory.getInstance().createOrUpdate(floorplan);
        
        performBackup();
    	finish();
        
	}
	
	private void performBackup() {
		BackupBundle bundle = new BackupBundle();
		bundle.inititalize();
		HostessConfig config = bundle.serialize();
		
		UpdateHostessConfigRequest request = new UpdateHostessConfigRequest(Config.getLocationID(), Config.getTenantID() , config);
		request.execute(contentManager, new UpdateHostessConfigRequestListener() );
    	Toast.makeText(FloorplanBuilderActivity.this, "Starting backup to server", Toast.LENGTH_SHORT).show();
	}

	private class UpdateHostessConfigRequestListener extends BaseListener<Void> {
		@Override
		public void onFailure(SpiceException e) {
	        Toast.makeText( FloorplanBuilderActivity.this, "Error backing up plans to server: " + e.getMessage(), Toast.LENGTH_LONG ).show();
		}
	
		@Override
		public void onRequestSuccess(Void arg0) {
	    	Toast.makeText(FloorplanBuilderActivity.this, "Floorplans and sectionplans backed up", Toast.LENGTH_SHORT).show();
		}
	}
}
