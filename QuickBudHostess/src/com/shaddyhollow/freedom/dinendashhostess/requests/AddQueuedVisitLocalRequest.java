package com.shaddyhollow.freedom.dinendashhostess.requests;

import java.util.Date;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import android.annotation.SuppressLint;

import com.flurry.android.FlurryAgent;
import com.shaddyhollow.quickbud.FlurryEvents;
import com.shaddyhollow.quickbud.datastore.QueuedVisitLoader;
import com.shaddyhollow.quicktable.models.QueuedVisit;
import com.shaddyhollow.quicktable.models.QueuedVisitRequest;
import com.shaddyhollow.robospice.BaseRequest;

public class AddQueuedVisitLocalRequest extends BaseRequest<QueuedVisit>{

    private QueuedVisitRequest request;
    private QueuedVisitLoader visitLoader;
    private int size;
    private boolean fromServer;
	public QueuedVisit localVisit = null;

    
	public AddQueuedVisitLocalRequest(QueuedVisitLoader visitLoader, QueuedVisitRequest request, int size, boolean fromServer) {
		super(QueuedVisit.class);
   	 	FlurryAgent.logEvent(FlurryEvents.QUEUE_ADDWALKIN.name());
		this.visitLoader = visitLoader;
		this.request = request;
		this.size = size;
		this.fromServer = fromServer;
	}

	@Override
	public QueuedVisit loadOfflineData() {
		QueuedVisit visit = null;
		try {
			visit = saveLocalVisit();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return visit;
	}

	private QueuedVisit saveLocalVisit() throws Exception {
		localVisit = new QueuedVisit();
		
		int lowWaitTime = (size+2)/3 * 5;
		localVisit.setLow_wait_time(String.valueOf(lowWaitTime));
		localVisit.setHigh_wait_time(String.valueOf(lowWaitTime+5));
		
		localVisit.setFromServer(fromServer ? 1 : 0);

		localVisit.setId(request.getId());
		localVisit.setVisit_id(null);

		DateTimeFormatter dtfOut = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
		localVisit.setCreated_at(dtfOut.print(new Date().getTime()));

		localVisit.setName(request.getName());
		localVisit.setParty_size(request.getParty_size());
		localVisit.setPhone_number(request.getPhone_number());
		localVisit.setStatus(request.getStatus());
		localVisit.setBooster_seats(request.getBooster_seats());
		localVisit.setHigh_chairs(request.getHigh_chairs());	
		localVisit.setWheel_chair_access(request.isWheel_chair_access());
		localVisit.setSpecialRequests(request.getSpecialRequests());
		
		visitLoader.create(localVisit);
		
		return localVisit;
	}

}
