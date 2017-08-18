package com.shaddyhollow.freedom.dinendashhostess.requests;

import java.util.UUID;

import com.shaddyhollow.quickbud.Config;
import com.shaddyhollow.robospice.BaseRequest;

public class UpdateSectionStatusRequest extends BaseRequest<Void>{
    private final String url =
    Config.getServerRoot() + "locations/{location_id}/sections/{section_id}.json";

	 private UUID location_id;
	 private UUID section_id;
	 private SectionStatus sectionStatus;
    
	public UpdateSectionStatusRequest(UUID location_id, UUID section_id, boolean open) {
		super(Void.class);
	    this.location_id = location_id;
	    this.section_id = section_id;
	    
		sectionStatus = new SectionStatus();
		sectionStatus.open = open;
	}

    private class SectionStatus {
   	 @SuppressWarnings("unused")
		public boolean open;
    }

	@Override
	public Void loadOnlineData() throws Exception {
		getRestTemplate().put(url, sectionStatus, location_id, section_id);
		return null;
	}

}
