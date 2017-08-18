package com.shaddyhollow.quickbud;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import com.flurry.android.FlurryAgent;
import com.octo.android.robospice.SpiceManager;
import com.shaddyhollow.freedom.dinendashhostess.requests.DeleteAllCarryOutVisitsRequest;
import com.shaddyhollow.freedom.dinendashhostess.requests.DeleteAllQueuedVisitsRequest;
import com.shaddyhollow.freedom.dinendashhostess.requests.DeleteAllSeatedVisitsRequest;
import com.shaddyhollow.home.UpdateActivity;
import com.shaddyhollow.quickbud.datastore.QueuedVisitFactory;
import com.shaddyhollow.quickbud.datastore.SectionPlanFactory;
import com.shaddyhollow.quickbud.datastore.ServerFactory;

public class DataResetUtil {
	private Activity activity = null;
	private SpiceManager contentManager = null;

	public DataResetUtil(Activity activity, SpiceManager contentManager) {
		this.activity = activity;
		this.contentManager = contentManager;
	}
	
	public void performDailyReset() {
	    new AlertDialog.Builder(activity)
        .setIcon(android.R.drawable.ic_dialog_alert)
        .setTitle("Daily Reset")
        .setMessage("Are you ready to reset the seating plan for the day?")
        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	        @Override
	        public void onClick(DialogInterface dialog, int which) {
		    	FlurryAgent.logEvent(FlurryEvents.HOSTESS_DAILYRESET.name());
	        	ServerFactory.getInstance().dailyReset();
        		SectionPlanFactory.getInstance().dailyReset();

	        	try {
	        		DeleteAllCarryOutVisitsRequest request = new DeleteAllCarryOutVisitsRequest(Config.getTenantID(), Config.getLocationID());
	        		request.execute(contentManager, null);
	        	} catch (Exception e) {
	        	}

	        	try {
	        		DeleteAllQueuedVisitsRequest request = new DeleteAllQueuedVisitsRequest(Config.getTenantID(), Config.getLocationID());
	        		request.execute(contentManager, null);
	        	} catch (Exception e) {
	        	}

	        	try {
	        		DeleteAllSeatedVisitsRequest request = new DeleteAllSeatedVisitsRequest(Config.getTenantID(), Config.getLocationID());
	        		request.execute(contentManager, null);
	        	} catch (Exception e) {
	        	}
	        	
//	        	Intent intent = new Intent(activity, UpdateActivity.class);
//				activity.startActivityForResult(intent, UpdateActivity.UPDATE_RESULT);
	        }

	    })
	    .setNegativeButton("No", null)
	    .show();
	}


}
