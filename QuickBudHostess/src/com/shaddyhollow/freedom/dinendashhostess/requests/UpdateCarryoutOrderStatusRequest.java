package com.shaddyhollow.freedom.dinendashhostess.requests;

import java.util.UUID;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;
import com.shaddyhollow.quickbud.Config;


public class UpdateCarryoutOrderStatusRequest extends SpringAndroidSpiceRequest<Void>{
    private final String url = Config.getServerRoot() + "locations/{location_id}/carry_out_visits/{visit_id}.json";

    UUID locationId;
    UUID visitId;
    
    UpdateStatusRequest request;
    
	public UpdateCarryoutOrderStatusRequest(UUID locationId, UUID visitId, String orderStatus) {
		super(Void.class);
		this.locationId = locationId;
		this.visitId = visitId;
		
		request = new UpdateStatusRequest();
		request.order_status = orderStatus;
	}

	private class UpdateStatusRequest {
		@SuppressWarnings("unused")
		public String order_status;
	}
	
	@Override
	public Void loadDataFromNetwork()
			throws Exception {
    	getRestTemplate().put(url, request, locationId, visitId);
    	return null;
	}
}
