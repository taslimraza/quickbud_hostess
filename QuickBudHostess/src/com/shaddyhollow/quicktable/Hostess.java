package com.shaddyhollow.quicktable;

import android.app.Application;
import android.content.pm.PackageManager.NameNotFoundException;

import com.shaddyhollow.quickbud.Config;

public class Hostess extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		try {
			Config.versionName = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0).versionName;
			Config.packageName = getApplicationContext().getPackageName();
		} catch (NameNotFoundException e) {
		}

		Config.CARRYOUTONLY_ENABLED = true;
	}

}
