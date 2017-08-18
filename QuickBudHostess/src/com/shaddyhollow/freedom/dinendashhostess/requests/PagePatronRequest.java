package com.shaddyhollow.freedom.dinendashhostess.requests;

import java.util.UUID;

import com.shaddyhollow.freedom.dinendashhostess.requests.PageCarryOutRequest.pageRequest;
import com.shaddyhollow.quickbud.Config;
import com.shaddyhollow.quicktable.models.removeQueuedVisitResponse;
import com.shaddyhollow.robospice.BaseRequest;

public class PagePatronRequest extends BaseRequest<Void>{
    private final String url =
    		Config.getServerRoot() + "qb/api/queuedvisit/page/";

    private UUID queuedVisitId;
    private Integer locationId;
    private Integer tenantId;
    private pageRequest request;
    
	public PagePatronRequest(Integer tenantId, Integer locationId, UUID queuedVisitId, String message) {
		super(Void.class);
    	this.queuedVisitId = queuedVisitId;
    	this.locationId = locationId;
    	this.tenantId = tenantId;
    	this.request = new pageRequest();
    	this.request.tenant_id = tenantId;
    	this.request.location_id = locationId;
    	this.request.visit_id = queuedVisitId;
    	this.request.message = message;
    }
	
	@Override
	public Void loadOnlineData() throws Exception {
		getRestTemplate().postForObject(url, request, removeQueuedVisitResponse.class); 
		return null;
	}
	
	class pageRequest{
		@SuppressWarnings("unused")
		private int tenant_id;
		@SuppressWarnings("unused")
		private int location_id;
		@SuppressWarnings("unused")
		private UUID visit_id;
		@SuppressWarnings("unused")
		private String message;
	}

}
