package com.shaddyhollow.freedom.dinendashhostess.requests;

import java.util.UUID;

import android.annotation.SuppressLint;

import com.shaddyhollow.quickbud.Config;
import com.shaddyhollow.quickbud.datastore.QueuedVisitFactory;
import com.shaddyhollow.quickbud.datastore.QueuedVisitLoader;
import com.shaddyhollow.quicktable.models.QueuedVisit;
import com.shaddyhollow.quicktable.models.QueuedVisitRequest;
import com.shaddyhollow.quicktable.models.QueuedVisitWithNestedAttributes;
import com.shaddyhollow.robospice.BaseRequest;

public class UpdateQueuedVisitRequest extends BaseRequest<QueuedVisit> {
	// private final String url = Config.getServerRoot() +
	// "locations/{location_id}/queued_visits/{request_id}.json";
	private final String url = Config.getServerRoot() + "qt/api/dinein/modify/?id={request_id}";

	private QueuedVisit localVisit;
	private QueuedVisitRequest request;
	private QueuedVisitWithNestedAttributes response;
	private QueuedVisitLoader visitLoader;
	private UUID requestID = null;

	public UpdateQueuedVisitRequest(QueuedVisitLoader visitLoader,
			QueuedVisitRequest request, UUID requestID) {
		super(QueuedVisit.class);
		this.visitLoader = visitLoader;
		this.request = request;
		this.requestID = requestID;
	}

	@Override
	public QueuedVisit loadOnlineData() throws Exception {
		// TODO fix update so the order_in or visit doesn't get lost
		response = getRestTemplate().postForObject(url, request, 
				QueuedVisitWithNestedAttributes.class, requestID.toString());
		return localVisit;
	}

	@Override
	public QueuedVisit loadOfflineData() {
		localVisit = QueuedVisitFactory.getInstance().read(requestID);

		localVisit.setId(requestID);
		localVisit.setName(request.getName());
		localVisit.setParty_size(request.getParty_size());
		localVisit.setPhone_number(request.getPhone_number());
		localVisit.setStatus(request.getStatus());
		localVisit.setBooster_seats(request.getBooster_seats());
		localVisit.setHigh_chairs(request.getHigh_chairs());
		localVisit.setWheel_chair_access(request.isWheel_chair_access());
		localVisit.setSpecialRequests(request.getSpecialRequests());

		if (response != null) {
			localVisit.setLow_wait_time(String.valueOf(response.low_wait_time));
			localVisit
					.setHigh_wait_time(String.valueOf(response.low_wait_time));
			localVisit.setId(response.getID());
			localVisit.setVisit_id(response.getVisit_id());
		}
		visitLoader.update(localVisit);
		return localVisit;
	}

}
