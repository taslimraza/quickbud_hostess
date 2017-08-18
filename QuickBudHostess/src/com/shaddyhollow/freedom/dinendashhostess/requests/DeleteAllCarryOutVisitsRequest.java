package com.shaddyhollow.freedom.dinendashhostess.requests;

import java.util.UUID;

import com.shaddyhollow.quickbud.Config;
import com.shaddyhollow.quickbud.datastore.CarryoutFactory;
import com.shaddyhollow.quicktable.models.removeQueuedVisitResponse;
import com.shaddyhollow.robospice.BaseRequest;

public class DeleteAllCarryOutVisitsRequest extends BaseRequest<Void>{
//	private final String url = Config.getServerRoot() + "locations/{location_id}/carry_out_visits/clear.json";
	private final String url = 
			Config.getServerRoot() + "qb/api/carryout/clear_all/?tenant_id={tenant_id}&location_id={location_id}";
	private Integer location_id;
	private Integer tenantId;
    
    public DeleteAllCarryOutVisitsRequest(Integer tenantId, Integer location_id) {
       super(Void.class);
       this.tenantId = tenantId; 
       this.location_id = location_id;
    }
    
	@Override
	public Void loadOnlineData() throws Exception {
		getRestTemplate().getForObject(url, removeQueuedVisitResponse[].class, tenantId, location_id);
		return null;
	}
	
	@Override 
	public Void loadOfflineData() {
		CarryoutFactory.getInstance().bulkDelete(null);
		return null;
	}
}
