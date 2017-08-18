package com.shaddyhollow.freedom.dinendashhostess.requests;

import java.util.UUID;

import com.shaddyhollow.quickbud.Config;
import com.shaddyhollow.robospice.BaseRequest;

public class ResetLayoutRequest extends BaseRequest<Void>{
    private final String url =
    		Config.getServerRoot() + "locations/{location_id}/tables/reset_layout.json";

    private UUID location_id;
    
	public ResetLayoutRequest(UUID location_id) {
		super(Void.class);
        this.location_id = location_id;
	}

	@Override
	public Void loadOnlineData() throws Exception {
		getRestTemplate().put(url, null, location_id);
		return null;
	}

}