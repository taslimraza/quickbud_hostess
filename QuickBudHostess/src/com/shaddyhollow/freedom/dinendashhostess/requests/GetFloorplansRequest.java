package com.shaddyhollow.freedom.dinendashhostess.requests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import com.shaddyhollow.quickbud.Config;
import com.shaddyhollow.quicktable.models.Floorplan;
import com.shaddyhollow.quicktable.models.ListFloorplans;
import com.shaddyhollow.robospice.BaseRequest;

public class GetFloorplansRequest extends BaseRequest<ListFloorplans>{
    private final String url =
    Config.getServerRoot() + "locations/{location_id}/floorplans.json";

    private UUID locationId;

	public GetFloorplansRequest(UUID locationId) {
		super(ListFloorplans.class);
		this.locationId = locationId;
	}

	@Override
	public ListFloorplans loadOnlineData() throws Exception {
	 	Floorplan[] floorplans = null;
 		floorplans = getRestTemplate().getForObject(url, Floorplan[].class, locationId);
 		ListFloorplans list = new ListFloorplans();
	 	list.setFloorplans(new ArrayList<Floorplan>(Arrays.asList(floorplans)));
	    return list;  	}

}
