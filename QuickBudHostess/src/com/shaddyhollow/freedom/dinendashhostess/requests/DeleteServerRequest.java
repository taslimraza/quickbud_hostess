package com.shaddyhollow.freedom.dinendashhostess.requests;

import java.util.UUID;

import com.flurry.android.FlurryAgent;
import com.shaddyhollow.quickbud.Config;
import com.shaddyhollow.quickbud.FlurryEvents;
import com.shaddyhollow.quickbud.datastore.ServerFactory;
import com.shaddyhollow.quicktable.models.Server;
import com.shaddyhollow.robospice.BaseRequest;

public class DeleteServerRequest extends BaseRequest<Void>{
//	private final String url =
//	        Config.getServerRoot() + "locations/{location_id}/servers/{server_id}.json";
	private final String url =
			Config.getServerRoot() + "qt/api/server/delete/?tenant_id={tenant_id}&server_id={server_id}";
	private Server server;
    private Integer tenantId;
    
    public DeleteServerRequest(Integer tenantId, Server server) {
       super(Void.class);
       this.tenantId = tenantId;
       this.server = server;
   		FlurryAgent.logEvent(FlurryEvents.SERVER_DELETE.name());
    }
    
	@Override
	public Void loadOnlineData() throws Exception {
		getRestTemplate().getForObject(url, Server.class, tenantId, server.getId());
		return null;
	}
	
	@Override 
	public Void loadOfflineData() {
		ServerFactory.getInstance().delete(server.getId());
		return null;
	}
}
