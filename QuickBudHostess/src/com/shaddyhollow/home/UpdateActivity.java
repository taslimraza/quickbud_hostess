package com.shaddyhollow.home;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.flurry.android.FlurryAgent;
import com.shaddyhollow.quickbud.R;
import com.shaddyhollow.quickbud.Config;
import com.shaddyhollow.quickbud.FlurryEvents;
import com.shaddyhollow.updatechecker.UpdateChecker;

public class UpdateActivity extends Activity {
	final UpdateChecker checker = new UpdateChecker(this, true);
	public final static int UPDATE_RESULT = 3100;
	public final static int NO_UPDATE_FOUND=3101;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        FlurryAgent.logEvent(FlurryEvents.SETTINGS_UPDATECHECK.name());

		new Progress().execute();
	}

	public void showDialog() {
        FlurryAgent.logEvent(FlurryEvents.UPDATE_FOUND.name());
		setContentView(R.layout.update_activity);
		findViewById(R.id.btn_ok).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
			            FlurryAgent.logEvent(FlurryEvents.UPDATE_ACCEPTED.name());
//						checker.downloadAndInstall(Config.getAPKURL());
						setResult(RESULT_OK);
						finish();
					}
				});
		
		findViewById(R.id.btn_cancel).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
			            FlurryAgent.logEvent(FlurryEvents.UPDATE_DECLINED.name());
						setResult(RESULT_CANCELED);
						finish();
					}
				});
	}
	
	public class Progress extends AsyncTask<String, Void, Void> {
		ProgressDialog dialog;
		boolean updateAvailable = false;

		protected void onPreExecute() {
			UpdateActivity.this.setVisible(false);
			dialog = ProgressDialog.show(UpdateActivity.this,"","Checking for updates. Please wait...", true);
			dialog.show();
		}

		@Override
		protected Void doInBackground(String... params) {
			try {
				checker.checkForUpdateByVersionCode(Config.getVersionURL());
				updateAvailable = checker.isUpdateAvailable();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(Void unused) {
			try {
				dialog.dismiss();
			} catch (Exception e) {
				// window may no longer be attached to view if page changed
			}
			if(!updateAvailable) {
				UpdateActivity.this.setResult(NO_UPDATE_FOUND);
				UpdateActivity.this.finish();
			} else {
				UpdateActivity.this.showDialog();
			}
		}

	}
}