package com.shaddyhollow.freedom.dinendashhostess.requests;

import com.flurry.android.FlurryAgent;
import com.shaddyhollow.quickbud.Config;
import com.shaddyhollow.quickbud.FlurryEvents;
import com.shaddyhollow.quickbud.datastore.ServerFactory;
import com.shaddyhollow.quicktable.models.Server;
import com.shaddyhollow.robospice.BaseRequest;

public class AddServerRequest extends BaseRequest<Server>{
//	private final String url =
//	        Config.getServerRoot() + "locations/{location_id}/servers";
	private final String url = Config.getServerRoot() + "qt/api/server/?tenant_id={tenant_id}&location_id={location_id}";
    private Server server;
    private Integer tenantId;
    private Integer location_id;
    
    public AddServerRequest(Integer tenantId, Integer location_id, Server server) {
       super(Server.class);
       this.tenantId = tenantId;
       this.location_id = location_id;
       this.server = server;
   	   FlurryAgent.logEvent(FlurryEvents.SERVER_CREATE.name());
    }
    
	@Override
	public Server loadOnlineData() throws Exception {
		Server newServer = getRestTemplate().postForObject(url, server, Server.class, tenantId, location_id);
		return newServer;
	}
	
	@Override
	public Server loadOfflineData() {
		return ServerFactory.getInstance().create(server);
	}

}
