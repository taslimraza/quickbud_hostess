package com.shaddyhollow.freedom.dinendashhostess.requests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import com.shaddyhollow.quickbud.Config;
import com.shaddyhollow.quicktable.models.ListSectionplans;
import com.shaddyhollow.quicktable.models.SectionPlan;
import com.shaddyhollow.robospice.BaseRequest;

public class GetSectionplansRequest extends BaseRequest<ListSectionplans>{
    private final String url =
    Config.getServerRoot() + "locations/{location_id}/sectionplans.json";

    private UUID locationId;

	public GetSectionplansRequest(UUID locationId) {
		super(ListSectionplans.class);
		this.locationId = locationId;
	}

	@Override
	public ListSectionplans loadOnlineData() throws Exception {
	 	SectionPlan[] sectionplans = null;
 		sectionplans = getRestTemplate().getForObject(url, SectionPlan[].class, locationId);
 		ListSectionplans list = new ListSectionplans();
	 	list.setSectionplans(new ArrayList<SectionPlan>(Arrays.asList(sectionplans)));
	    return list;  	}

}
