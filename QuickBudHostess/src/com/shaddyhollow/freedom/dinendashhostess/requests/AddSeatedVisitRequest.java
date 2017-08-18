package com.shaddyhollow.freedom.dinendashhostess.requests;

import java.util.UUID;

import com.shaddyhollow.quickbud.Config;
import com.shaddyhollow.quicktable.models.QueuedVisit;
import com.shaddyhollow.quicktable.models.SeatedVisit;
import com.shaddyhollow.robospice.BaseRequest;

public class AddSeatedVisitRequest extends BaseRequest<SeatedVisit>{
//    private final String url =
//    Config.getServerRoot() + "locations/{location_id}/seated_visits.json";
	private final String url =
			Config.getServerRoot() + "qt/api/seated/{location_id}/seated_visits/?tenant_id={tenant_id}";
    SeatedVisitRequest request;
    QueuedVisit queuedVisit;
    Integer locationId;
    Integer tenantId;
    
	public AddSeatedVisitRequest(Integer tenantId, Integer locationId, QueuedVisit queuedVisit, SeatedVisit seatedVisit) {
		super(SeatedVisit.class);
    	this.queuedVisit = queuedVisit;
    	this.tenantId = tenantId;
    	this.locationId = locationId;
    	this.request = new SeatedVisitRequest();
    	this.request.visit_id = seatedVisit.visit_id;
    	this.request.server_id = seatedVisit.server_id;
    	this.request.party_size = seatedVisit.party_size;
    }

    private class SeatedVisitRequest {
    	@SuppressWarnings("unused")
		public UUID visit_id;
    	@SuppressWarnings("unused")
    	public UUID server_id;
    	@SuppressWarnings("unused")
    	public int party_size;
    }
    
	@Override
	public SeatedVisit loadOnlineData()
			throws Exception {
    	return getRestTemplate().postForObject(url, request, SeatedVisit.class, String.valueOf(locationId), String.valueOf(tenantId));
	}

}
