package com.shaddyhollow.freedom.dinendashhostess.requests;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import android.annotation.SuppressLint;

import com.flurry.android.FlurryAgent;
import com.shaddyhollow.quickbud.Config;
import com.shaddyhollow.quickbud.FlurryEvents;
import com.shaddyhollow.quicktable.models.QueuedVisit;
import com.shaddyhollow.quicktable.models.QueuedVisitRequest;
import com.shaddyhollow.quicktable.models.QueuedVisitWithNestedAttributes;
import com.shaddyhollow.robospice.BaseRequest;

public class AddQueuedVisitRemoteRequest extends BaseRequest<QueuedVisit>{
//    private final String url = Config.getServerRoot() + "locations/{location_id}/queued_visits.json";
	private final String url = Config.getServerRoot() + "qt/api/dinein/";
	
    private QueuedVisitRequest request;
    private QueuedVisitWithNestedAttributes response;
    private QueuedVisit localPatron;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    
	public AddQueuedVisitRemoteRequest(QueuedVisitRequest request, QueuedVisit localPatron) {
		super(QueuedVisit.class);
   	 	FlurryAgent.logEvent(FlurryEvents.QUEUE_ADDWALKIN.name());
		this.request = request;
		this.localPatron = localPatron;
	}

	@Override
	public QueuedVisit loadOnlineData() throws Exception {
		String num = request.getPhone_number();
		request.setPhone_number("1"+request.getPhone_number());
		response = getRestTemplate().postForObject(url, request, QueuedVisitWithNestedAttributes.class/*, request.getLocation_id()*/);
		
		localPatron.setId(response.getID());
		localPatron.setVisit_id(response.getVisit_id());
		localPatron.setStatus(request.getStatus());
		localPatron.setParty_size(response.getParty_size());
		localPatron.setName(response.getName());
		if(response.getPhone_number().length()<=3){
			localPatron.setPhone_number(null);	
		}else {
			localPatron.setPhone_number(response.getPhone_number());	
		}
		localPatron.setLow_wait_time(String.valueOf(response.getLow_wait_time()));
		localPatron.setHigh_wait_time(String.valueOf(response.getHigh_wait_time()));
		localPatron.setOrder_in(false);
		localPatron.setWheel_chair_access(response.isWheel_chair_access());
		localPatron.setHigh_chairs(response.getHigh_chairs());
		localPatron.setBooster_seats(response.getBooster_seats());
		
//		SimpleDateFormat remoteFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
//		remoteFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
//		long remoteCreatedAt = remoteFormat.parse(response.getCreatedAt()).getTime();
//		remoteCreatedAt = remoteCreatedAt - Config.timeDiff;
//		localPatron.setCreated_at(dateFormat.format(remoteCreatedAt));
		localPatron.setCreated_at(response.getCreatedAt());
		
		localPatron.setSpecialRequests(request.getSpecialRequests());
//		localPatron.setRemoved();
		localPatron.setFromServer(0);
		localPatron.setWalkIn(response.getWalkIn());
		
    	return localPatron;
	}

}
