package com.shaddyhollow.freedom.hostess.dialogs;


import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.shaddyhollow.quicktable.models.TableForSeating;

public class ChooseTableDialogFragment extends DialogFragment {
	private CharSequence[] items;
	private int itemSelected;
	private ArrayList<TableForSeating> tables;
	private Listener listener;

	public void initialize(ArrayList<TableForSeating> tables, Listener listener) {
		this.tables = tables;
		this.listener = listener;
		
		getItemsFromList();
	}

	private void getItemsFromList() {
		ArrayList<String> itemsList = new ArrayList<String>();
		for (TableForSeating table : tables) {
			itemsList.add(table.getDescription());
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
                        	listener.onTableSelected(tables.get(itemSelected));
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
    
    public interface Listener {
    	void onTableSelected(TableForSeating tableForSeating);
    }
}
