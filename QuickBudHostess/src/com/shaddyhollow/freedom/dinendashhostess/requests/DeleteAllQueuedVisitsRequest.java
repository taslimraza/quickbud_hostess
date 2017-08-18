package com.shaddyhollow.freedom.dinendashhostess.requests;

import java.util.UUID;

import com.shaddyhollow.quickbud.Config;
import com.shaddyhollow.quickbud.datastore.QueuedVisitFactory;
import com.shaddyhollow.quicktable.models.removeQueuedVisitResponse;
import com.shaddyhollow.robospice.BaseRequest;

public class DeleteAllQueuedVisitsRequest extends BaseRequest<Void> {
	// private final String url = Config.getServerRoot() +
	// "locations/{location_id}/queued_visits/clear.json";
	private final String url = Config.getServerRoot() + "qt/api/queuedvisit/clear_all/?tenant_id={tenant_id}&location_id={location_id}";
	private Integer location_id;
	private Integer tenant_id;

	public DeleteAllQueuedVisitsRequest(Integer tenant_id, Integer location_id) {
		super(Void.class);
		this.tenant_id = tenant_id;
		this.location_id = location_id;
	}

	@Override
	public Void loadOnlineData() throws Exception {
		getRestTemplate().getForObject(url, removeQueuedVisitResponse[].class, tenant_id, location_id);
		return null;
	}

	@Override
	public Void loadOfflineData() {
		QueuedVisitFactory.getInstance().bulkDelete(null);
		return null;
	}
}
