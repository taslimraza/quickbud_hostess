package com.shaddyhollow.quickbud.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.shaddyhollow.freedom.data.BackupBundle;
import com.shaddyhollow.freedom.dinendashhostess.printer.MiniPrinterFunctions;
import com.shaddyhollow.freedom.dinendashhostess.printer.TestReceipt;
import com.shaddyhollow.freedom.dinendashhostess.requests.AddAttachmentRequest;
import com.shaddyhollow.freedom.dinendashhostess.requests.GetAttachmentRequest;
import com.shaddyhollow.freedom.dinendashhostess.requests.GetServersRequest;
import com.shaddyhollow.home.UpdateActivity;
import com.shaddyhollow.quickbud.Config;
import com.shaddyhollow.quickbud.FlurryEvents;
import com.shaddyhollow.quickbud.datastore.FloorplanFactory;
import com.shaddyhollow.quickbud.datastore.SectionPlanFactory;
import com.shaddyhollow.quickbud.datastore.ServerFactory;
import com.shaddyhollow.quicktable.models.Attachment;
import com.shaddyhollow.quicktable.models.Floorplan;
import com.shaddyhollow.quicktable.models.HostessConfig;
import com.shaddyhollow.quicktable.models.ListServers;
import com.shaddyhollow.quicktable.models.SectionPlan;
import com.shaddyhollow.quicktable.models.Server;
import com.shaddyhollow.robospice.BaseListener;
import com.shaddyhollow.updatechecker.UpdateChecker;

public class SettingsFragment extends PreferenceFragment {
	int preferenceResourceID;
	SpiceManager contentManager;
	final static String PLAN_BACKUP_KEY = "hostess_config"; 
	
	public static SettingsFragment newInstance(SpiceManager contentManager, int preferenceResourceID) {
		SettingsFragment fragment = new SettingsFragment();

		fragment.contentManager = contentManager;
		fragment.preferenceResourceID = preferenceResourceID;
		return fragment;
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(preferenceResourceID);
        FlurryAgent.logEvent(FlurryEvents.SETTINGS_ACTIVITY.name());
    }

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
		boolean retVal = true;
		String selectedKey = preference.getKey();

		if(selectedKey.equals("carryout_printer_test")) {
			printTestReceipt(preference.getSharedPreferences().getString("carryout_printer", "BT:"), "Carry out printer");
		} else if(selectedKey.equals("dinein_printer_test")) {
			printTestReceipt(preference.getSharedPreferences().getString("dinein_printer", "BT:"), "Dine in printer");
		} else if(selectedKey.equals("layout_backup")) {
			performBackup();
		} else if(selectedKey.equals("layout_restore")) {
			performRestore();
		} else if(selectedKey.equals("layout_clear")) {
			performReset();
		}/* else if(selectedKey.equals("application_checkupdate")) {
			checkForUpdate();
		}*/ else {
			retVal = super.onPreferenceTreeClick(preferenceScreen, preference);
		}
		
