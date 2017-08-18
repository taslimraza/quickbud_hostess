package com.shaddyhollow.robospice;

import android.app.Activity;
import android.content.pm.PackageManager.NameNotFoundException;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;

import com.flurry.android.FlurryAgent;
import com.octo.android.robospice.SpiceManager;
import com.shaddyhollow.quickbud.Config;

public class BaseRoboSpiceActivity extends Activity {
    public SpiceManager contentManager = new SpiceManager( OfflineableGsonSpringAndroidSpiceService.class );

    @Override
    protected void onStart() {
        super.onStart();
        String versionName = "";

        try {
			versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
		}
   
        contentManager.start( this );
    	contentManager.cancelAllRequests();

        FlurryAgent.onStartSession(this, "YCVCTYVS8XMN67RHBRH2");
        FlurryAgent.setContinueSessionMillis(1000 * 60 * 60 * 12); // ms/sec * sec/min * min/hr * hr
//        if(Config.location!=null && Config.location.getId()!=null) {
//        	FlurryAgent.setUserId(Config.location.getId().toString());
//        }
        FlurryAgent.setVersionName(versionName);
    }

    @Override
    protected void onStop() {
        super.onStop();
        
    	if(contentManager.isStarted()) {
    		contentManager.shouldStop();
    	}
        FlurryAgent.onEndSession(this);
    }
    
    
}
