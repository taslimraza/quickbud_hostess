package com.shaddyhollow.freedom.dinendashhostess.requests;

import java.util.UUID;

import com.shaddyhollow.quickbud.Config;
import com.shaddyhollow.quicktable.models.HostessConfig;
import com.shaddyhollow.robospice.BaseRequest;

public class UpdateHostessConfigRequest extends BaseRequest<Void>{
//    private final String url =
//    		Config.getServerRoot() + "locations/{location_id}.json";
	  private final String url =
			  Config.getServerRoot() + "qt/api/location/{location_id}/attachments/?tenant_id={tenant_id}";
	  
    private Integer location_id;
    private Integer tenant_id;
    private HostessConfig hostessConfig;
    
	public UpdateHostessConfigRequest(Integer location_id, Integer tenant_id, HostessConfig hostessConfig) {
		super(Void.class);
        this.location_id = location_id;
        this.tenant_id = tenant_id;
        this.hostessConfig = hostessConfig;
	}

	@Override
	public Void loadOnlineData() throws Exception {
		getRestTemplate().put(url, hostessConfig, location_id, tenant_id);
		return null;
	}

}