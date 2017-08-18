package com.shaddyhollow.freedom.dinendashhostess.requests;

import java.util.UUID;

import com.shaddyhollow.quickbud.Config;
import com.shaddyhollow.robospice.BaseRequest;

public class TextPatronRequest extends BaseRequest<Void>{
//    private final String url =
//    Config.getServerRoot() + "locations/{location_id}/queued_visits/{queued_visit_id}/send_message.json";
	private final String url =
			Config.getServerRoot() + "qb/api/send/send_sms/?tenant_id={tenant_id}&location_id={location_id}&queued_visit_id={queued_visit_id}";
    private UUID queuedVisitId;
    private Integer locationId;
    private Integer tenantId;
    private Message message;
    
	public TextPatronRequest(Integer tenantId, Integer locationId, UUID queuedVisitId, String message) {
		super(Void.class);
    	this.queuedVisitId = queuedVisitId;
    	this.tenantId = tenantId;
    	this.locationId = locationId;
    	this.message = new Message(message);
    }
	
	@Override
	public Void loadOnlineData() throws Exception {
		getRestTemplate().put(url, message, tenantId, locationId, queuedVisitId); 
		return null;
	}
	
	class Message {
		String message;
		
		public Message(String message) {
			this.message = message;
		}
	}

}
