package com.shaddyhollow.freedom.dinendashhostess.requests;

import java.util.UUID;

import com.shaddyhollow.quickbud.Config;
import com.shaddyhollow.robospice.BaseRequest;

public class SetDefaultLayoutRequest extends BaseRequest<Void>{
    private final String url =
    		Config.getServerRoot() + "locations/{location_id}/tables/set_defaults.json";

    private UUID location_id;
    
	public SetDefaultLayoutRequest(UUID location_id) {
		super(Void.class);
        this.location_id = location_id;
	}

	@Override
	public Void loadOnlineData() throws Exception {
		getRestTemplate().put(url, null, location_id);
		return null;
	}

}