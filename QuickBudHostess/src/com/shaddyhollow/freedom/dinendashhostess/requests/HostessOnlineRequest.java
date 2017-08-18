package com.shaddyhollow.freedom.dinendashhostess.requests;

import android.util.Log;

import com.shaddyhollow.quickbud.Config;
import com.shaddyhollow.quicktable.models.removeQueuedVisitResponse;
import com.shaddyhollow.robospice.BaseRequest;

public class HostessOnlineRequest extends BaseRequest<Void>{
	
	private final String url =
    		Config.getServerRoot() + "qb/api/active/";

	public HostessOnlineRequest(Class<Void> resultType) {
		super(resultType);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public Void loadOnlineData() throws Exception {
		getRestTemplate().getForEntity(url, removeQueuedVisitResponse.class); 
		Log.i("Hostess Online", "true");
		return null;
	}

}
