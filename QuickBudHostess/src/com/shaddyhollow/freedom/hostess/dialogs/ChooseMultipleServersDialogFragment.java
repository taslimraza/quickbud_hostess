package com.shaddyhollow.freedom.hostess.dialogs;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.shaddyhollow.quicktable.models.Server;

public class ChooseMultipleServersDialogFragment extends DialogFragment {
	private CharSequence[] items;
	private boolean[] selected;
	private List<Server> servers;
	private Listener listener;
	
	public static ChooseMultipleServersDialogFragment newInstance(List<Server> servers, List<Server> selectedServers, Listener listener) {
		ChooseMultipleServersDialogFragment fragment = new ChooseMultipleServersDialogFragment();
		fragment.initialize(servers, selectedServers, listener);
		return fragment;
	}

	public void initialize(List<Server> servers, List<Server> selectedServers, Listener listener) {
		this.servers = servers;
		this.listener = listener;
		List<UUID> selectedServerIDs = new ArrayList<UUID>();
		if(selectedServers!=null) {
			for(Server selectedServer : selectedServers) {
				if(selectedServer == null) {
					continue;
				}
				selectedServerIDs.add(selectedServer.getId());
			}
		}
		getItemsFromList(selectedServerIDs);
	}

	private void getItemsFromList(List<UUID> selectedIDs) {
		items = new CharSequence[servers.size()];
		selected = new boolean[servers.size()];
		
		if(servers!=null) {
			for(int i=0;i<servers.size();i++) {
				Server server = servers.get(i);
				items[i] = server.getName();
				selected[i] = selectedIDs.contains(server.getId());
			}
		}		
	}
	
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
        		.setTitle("Select Servers")
                .setMultiChoiceItems(items, selected, new DialogInterface.OnMultiChoiceClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which, boolean isChecked) {
							selected[which] = isChecked; 
						}
					}
                )
                .setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        	ArrayList<Server> selectedServers = new ArrayList<Server>();
                        	for(int i=0;i<selected.length;i++) {
                        		if(selected[i]) {
                        			selectedServers.add(servers.get(i));
                        		}
                        	}
                        	listener.onServersSelected(selectedServers.toArray(new Server[0]));
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
    	public void onServersSelected(Server[] server);
    }
}
