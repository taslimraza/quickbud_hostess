package com.shaddyhollow.freedom.dinendashhostess.requests;

import com.shaddyhollow.quickbud.Config;
import com.shaddyhollow.quicktable.models.SectionPlanToSave;
import com.shaddyhollow.robospice.BaseRequest;

public class AddSectionplanRequest extends BaseRequest<SectionPlanToSave>{
    private final String url =
    Config.getServerRoot() + "locations/{location_id}/sectionplans.json";

    private SectionPlanToSave request;
    
	public AddSectionplanRequest(SectionPlanToSave request) {
		super(SectionPlanToSave.class);
		this.request = request;
	}

	@Override
	public SectionPlanToSave loadOnlineData()
			throws Exception {
    	return getRestTemplate().postForObject(url, request, SectionPlanToSave.class, request.getLocation_id());
	}

}
