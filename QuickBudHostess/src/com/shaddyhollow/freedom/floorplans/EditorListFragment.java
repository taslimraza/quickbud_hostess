package com.shaddyhollow.freedom.floorplans;

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
import com.shaddyhollow.freedom.tables.TablesAdapter;
import com.shaddyhollow.quickbud.datastore.TableFactory;
import com.shaddyhollow.quicktable.models.Table;
import com.shaddyhollow.util.AlertDialogFragment;

public class EditorListFragment extends Fragment implements OnItemClickListener {
	public final static int EDIT_TABLE = 100;
	public final static int DELETE_TABLE = 101;
	private final static int RESET_LAYOUT = 102;
	TablesAdapter adapter;
	ListView list = null;

	public static EditorListFragment newInstance() {
		return new EditorListFragment();
	}

	public void setTablesAdapter(TablesAdapter adapter) {
		this.adapter = adapter;
		list.setAdapter(adapter);
	}

    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.tableslist, container);

		list = (ListView)view.findViewById(android.R.id.list);
		list.setOnItemClickListener(this);
		list.setAdapter(adapter);

		return view;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean handled = false;

		switch (item.getItemId()) {
		case R.id.action_reset: {
			AlertDialogFragment dlg = new AlertDialogFragment();
			dlg.setTitle("Layout Editor");
			dlg.setCancelable(true);
			dlg.setMessage("Are you sure you want to reset the layout?");
			dlg.setTargetFragment(this, RESET_LAYOUT);
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
		int currentSelection = adapter.getCurrentSelectionPosition();
		if(currentSelection == position) {
			adapter.clearSelectionPosition();
		} else {
			adapter.setSelectionPosition(position);
		}
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode) {
		case DELETE_TABLE:
			TableFactory.getInstance().delete(adapter.getCurrentSelection().getId());
			adapter.remove(adapter.getCurrentSelectionPosition());
			adapter.clearSelectionPosition();
			adapter.notifyDataSetChanged();
			break;
		case EDIT_TABLE:
			if(data!=null) {
				Table table;
				UUID tableID = (UUID)data.getExtras().get(EditorDetailFragment.KEY_ID);
				if(tableID==null) {
					table = new Table();
					table.id = UUID.randomUUID();
					table.name = String.valueOf(table.id);
					adapter.add(table);
				} else {
					table = adapter.getItemByID(tableID);
				}
				table.name = data.getStringExtra(EditorDetailFragment.KEY_NAME);
				table.seats = data.getIntExtra(EditorDetailFragment.KEY_SEATS, 0);
				table.table_type = data.getStringExtra(EditorDetailFragment.KEY_TYPE);
				adapter.notifyDataSetChanged();

			}
			break;
		case RESET_LAYOUT:
			adapter.clearSelectionPosition();
			adapter.clear();
			adapter.notifyDataSetChanged();
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void ensureSelectionVisible() {
		if(adapter.getCurrentSelectionPosition()!=-1) {
			list.smoothScrollToPosition(adapter.getCurrentSelectionPosition());
		}
	}
}
