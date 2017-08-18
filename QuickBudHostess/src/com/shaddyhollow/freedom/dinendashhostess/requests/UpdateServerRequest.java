package com.shaddyhollow.freedom.dinendashhostess.requests;

import java.util.UUID;

import com.flurry.android.FlurryAgent;
import com.shaddyhollow.quickbud.Config;
import com.shaddyhollow.quickbud.FlurryEvents;
import com.shaddyhollow.quickbud.datastore.ServerFactory;
import com.shaddyhollow.quicktable.models.Server;
import com.shaddyhollow.robospice.BaseRequest;

public class UpdateServerRequest extends BaseRequest<Void>{
//	private final String url =
//	        Config.getServerRoot() + "locations/{location_id}/servers/{server_id}.json";
	private final String url =
			Config.getServerRoot() + "qt/api/server/modify/?tenant_id={tenant_id}&server_id={server_id}";
	
	private Server server;
    private Integer tenantId;
    
    public UpdateServerRequest(Integer tenantId, Server server) {
       super(Void.class);
       this.tenantId = tenantId;
       this.server = server;
   	   FlurryAgent.logEvent(FlurryEvents.SERVER_UPDATE.name());
    }
    
	@Override
	public Void loadOnlineData() throws Exception {
		getRestTemplate().postForObject(url, server, Server.class, tenantId, server.getId());
		return null;
	}
	
	@Override
	public Void loadOfflineData() {
		ServerFactory.getInstance().update(server);
		return null;
	}
}
