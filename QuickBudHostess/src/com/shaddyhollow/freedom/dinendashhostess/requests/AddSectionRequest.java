package com.shaddyhollow.freedom.dinendashhostess.requests;

import java.util.UUID;

import com.shaddyhollow.quickbud.Config;
import com.shaddyhollow.quicktable.models.Section;
import com.shaddyhollow.robospice.BaseRequest;

public class AddSectionRequest extends BaseRequest<Void>{
	private final String url =
	        Config.getServerRoot() + "locations/{location_id}/sections.json";
    private UUID location_id;
    private String section_name;
    private boolean section_open;
    
    public AddSectionRequest(UUID location_id, Section section) {
       super(Void.class);
       this.location_id = location_id;
       this.section_name = section.name;
       this.section_open = section.open;
    }
    
	@Override
	public Void loadOnlineData() throws Exception {
		getRestTemplate().put(url, null, location_id, section_name, section_open); 
		return null;
	}
}
