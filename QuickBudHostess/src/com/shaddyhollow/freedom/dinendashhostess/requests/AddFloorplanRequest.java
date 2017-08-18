package com.shaddyhollow.freedom.dinendashhostess.requests;

import com.shaddyhollow.quickbud.Config;
import com.shaddyhollow.quicktable.models.FloorplanToSave;
import com.shaddyhollow.robospice.BaseRequest;

public class AddFloorplanRequest extends BaseRequest<FloorplanToSave>{
    private final String url =
    Config.getServerRoot() + "locations/{location_id}/floorplans.json";

    private FloorplanToSave request;
    
	public AddFloorplanRequest(FloorplanToSave request) {
		super(FloorplanToSave.class);
		this.request = request;
	}

	@Override
	public FloorplanToSave loadOnlineData()
			throws Exception {
    	return getRestTemplate().postForObject(url, request, FloorplanToSave.class, request.getLocation_id());
	}

}
