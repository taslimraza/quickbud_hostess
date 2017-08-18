package com.shaddyhollow.freedom.dinendashhostess.requests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import com.shaddyhollow.quickbud.Config;
import com.shaddyhollow.quicktable.models.ListServers;
import com.shaddyhollow.quicktable.models.Server;
import com.shaddyhollow.robospice.BaseRequest;

public class GetServersRequest extends BaseRequest<ListServers>{
//    private final String url =
//    Config.getServerRoot() + "locations/{location_id}/servers.json";
    private final String url = 
    		Config.getServerRoot() + "qt/api/server/?tenant_id={tenant_id}&location_id={location_id}";

    private Integer locationId;
    private Integer tenantId;

	public GetServersRequest(Integer tenantId, Integer locationId) {
		super(ListServers.class);
		this.tenantId = tenantId;
		this.locationId = locationId;
	}

	@Override
	public ListServers loadOnlineData() throws Exception {
	 	Server[] sections = null;
 		sections = getRestTemplate().getForObject(url, Server[].class, tenantId, locationId);
 		ListServers list = new ListServers();
	 	list.setServers(new ArrayList<Server>(Arrays.asList(sections)));
	    return list;  	
	    }
}
