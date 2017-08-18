package com.shaddyhollow.freedom.dinendashhostess.requests;

import java.util.UUID;

import com.shaddyhollow.quickbud.Config;
import com.shaddyhollow.quicktable.models.HostessConfig;
import com.shaddyhollow.robospice.BaseRequest;

public class GetHostessConfigRequest extends BaseRequest<HostessConfig>{
    private final String url =
    Config.getServerRoot() + "locations/{location_id}.json";

    private UUID locationId;

	public GetHostessConfigRequest(UUID locationId) {
		super(HostessConfig.class);
		this.locationId = locationId;
	}

	@Override
	public HostessConfig loadOnlineData() throws Exception {
 		return getRestTemplate().getForObject(url, HostessConfig.class, locationId);
	}

}
