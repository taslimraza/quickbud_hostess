package com.shaddyhollow.freedom.dinendashhostess.requests;

import java.util.UUID;

import com.shaddyhollow.quickbud.Config;
import com.shaddyhollow.quicktable.models.removeQueuedVisitResponse;
import com.shaddyhollow.robospice.BaseRequest;

public class PageCarryOutRequest extends BaseRequest<Void>{
    private final String url =
    		Config.getServerRoot() + "qb/api/queuedvisit/page/";
    private UUID carryoutVisitId;
    private Integer locationId;
    private Integer tenantId;
    private Message message;
    private pageRequest request;
    
	public PageCarryOutRequest(Integer tenantId, Integer locationId, UUID carryoutVisitId, String message) {
		super(Void.class);
    	this.carryoutVisitId = carryoutVisitId;
    	this.locationId = locationId;
    	this.tenantId = tenantId;
    	this.message = new Message(message);
    	this.request = new pageRequest();
    	this.request.tenant_id = tenantId;
    	this.request.location_id = locationId;
    	this.request.visit_id = carryoutVisitId;
    	this.request.message = message;
    }
	
	@Override
	public Void loadOnlineData() throws Exception {
		getRestTemplate().postForObject(url, request, removeQueuedVisitResponse.class/*locationId, carryoutVisitId*/); 
		return null;
	}
	
	class Message {
		String message;
		
		public Message(String message) {
			this.message = message;
		}
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
