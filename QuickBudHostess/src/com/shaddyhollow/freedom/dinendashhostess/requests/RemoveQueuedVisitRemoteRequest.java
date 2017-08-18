package com.shaddyhollow.freedom.dinendashhostess.requests;

import java.util.UUID;

import com.shaddyhollow.quickbud.Config;
import com.shaddyhollow.quickbud.datastore.QueuedVisitLoader;
import com.shaddyhollow.quicktable.models.removeQueuedVisitResponse;
import com.shaddyhollow.robospice.BaseRequest;

public class RemoveQueuedVisitRemoteRequest extends BaseRequest<Void> {
//    private final String url =
//    Config.getServerRoot() + "locations/{location_id}/queued_visits/{queued_visit_id}.json";
	String url =
			Config.getServerRoot() + "qt/api/queuedvisit/clear/?tenant_id={tenant_id}&visit_id={queued_visit_it}";
	private UUID queuedVisitId;
    PatronRemovedCallback callback;
    private Integer tenantId;

	public RemoveQueuedVisitRemoteRequest(QueuedVisitLoader visitLoader, Integer tenantId, UUID queuedVisitId, PatronRemovedCallback callback) {
		super(Void.class);
    	this.queuedVisitId = queuedVisitId;
    	this.tenantId = tenantId;
    	this.callback = callback;
	}

	@Override
	public Void loadOnlineData() throws Exception {
	
    	getRestTemplate().getForObject(url, removeQueuedVisitResponse.class, tenantId, queuedVisitId);
    	return null;
	}
	
	public interface PatronRemovedCallback {
		public void patronRemoved(UUID queuedVisitID);
	}
}
