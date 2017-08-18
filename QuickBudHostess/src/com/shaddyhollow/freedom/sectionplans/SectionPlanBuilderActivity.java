package com.shaddyhollow.freedom.sectionplans;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.shaddyhollow.quickbud.R;
import com.shaddyhollow.freedom.data.BackupBundle;
import com.shaddyhollow.freedom.dinendashhostess.requests.UpdateHostessConfigRequest;
import com.shaddyhollow.freedom.floorplans.FloorplanAdapter;
import com.shaddyhollow.freedom.sections.SectionsAdapter;
import com.shaddyhollow.freedom.tables.TablesAdapter;
import com.shaddyhollow.quickbud.Config;
import com.shaddyhollow.quickbud.datastore.SectionPlanFactory;
import com.shaddyhollow.quicktable.models.Floorplan;
import com.shaddyhollow.quicktable.models.HostessConfig;
import com.shaddyhollow.quicktable.models.Section;
import com.shaddyhollow.quicktable.models.SectionPlan;
import com.shaddyhollow.quicktable.models.Table;
import com.shaddyhollow.robospice.BaseListener;
import com.shaddyhollow.robospice.BaseRoboSpiceActivity;
import com.shaddyhollow.util.AlertDialogFragment;

public class SectionPlanBuilderActivity extends BaseRoboSpiceActivity implements ActionBar.TabListener, OnItemClickListener {
	SectionsPagerAdapter mSectionsPagerAdapter;
	TablesAdapter tablesAdapter = null;
	SectionsAdapter sectionsAdapter = null;
	FloorplanAdapter floorplanAdapter = null;
	SectionPlan area = null;

	ViewPager mViewPager;

//	View sectionsView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sectionarea_main);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		tablesAdapter = new TablesAdapter(this);
        sectionsAdapter = new SectionsAdapter(this);
        floorplanAdapter = new FloorplanAdapter(this);
        
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

		onLoad();

