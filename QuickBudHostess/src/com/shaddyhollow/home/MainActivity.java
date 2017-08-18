package com.shaddyhollow.home;

import java.util.UUID;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.gson.Gson;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.shaddyhollow.quickbud.R;
import com.shaddyhollow.freedom.dinendashhostess.requests.HostessOnlineRequest;
import com.shaddyhollow.freedom.dinendashhostess.requests.LogoutHostessRequest;
import com.shaddyhollow.freedom.floorplans.FloorplanManagerActivity;
import com.shaddyhollow.freedom.hostess.CarryoutActivity;
import com.shaddyhollow.freedom.hostess.HostessActivity;
import com.shaddyhollow.freedom.sectionplans.SectionPlanManagerActivity;
import com.shaddyhollow.freedom.servers.ServerManagerActivity;
import com.shaddyhollow.quickbud.Config;
import com.shaddyhollow.quickbud.DataResetUtil;
import com.shaddyhollow.quickbud.FlurryEvents;
import com.shaddyhollow.quickbud.datastore.CarryoutFactory;
import com.shaddyhollow.quickbud.datastore.FloorplanFactory;
import com.shaddyhollow.quickbud.datastore.QueuedVisitFactory;
import com.shaddyhollow.quickbud.datastore.SectionPlanFactory;
import com.shaddyhollow.quickbud.datastore.ServerFactory;
import com.shaddyhollow.quickbud.datastore.TableFactory;
import com.shaddyhollow.quickbud.datastore.TabletStateHelper;
import com.shaddyhollow.quickbud.settings.SettingsActivity;
import com.shaddyhollow.quicktable.models.LocationEntry;
import com.shaddyhollow.robospice.BaseListener;
import com.shaddyhollow.robospice.BaseRoboSpiceActivity;
import com.shaddyhollow.updatechecker.UpdateChecker;
import com.shaddyhollow.util.ButtonHighlighterOnTouchListener;

/**
 * Look for more info about the auto-updating at
 * https://github.com/RaghavSood/AppaholicsUpdateChecker
 * /wiki/API-2-Integration-Guide
 * 
 * @author sashikolli
 *
 */
public class MainActivity extends BaseRoboSpiceActivity {
	private final static int LOGIN = 100;
	private Handler handler = new Handler();
	private static boolean isLoggedIn = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Crashlytics.start(this);
		setContentView(R.layout.activity_main);
		Config.setOnline(true);
		
		getActionBar().setTitle("Dispensary App Management");
		getActionBar().setIcon(R.drawable.quickbud_image);

		View carryoutSection = findViewById(R.id.carryoutSection);
		carryoutSection
				.setVisibility(Config.CARRYOUTONLY_ENABLED ? View.VISIBLE
						: View.GONE);

		ImageButton btn_hostess = (ImageButton) findViewById(R.id.tableManagement);
		btn_hostess.setOnTouchListener(new ButtonHighlighterOnTouchListener(
				btn_hostess));

		ImageButton btn_carryouts = (ImageButton) findViewById(R.id.carryoutManagement);
		btn_carryouts.setOnTouchListener(new ButtonHighlighterOnTouchListener(
				btn_carryouts));

