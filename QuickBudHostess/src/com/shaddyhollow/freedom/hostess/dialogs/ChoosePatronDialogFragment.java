package com.shaddyhollow.freedom.hostess.dialogs;


import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListAdapter;

import com.shaddyhollow.quickbud.datastore.QueuedVisitLoader;
import com.shaddyhollow.quicktable.models.QueuedVisit;
import com.shaddyhollow.quicktable.models.Table;

public class ChoosePatronDialogFragment extends DialogFragment {
	private CharSequence[] items;
	private int itemSelected;
	private ListAdapter adapter;
	private Table table;
	private Listener listener;
	private ArrayList<QueuedVisit> visits = new ArrayList<QueuedVisit>();

    public static ChoosePatronDialogFragment newInstance(ListAdapter adapter, Table table, Listener listener) {
    	ChoosePatronDialogFragment fragment = new ChoosePatronDialogFragment();
    	fragment.initialize(adapter, table, listener);
        return fragment;
    }
    
	public interface Listener {
		public void onPatronSelected(QueuedVisit visit, Table table);
	}
	
	private void initialize(ListAdapter adapter, Table table, Listener listener) {
		this.adapter = adapter;
		this.listener = listener;
		this.table = table;

		getItemsFromList();
	}

	private void getItemsFromList() {
		ArrayList<String> itemsList = new ArrayList<String>();
		itemsList.add("Add Walk-in");
		for (int i = 0; i < adapter.getCount(); i++) {
			Cursor cursor = (Cursor)adapter.getItem(i);
			QueuedVisit visit = QueuedVisitLoader.convertToObject(cursor);
			String text = visit.getName() + " : Party of " + visit.getParty_size();
			itemsList.add(text);
			visits.add(visit);
		}
		
		items = itemsList.toArray(new CharSequence[itemsList.size()]);
	}
	
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	itemSelected = 0;
        return new AlertDialog.Builder(getActivity())
                .setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						itemSelected = which;
					}
				})
                .setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        	if (itemSelected == 0 ) {
                        		listener.onPatronSelected(null, table);
                        	} else {
                            	listener.onPatronSelected(visits.get(itemSelected-1), table);
                        	}
                        }
                    }
                )
                .setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    }
                )
                .create();
    }
}
