package com.shaddyhollow.freedom.dinendashhostess.requests;

import com.shaddyhollow.quickbud.Config;
import com.shaddyhollow.quicktable.models.removeQueuedVisitResponse;
import com.shaddyhollow.robospice.BaseRequest;

public class LogoutHostessRequest extends BaseRequest<Void>{
	
	private final String url =
    		Config.getServerRoot() + "qb/api/logout/";

	public LogoutHostessRequest(Class<Void> resultType) {
		super(resultType);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public Void loadOnlineData() throws Exception {
		getRestTemplate().getForEntity(url, removeQueuedVisitResponse.class); 
		return null;
	}

}