		initFactories();
		
	}
	
	private Runnable runnable = new Runnable() {
		public void run() {
			HostessOnlineRequest request = new HostessOnlineRequest(null);
			request.execute(contentManager, new HostessOnlineListener());
			hostessOnline();
		}
	};
	
	private void initFactories() {
		FloorplanFactory.getInstance(getApplicationContext());
		TableFactory.getInstance(getApplicationContext());
		SectionPlanFactory.getInstance(getApplicationContext());
		ServerFactory.getInstance(getApplicationContext());
		QueuedVisitFactory.getInstance(getApplicationContext());
		CarryoutFactory.getInstance(getApplicationContext());
	}

	@Override
	public void onResume() {
		super.onResume();
		updateVersionName();
		if (isLoggedIn){
			hostessOnline();
		}else {
			login();	
		}
	}

	public void login() {
		if (Config.location == null) {
			Intent intent = new Intent(this, LoginActivity.class);
			startActivityForResult(intent, LOGIN);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (ActivityManager.isUserAMonkey()) {
			return true;
		}
		switch (item.getItemId()) {
		case R.id.action_dailyreset: {
			DataResetUtil resetUtil = new DataResetUtil(this, contentManager);
			resetUtil.performDailyReset();
			FlurryAgent.logEvent(FlurryEvents.HOME_DAILYRESET.name());
		}
			break;
		case R.id.action_logout: {
			logout();
			// Config.location = null;
			// FlurryAgent.logEvent(FlurryEvents.HOME_LOGOUT.name());
			// login();
		}
			break;
		case R.id.action_backupstate: {
			TabletStateHelper.exportDB(this, contentManager);
		}
			break;
		case R.id.action_restorestate: {
			TabletStateHelper.importDB(this, contentManager);
		}
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void manageTables(View view) {

		if (handler != null) {
			handler.removeCallbacks(runnable);
		}
		
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(Config.adminUrl));
		startActivity(intent);
		
//		Toast.makeText(this, "Coming soon...", Toast.LENGTH_SHORT).show();

//		FlurryAgent.logEvent(FlurryEvents.HOME_TABLEMANAGEMENT.name());
//		Intent myIntent = new Intent(MainActivity.this, HostessActivity.class);
//		myIntent.putExtra("LOCATIONID", Config.getLocationID());
//		myIntent.putExtra("TENANTID", Config.getTenantID());
//		myIntent.putExtra("RESTNAME", Config.getRestaurantName());
//		// myIntent.putExtra("LOCATIONID", 5);
//		// myIntent.putExtra("TENANTID", 1);
//		startActivity(myIntent);
	}

	public void manageCarryouts(View view) {
		
		if (handler != null) {
			handler.removeCallbacks(runnable);
		}
		
		FlurryAgent.logEvent(FlurryEvents.HOME_CARRYOUTS.name());
		Intent myIntent = new Intent(MainActivity.this, CarryoutActivity.class);
		myIntent.putExtra("LOCATIONID", Config.getLocationID());
		myIntent.putExtra("TENANTID", Config.getTenantID());
		myIntent.putExtra("RESTNAME", Config.getRestaurantName());
		// myIntent.putExtra("LOCATIONID", 1);
		// myIntent.putExtra("TENANTID", 5);
		startActivity(myIntent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_home, menu);
		return true;
	}

	private void updateVersionName() {
		try {
			String versionName = Config.serverName
					+ " "
					+ getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
			TextView versionField = (TextView) findViewById(R.id.version);
			versionField.setText(versionName);
		} catch (Exception e) {
		}
	}

	private void setupCrashlyticsVariables(String email) {
		try {
			Crashlytics.setUserIdentifier("" + Config.location.getLocationId());
		} catch (Exception e) {
		}
		try {
			Crashlytics.setUserEmail(email);
		} catch (Exception e) {
		}
		try {
			Crashlytics.setUserName(Config.location.getName() + " - "
					+ Config.location.getCity() + " [" + Config.serverName
					+ "]");
		} catch (Exception e) {
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case LOGIN:
			if (resultCode == RESULT_OK) {
				isLoggedIn = true;
				getActionBar().setTitle("Dispensary App Management"/* + Config.serverName*/);
				getActionBar().setIcon(R.drawable.quickbud_image);

				updateVersionName();

				Gson gson = new Gson();
				String locationJson = data.getStringExtra("location");
				Config.location = gson.fromJson(locationJson,
						LocationEntry.class);

				setupCrashlyticsVariables(data.getStringExtra("email"));
				if (Config.isOnline()) {
					SharedPreferences prefs = PreferenceManager
							.getDefaultSharedPreferences(this);
					Integer previousLocation = prefs.getInt(
							"PREVIOUS_LOCATION", 0);
					Log.i("LocationId", previousLocation.toString());
					if (previousLocation != Config.location.getLocationId()) {
						// performRestore();
						Editor editor = prefs.edit();
						editor.putInt("PREVIOUS_LOCATION",
								Config.location.getLocationId());
						editor.commit();
					}
				}
			} else {
				finish();
			}
			break;
		/*
		 * case UpdateActivity.UPDATE_RESULT: UpdateChecker checker = new
		 * UpdateChecker(this, true); if(resultCode == Activity.RESULT_OK) {
		 * checker.downloadAndInstall(Config.getAPKURL()); } else if(resultCode
		 * == UpdateActivity.NO_UPDATE_FOUND) { Toast.makeText(this,
		 * "There is no update available", Toast.LENGTH_SHORT).show(); } break;
		 */

		}
	}

	public void logout() {
		if (handler != null) {
			handler.removeCallbacks(runnable);
		}
		LogoutHostessRequest request = new LogoutHostessRequest(null);
		request.execute(contentManager, new LogoutHostessListener());
	}

	private class LogoutHostessListener extends BaseListener<Void> {

		@Override
		public void onFailure(SpiceException e) {
			
			isLoggedIn = false;
			Config.location = null;
			FlurryAgent.logEvent(FlurryEvents.HOME_LOGOUT.name());
			login();
			
			// Toast.makeText( getApplicationContext(),
			// "Error during Hostess Login: " + e.getMessage(),
			// Toast.LENGTH_LONG ).show();
			// FlurryAgent.logEvent(FlurryEvents.LOGIN_FAIL.name());
			// showProgress(false);
		}

		@Override
		public void onRequestSuccess(Void obj) {
			// TODO Auto-generated method stub
			isLoggedIn = false;
			Config.location = null;
			FlurryAgent.logEvent(FlurryEvents.HOME_LOGOUT.name());
			login();
		}
	}

	public void hostessOnline() {
		handler.postDelayed(runnable, 60 * 1000);
	}

	private class HostessOnlineListener extends BaseListener<Void> {

		@Override
		public void onFailure(SpiceException e) {
//			hostessOnline();
			// Toast.makeText( getApplicationContext(),
			// "Error during Hostess Login: " + e.getMessage(),
			// Toast.LENGTH_LONG ).show();
			// FlurryAgent.logEvent(FlurryEvents.LOGIN_FAIL.name());
			// showProgress(false);
		}

		@Override
		public void onRequestSuccess(Void obj) {
//			hostessOnline();
			// TODO Auto-generated method stub
			// Config.location = null;
			// FlurryAgent.logEvent(FlurryEvents.HOME_LOGOUT.name());
			// login();
		}
	}
}
