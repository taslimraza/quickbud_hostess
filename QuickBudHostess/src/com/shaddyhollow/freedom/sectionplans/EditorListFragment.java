package com.shaddyhollow.freedom.sectionplans;

import java.util.UUID;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.shaddyhollow.quickbud.R;
import com.shaddyhollow.freedom.sections.SectionsAdapter;
import com.shaddyhollow.freedom.tables.TablesAdapter;
import com.shaddyhollow.quickbud.Config;
import com.shaddyhollow.quickbud.datastore.SectionPlanFactory;
import com.shaddyhollow.quicktable.models.Section;
import com.shaddyhollow.quicktable.models.Table;
import com.shaddyhollow.util.AlertDialogFragment;

public class EditorListFragment extends Fragment implements OnItemClickListener {
	public final static int EDIT = 100;
	public final static int DELETE = 101;
	private final static int RESET = 102;
	
	TablesAdapter tablesAdapter = null;
	SectionsAdapter sectionsAdapter = null;
	String dataDir = Config.getStorageDir("areas");
	ListView list = null;
	
    public static EditorListFragment newInstance() {
        return new EditorListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        
    }

    public void setSectionsAdapter(SectionsAdapter adapter) {
    	this.sectionsAdapter = adapter;
		list.setAdapter(adapter);
    }
    
    public void setTablesAdapter(TablesAdapter adapter) {
    	this.tablesAdapter = adapter;
    }
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.sectionslist, container);
		
        list = (ListView)view.findViewById(R.id.sectionlist);
        list.setOnItemClickListener(this);
        list.setAdapter(sectionsAdapter);
		registerForContextMenu(list);

		return view;
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
		boolean handled = false;

		switch (item.getItemId()) {
        case R.id.action_add:
        	addNewSection();
        	handled = true;
            break;
        case R.id.action_reset: {
				AlertDialogFragment dlg = new AlertDialogFragment();
				dlg.setTitle("Layout Editor");
				dlg.setCancelable(true);
				dlg.setMessage("Are you sure you want to reset the layout?");
		    	dlg.setTargetFragment(this, RESET);
				dlg.show(getFragmentManager(), "AlertDialog");
	        }
        	handled = true;
        	break;
        default:
			handled = super.onOptionsItemSelected(item);
        }

        return handled;
    }
    
	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
		int currentSelection = sectionsAdapter.getCurrentSelectionPosition();
		if(currentSelection == position) {
			sectionsAdapter.clearSelectionPosition();
		} else {
			sectionsAdapter.setSelectionPosition(position);
		}
		sectionsAdapter.notifyDataSetChanged();
	}

	private void addNewSection() {
    	Section section = new Section();
		section.id = UUID.randomUUID();
    	section.name = String.valueOf(sectionsAdapter.getCount());
    	section.open = true;
    	section.colorID = sectionsAdapter.getUnusedColor();

    	sectionsAdapter.add(section);
    	sectionsAdapter.setSelectionByID(section.id);
    	sectionsAdapter.notifyDataSetChanged();
	}

	private void deleteSection(UUID sectionID) {
		Section section = sectionsAdapter.getItemByID(sectionID);
		if(section.tables!=null) {
			for(Table table : section.tables) {
				Table tableInGrid = tablesAdapter.getItemByID(table.getId());
				if(tableInGrid!=null) {
					tableInGrid.section_id = null;
				}
			}
		}
		SectionPlanFactory.getInstance().delete(sectionsAdapter.getCurrentSelection().getId());
		sectionsAdapter.remove(sectionsAdapter.getCurrentSelectionPosition());
		sectionsAdapter.clearSelectionPosition();
		sectionsAdapter.notifyDataSetChanged();
		tablesAdapter.notifyDataSetChanged();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode) {
		case DELETE:
			deleteSection(sectionsAdapter.getCurrentSelection().getId());
			break;
		case EDIT:
			if(data!=null) {
				Section section;
				UUID sectionID = (UUID)data.getExtras().get(EditorDetailFragment.KEY_ID);
				if(sectionID==null) {
					section = new Section();
					section.id = UUID.randomUUID();
					section.name = String.valueOf(section.id);
					sectionsAdapter.add(section);
				} else {
					section = sectionsAdapter.getItemByID(sectionID);
				}
				section.name = data.getStringExtra(EditorDetailFragment.KEY_NAME);
				section.colorID = data.getIntExtra(EditorDetailFragment.KEY_COLOR, 0);
				sectionsAdapter.notifyDataSetChanged();

			}
			break;
		case RESET:
			sectionsAdapter.clearSelectionPosition();
			sectionsAdapter.clear();
			sectionsAdapter.notifyDataSetChanged();
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	
}
