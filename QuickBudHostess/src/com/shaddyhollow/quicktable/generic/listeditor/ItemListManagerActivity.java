package com.shaddyhollow.quicktable.generic.listeditor;

import java.util.UUID;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.shaddyhollow.quickbud.R;
import com.shaddyhollow.freedom.data.BaseDataAdapter;
import com.shaddyhollow.quicktable.models.Identifiable;
import com.shaddyhollow.robospice.BaseRoboSpiceActivity;

public abstract class ItemListManagerActivity<T extends Identifiable> extends BaseRoboSpiceActivity implements ItemListFragment.Callbacks {
	protected BaseDataAdapter<T> adapter;

	public abstract BaseDataAdapter<T> initAdapter();	
	public abstract String getItemType();
	public abstract void performItemDelete(Identifiable item);
	public abstract void performItemUpdate(Identifiable item);
	public abstract void performItemCreate(String itemName);

	public int getManagerView() {
		return R.layout.item_manager;
	}
	
	public int getDetailLayout() {
		return R.layout.item_detail;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getManagerView());
		if(ActivityManager.isUserAMonkey()) {
			finish();
		}

		if (findViewById(R.id.item_detail_container) != null) {
			ItemListFragment listFragment = ((ItemListFragment) getFragmentManager().findFragmentById(R.id.item_list));
			
			adapter = initAdapter();
			listFragment.setActivateOnItemClick(true);
			listFragment.setAdapter(adapter);
		}
		
	}

	@Override
	public void onItemSelected(UUID id) {
			ItemDetailFragment fragment = ItemDetailFragment.newInstance(getDetailLayout(), getDetailEditor()!=null);
			fragment.setAdapter(adapter);
			getFragmentManager().beginTransaction().replace(R.id.item_detail_container, fragment).commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_itemmanager, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.action_add:
			showSimpleItemEditor(null);
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	public Intent getDetailEditor() {
		return null;
	}
	
	public void showDetailEditor(Identifiable item) {
		Intent detailEditor = getDetailEditor();
		if(detailEditor!=null) {
			startActivity(detailEditor);
		}
	}
	
	public void showSimpleItemEditor(Identifiable item) {
		SimpleIdentifiableEditorFragment dlg = SimpleIdentifiableEditorFragment.newInstance(getItemType());
		if(item!=null) {
			Bundle bundle = new Bundle();
			bundle.putString(SimpleIdentifiableEditorFragment.KEY_ID, String.valueOf(item.getId()));
			bundle.putString(SimpleIdentifiableEditorFragment.KEY_NAME, item.getName());
			dlg.setArguments(bundle);
		}

		dlg.show(getFragmentManager(), "EditorDialog");
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		// creating item
		if(data!=null) {
			String itemName = data.getStringExtra(SimpleIdentifiableEditorFragment.KEY_NAME);
			if(itemName==null || itemName.length()==0) {
				Toast.makeText(this, getItemType() + " name is invalid", Toast.LENGTH_SHORT).show();
				return;
			}
			UUID itemID = (UUID)data.getExtras().get(SimpleIdentifiableEditorFragment.KEY_ID);
			if(itemID==null) {
				performItemCreate(data.getStringExtra(SimpleIdentifiableEditorFragment.KEY_NAME));
				adapter.notifyDataSetChanged();
			} else {
				Identifiable item = adapter.getItemByID(itemID);
				item.setName(data.getStringExtra(SimpleIdentifiableEditorFragment.KEY_NAME));
				performItemUpdate(item);
				adapter.notifyDataSetChanged();
			}
			
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void onEditDetails(View view) {
		showDetailEditor(adapter.getCurrentSelection());
	}
	
	public void onRename(View view) {
		showSimpleItemEditor(adapter.getCurrentSelection());
	}

	public void onDelete(View view) {
        final Identifiable item = adapter.getCurrentSelection();
        
	    new AlertDialog.Builder(this)
        .setIcon(android.R.drawable.ic_dialog_alert)
        .setTitle("Delete " + getItemType())
        .setMessage("Are you really sure you want to remove " + item.getName() + " from the system?")
        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	        @Override
	        public void onClick(DialogInterface dialog, int which) {
	        	performItemDelete(item);
				adapter.notifyDataSetChanged();
	        }

	    })
	    .setNegativeButton("No", null)
	    .show();

	}

}
