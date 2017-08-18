package com.shaddyhollow.freedom.dinendashhostess.requests;

import java.util.UUID;

import com.shaddyhollow.quickbud.datastore.QueuedVisitLoader;
import com.shaddyhollow.robospice.BaseRequest;

public class RemoveQueuedVisitLocalRequest extends BaseRequest<Void> {
    PatronRemovedCallback callback;
    private UUID queuedVisitId;
    private QueuedVisitLoader visitLoader;

	public RemoveQueuedVisitLocalRequest(QueuedVisitLoader visitLoader, Integer locationId, UUID queuedVisitId, PatronRemovedCallback callback) {
		super(Void.class);
		this.visitLoader = visitLoader;
    	this.queuedVisitId = queuedVisitId;
    	this.callback = callback;
	}

	@Override 
	public Void loadOfflineData() {
		try {
			visitLoader.markRemoved(queuedVisitId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(callback!=null) {
			callback.patronRemoved(queuedVisitId);
		}
		return null;
	}
	
	public interface PatronRemovedCallback {
		public void patronRemoved(UUID queuedVisitID);
	}
}
