package com.shaddyhollow.freedom.dinendashhostess.requests;

import java.util.UUID;

import com.shaddyhollow.quickbud.Config;
import com.shaddyhollow.quickbud.datastore.CarryoutLoader;
import com.shaddyhollow.quicktable.models.removeQueuedVisitResponse;
import com.shaddyhollow.robospice.BaseRequest;

public class DeleteCarryOutVisitRequest extends BaseRequest<Void>{
//    private final String url = Config.getServerRoot() + "locations/{location_id}/carry_out_visits/{carry_out_visit_id}.json";
	private final String url = 
			Config.getServerRoot() + "qb/api/carryout/clear/?tenant_id={tenant_id}&visit_id={carry_out_visit_id}";
	private CarryoutLoader visitLoader;
    private UUID carryOutVisitId;
    private Integer tenantId;
    
	public DeleteCarryOutVisitRequest(CarryoutLoader visitLoader, Integer tenantId, UUID carryOutVisitId) {
		super(Void.class);
		this.visitLoader = visitLoader;
    	this.carryOutVisitId = carryOutVisitId;
    	this.tenantId = tenantId;
    }
	
	@Override
	public Void loadOnlineData() throws Exception {
		getRestTemplate().getForObject(url, removeQueuedVisitResponse.class, tenantId, carryOutVisitId); 
		return null;
	}

	@Override
	public Void loadOfflineData() {
		try {
			visitLoader.markRemoved(carryOutVisitId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