        EditorListFragment sectionListFragment = (EditorListFragment)getFragmentManager().findFragmentById(R.id.list);
        sectionListFragment.setSectionsAdapter(sectionsAdapter);
        sectionListFragment.setTablesAdapter(tablesAdapter);

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}
		});

		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
			
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_sectionlist, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean handled = true;
		switch(item.getItemId()) {
		case R.id.action_save:
			onSave();
			break;
		default:
			handled = super.onOptionsItemSelected(item);
			break;
		}
		return handled;
	}

	public class SectionsPagerAdapter extends FragmentPagerAdapter {
		FloorplanAdapter floorplanAdapter = null;
		
		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
			floorplanAdapter = new FloorplanAdapter(SectionPlanBuilderActivity.this);
		}

		@Override
		public Fragment getItem(int position) {
			TablesAdapter tablesAdapter = new TablesAdapter(SectionPlanBuilderActivity.this);
			Floorplan floorplan = floorplanAdapter.getItem(position);
			if(floorplan.tables!=null) {
				tablesAdapter.addAll(Arrays.asList(floorplan.tables));
			}
			EditorGridFragment fragment = new EditorGridFragment();
			fragment.setSectionsAdapter(sectionsAdapter);
			fragment.setFloorplanID(floorplan.id);

			fragment.setTablesAdapter(tablesAdapter);
			
			if(area.sections!=null) {
				for(Section section : area.sections) {
					if(section.tables!=null) {
						for(Table table : section.tables) {
							Table addedTable = tablesAdapter.getItemByID(table.id);
							if(addedTable!=null) {
								addedTable.section_id = section.id;
							}
						}
					}
				}
			}
	        EditorListFragment sectionListFragment = (EditorListFragment)getFragmentManager().findFragmentById(R.id.list);
	        sectionListFragment.setTablesAdapter(tablesAdapter);

			return fragment;
		}

		@Override
		public int getCount() {
			return floorplanAdapter.getCount(); // # of areas + queue fragment
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			Floorplan floorplan = floorplanAdapter.getItem(position);
			return floorplan.name.toUpperCase(l);
		}
		
		public Fragment findFragment(int position) {
            String name = "android:switcher:" + mViewPager.getId() + ":" + position;
            FragmentManager fm = getFragmentManager();
            Fragment fragment = fm.findFragmentByTag(name);
            if (fragment == null) {
                fragment = getItem(position);
            }
            return fragment;
        }
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		sectionsAdapter.setSelectionPosition(position);
		sectionsAdapter.notifyDataSetChanged();
	}
	
	public void editSection(View view) {
        EditorListFragment sectionListFragment = (EditorListFragment)getFragmentManager().findFragmentById(R.id.list);
        Section section = sectionsAdapter.getCurrentSelection();
        
    	EditorDetailFragment dlg = EditorDetailFragment.newInstance();
    	if(section!=null) {
        	Bundle bundle = new Bundle();
        	bundle.putString(EditorDetailFragment.KEY_ID, String.valueOf(section.id));
        	bundle.putString(EditorDetailFragment.KEY_NAME, section.name);
        	bundle.putInt(EditorDetailFragment.KEY_COLOR, section.colorID);
        	dlg.setArguments(bundle);
    	}
    	
    	dlg.setTargetFragment(sectionListFragment, EditorListFragment.EDIT);
		dlg.show(getFragmentManager(), "SectionEditorDialog");

	}
	
	public void deleteSection(View view) {
        EditorListFragment sectionListFragment = (EditorListFragment)getFragmentManager().findFragmentById(R.id.list);
        Section section = sectionsAdapter.getCurrentSelection();
        
        AlertDialogFragment dlg = new AlertDialogFragment();
		dlg.setTitle("Section Plan Editor");
		dlg.setCancelable(true);
		dlg.setMessage("Are you sure you want to delete section " + section.name + "?");
		dlg.setPositiveText("YES");
		dlg.setNegativeText("NO");
		dlg.setTargetFragment(sectionListFragment, EditorListFragment.DELETE);
		dlg.show(getFragmentManager(), "AlertDialog");
	}


	
	public void onSave() {
        area.sections = sectionsAdapter.getAll().toArray(new Section[0]);
        SectionPlanFactory.getInstance().update(area);
        performBackup();
    	finish();
	}

	private void performBackup() {
		BackupBundle bundle = new BackupBundle();
		bundle.inititalize();
		HostessConfig config = bundle.serialize();
		
		UpdateHostessConfigRequest request = new UpdateHostessConfigRequest(Config.getLocationID(), Config.getTenantID(), config);
		request.execute(contentManager, new UpdateHostessConfigRequestListener() );
    	Toast.makeText(SectionPlanBuilderActivity.this, "Starting backup to server", Toast.LENGTH_SHORT).show();
	}

	private class UpdateHostessConfigRequestListener extends BaseListener<Void> {
		@Override
		public void onFailure(SpiceException e) {
	        Toast.makeText( SectionPlanBuilderActivity.this, "Error backing up plans to server: " + e.getMessage(), Toast.LENGTH_LONG ).show();
		}
	
		@Override
		public void onRequestSuccess(Void arg0) {
	    	Toast.makeText(SectionPlanBuilderActivity.this, "Floorplans and sectionplans backed up", Toast.LENGTH_SHORT).show();
		}
	}

	@SuppressLint("UseSparseArrays")
	public void onLoad() {
		Intent myIntent = getIntent();
		SectionPlan existingArea = SectionPlanFactory.getInstance().read((UUID)myIntent.getExtras().get("KEY_ID"));
		if(existingArea!=null) {
			area = existingArea;
			HashMap<Integer, Table> tables = new HashMap<Integer, Table>();
			
			if(existingArea.sections!=null) {
				sectionsAdapter.addAll(Arrays.asList(area.sections));
				sectionsAdapter.notifyDataSetChanged();
			}
			
			tablesAdapter.addAll(tables.values());
			tablesAdapter.notifyDataSetChanged();

		} else {
	        area = new SectionPlan();
	        area.id = (UUID)myIntent.getExtras().get("KEY_ID");
		}
	}
}
