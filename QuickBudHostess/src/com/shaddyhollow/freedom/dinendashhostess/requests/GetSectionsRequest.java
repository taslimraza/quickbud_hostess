package com.shaddyhollow.freedom.dinendashhostess.requests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import com.shaddyhollow.quickbud.Config;
import com.shaddyhollow.quicktable.models.ListSections;
import com.shaddyhollow.quicktable.models.Section;
import com.shaddyhollow.robospice.BaseRequest;

public class GetSectionsRequest extends BaseRequest<ListSections>{
    private final String url =
    Config.getServerRoot() + "locations/{location_id}/sections.json";

    private UUID locationId;

	public GetSectionsRequest(UUID locationId) {
		super(ListSections.class);
		this.locationId = locationId;
	}

	@Override
	public ListSections loadOnlineData() throws Exception {
	 	Section[] sections = null;
 		sections = getRestTemplate().getForObject(url, Section[].class, locationId);
 		ListSections list = new ListSections();
	 	list.setSections(new ArrayList<Section>(Arrays.asList(sections)));
	    return list;  	}

}
