package com.shaddyhollow.freedom.dinendashhostess.requests;

import java.util.UUID;

import com.shaddyhollow.quickbud.Config;
import com.shaddyhollow.robospice.BaseRequest;

public class TextCarryOutRequest extends BaseRequest<Void>{
//    private final String url =
//        Config.getServerRoot() + "locations/{location_id}/carry_out_visits/{carryout_visitid}/send_message.json";
	private final String url =
			Config.getServerRoot() + "qb/api/send/send_sms/?tenant_id={tenant_id}&location_id={location_id}&queued_visit_id={queued_visit_id}";
	
	private UUID carryoutVisitId;
    private UUID locationId;
    private Message message;
    
	public TextCarryOutRequest(UUID locationId, UUID carryoutVisitId, String message) {
		super(Void.class);
    	this.carryoutVisitId = carryoutVisitId;
    	this.locationId = locationId;
    	this.message = new Message(message);
    }
	
	@Override
	public Void loadOnlineData() throws Exception {
		getRestTemplate().put(url, message, locationId, carryoutVisitId); 
		return null;
	}
	
	class Message {
		String message;
		
		public Message(String message) {
			this.message = message;
		}
	}

}
