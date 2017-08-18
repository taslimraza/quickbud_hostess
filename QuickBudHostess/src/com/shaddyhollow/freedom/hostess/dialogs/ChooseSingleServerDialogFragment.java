package com.shaddyhollow.freedom.hostess.dialogs;


import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import com.shaddyhollow.quicktable.models.Server;

public class ChooseSingleServerDialogFragment extends DialogFragment {
	private CharSequence[] items;
	private int itemSelected;
	private List<Server> servers;
	private Listener listener;
	
	public static ChooseSingleServerDialogFragment newInstance(List<Server> servers, Listener listener) {
		ChooseSingleServerDialogFragment fragment = new ChooseSingleServerDialogFragment();
		fragment.initialize(servers, listener);
		return fragment;
	}

	public void initialize(List<Server> servers, Listener listener) {
		this.servers = servers;
		this.listener = listener;
		
		getItemsFromList();
	}

	private void getItemsFromList() {
		ArrayList<String> itemsList = new ArrayList<String>();
		
		if(servers!=null) {
			for (Server server : servers) {
				itemsList.add(server.name);
			}
		}		
		items = itemsList.toArray(new CharSequence[itemsList.size()]);
	}
	
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	itemSelected = 0;
        return new AlertDialog.Builder(getActivity())
        		.setTitle("Select server")
                .setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						itemSelected = which;
					}
				})
                .setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        	Server selectedServer = null;
                        	if(servers.size()==0) {
                        		Toast.makeText(getActivity(), "There are no servers available. Please create servers.", Toast.LENGTH_SHORT).show();
                        		return;
                        	}
                        	if(itemSelected<servers.size()) {
                        		selectedServer = servers.get(itemSelected);
                        	} 
                        	listener.onServerSelected(selectedServer);
                        }
                    }
                )
                .setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        	listener.onCancel();
                        }
                    }
                )
                .create();
    }
    
    public interface Listener {
    	public void onServerSelected(Server server);
    	public void onCancel();
    }
}