		return retVal;
	}

	private void printTestReceipt(String printer, String testString) {
		TestReceipt receipt = new TestReceipt(testString);
		try {
			MiniPrinterFunctions.sendCommand(getActivity(), printer, "mini", receipt.getPrintList());
		} catch (Exception e) {
			Toast.makeText(getActivity(), "Error printing: " + e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}
	
	private void performRestore() {
	    new AlertDialog.Builder(getActivity())
        .setIcon(android.R.drawable.ic_dialog_alert)
        .setTitle("Restore plans")
        .setMessage("Are you really sure you want to download and overwrite floorplans and section plans?")
        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	        @Override
	        public void onClick(DialogInterface dialog, int which) {
	            FlurryAgent.logEvent(FlurryEvents.SETTINGS_RESTORE.name());
	            GetAttachmentRequest request = new GetAttachmentRequest(Config.getTenantID(), Config.getLocationID(), PLAN_BACKUP_KEY);
//	    		GetHostessConfigRequest request = new GetHostessConfigRequest(Config.getLocationID());
	    		request.execute(contentManager,  new GetHostessConfigRequestListener());
	    		Toast.makeText(getActivity(), "Getting backup from server", Toast.LENGTH_SHORT).show();
	        }
	    })
	    .setNegativeButton("No", null)
	    .show();
	}
	
	private void performReset() {
		new AlertDialog.Builder(getActivity())
	    .setIcon(android.R.drawable.ic_dialog_alert)
	    .setTitle("Clean local data")
	    .setMessage("Are you really sure you want to delete floorplans and section plans?")
	    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	        @Override
	        public void onClick(DialogInterface dialog, int which) {
	            FlurryAgent.logEvent(FlurryEvents.SETTINGS_DELETE.name());
	    		FloorplanFactory.getInstance().bulkDelete(null);
	    		SectionPlanFactory.getInstance().bulkDelete(null);
	    		Toast.makeText(getActivity(), "Floorplans and sectionplans reset", Toast.LENGTH_SHORT).show();
	        }

	    })
	    .setNegativeButton("No", null)
	    .show();
	}

	private class GetHostessConfigRequestListener extends BaseListener<Attachment> {
		@Override
		public void onFailure(SpiceException e) {
	        Toast.makeText( getActivity(), "Error restoring plans from server: " + e.getMessage(), Toast.LENGTH_LONG ).show();
		}
	
		@Override
		public void onRequestSuccess(Attachment attachment) {
			if(attachment==null) {
				return;
			}
	    	BackupBundle bundle = new BackupBundle();
	    	try {
	    		HostessConfig config = new HostessConfig();
	    		config.hostess_config = attachment.getValue();
	    		bundle.deserialize(config);
	    	} catch (Exception e) {
	    		Toast.makeText(getActivity(), "There is a problem restoring data. " + e.getMessage(), Toast.LENGTH_SHORT).show();
	    		return;
	    	}

	    	FloorplanFactory.getInstance().bulkDelete(null);
	    	SectionPlanFactory.getInstance().bulkDelete(null);

			for(Floorplan floorplan : bundle.floorplans) {
				FloorplanFactory.getInstance().createOrUpdate(floorplan);
			}
			for(SectionPlan sectionplan : bundle.sectionPlans) {
				SectionPlanFactory.getInstance().createOrUpdate(sectionplan);
			}
	    	
			syncRemoteServers();

	        Toast.makeText( getActivity(), 
	        		"Restored "+ bundle.floorplans.size() + " floor plans " + " and " + bundle.sectionPlans.size() + " section plans", 
	        		Toast.LENGTH_SHORT).show();
		}
	}

	private void syncRemoteServers() {

		// process servers
		ServerFactory.getInstance().bulkDelete(null);
//    	GetServersRequest request = new GetServersRequest(Config.location.getLocationId());
		GetServersRequest request = new GetServersRequest(Config.getTenantID(), Config.getLocationID());
        request.execute(contentManager, new BaseListener <ListServers>() {

			@Override
			public void onFailure(SpiceException e) {
		        Toast.makeText( getActivity(), "Error loading servers: " + e.getMessage(), Toast.LENGTH_LONG ).show();
			}
	
			@Override
			public void onRequestSuccess(ListServers servers) {
				if(servers==null) {
					return;
				}
				Toast.makeText(getActivity(), "Restoring " + servers.getServers().size() + " servers", Toast.LENGTH_SHORT).show();
				for(Server server : servers.getServers()) {
					if(!ServerFactory.getInstance().exists(server.getId())) {
						ServerFactory.getInstance().create(server);
					}
				}
			}
        });
    }
	
	private void performBackup() {
	    new AlertDialog.Builder(getActivity())
        .setIcon(android.R.drawable.ic_dialog_alert)
        .setTitle("Backup plans")
        .setMessage("Are you really sure you want to overwrite the floorplans and section plans on the server?")
        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	        @Override
	        public void onClick(DialogInterface dialog, int which) {
	            FlurryAgent.logEvent(FlurryEvents.SETTINGS_BACKUP.name());
	    		BackupBundle bundle = new BackupBundle();
	    		bundle.inititalize();
	    		HostessConfig config = bundle.serialize();
	    		
	    		AddAttachmentRequest request = new AddAttachmentRequest(Config.getTenantID(), Config.getLocationID(), PLAN_BACKUP_KEY, config.hostess_config);
//	    		UpdateHostessConfigRequest request = new UpdateHostessConfigRequest(Config.getLocationID(), config);
	    		request.execute(contentManager, new UpdateHostessConfigRequestListener());
	        	Toast.makeText(getActivity(), "Starting backup to server", Toast.LENGTH_SHORT).show();
	        }
	    })
	    .setNegativeButton("No", null)
	    .show();
	}

	private class UpdateHostessConfigRequestListener extends BaseListener<Void> {
		@Override
		public void onFailure(SpiceException e) {
	        Toast.makeText( getActivity(), "Error backing up plans to server: " + e.getMessage(), Toast.LENGTH_LONG ).show();
		}
	
		@Override
		public void onRequestSuccess(Void arg0) {
	    	Toast.makeText(getActivity(), "Floorplans and sectionplans backed up", Toast.LENGTH_SHORT).show();
		}
	}

	private void checkForUpdate() {
		Intent intent = new Intent(getActivity(), UpdateActivity.class);
		startActivityForResult(intent, UpdateActivity.UPDATE_RESULT);
    }
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		  switch(requestCode) {
		  case UpdateActivity.UPDATE_RESULT:
			  UpdateChecker checker = new UpdateChecker(getActivity(), true);
			  if(resultCode == Activity.RESULT_OK) {
				checker.downloadAndInstall(Config.getAPKURL());
			  } if(resultCode == UpdateActivity.NO_UPDATE_FOUND) {
				Toast.makeText(getActivity(), "There is no update available", Toast.LENGTH_SHORT).show();
			  }
			  break;
	     
		  }
	}


}
