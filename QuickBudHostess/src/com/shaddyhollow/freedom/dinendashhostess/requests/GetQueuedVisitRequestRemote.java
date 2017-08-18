package com.shaddyhollow.freedom.dinendashhostess.requests;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import org.springframework.http.ResponseEntity;

import android.annotation.SuppressLint;
import android.util.Log;

import com.shaddyhollow.freedom.hostess.QueuedPatronsAdapter;
import com.shaddyhollow.quickbud.Config;
import com.shaddyhollow.quickbud.datastore.QueuedVisitFactory;
import com.shaddyhollow.quickbud.datastore.QueuedVisitLoader;
import com.shaddyhollow.quicktable.models.QueuedVisit;
import com.shaddyhollow.quicktable.models.QueuedVisitWithNestedAttributes;
import com.shaddyhollow.robospice.BaseRequest;
import com.shaddyhollow.util.FileOperations;

public class GetQueuedVisitRequestRemote extends BaseRequest<Boolean> {
//    private final String url = Config.getServerRoot() + "locations/{location_id}/queued_visits.json";
	private final String url = Config.getServerRoot() + "qt/api/queuedvisit/queued_visits/?tenant_id={tenant_id}&location_id={location_id}";
	@SuppressLint("SimpleDateFormat")
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	private Integer locationId;
	private Integer tenantId;
	private QueuedVisitLoader loader;
	
	public GetQueuedVisitRequestRemote(Integer tenantId, Integer locationId, QueuedPatronsAdapter adapter, QueuedVisitLoader loader) {
		super(Boolean.class);
		this.tenantId = tenantId;
		this.locationId = locationId;
		this.loader = loader;
	}
	
	@Override
	public Boolean loadOnlineData() throws Exception {
		Boolean modified = false;
		List<QueuedVisit> localVisitList = QueuedVisitFactory.getInstance().bulkRead(null);
		HashMap<UUID, QueuedVisit> localVisits = new HashMap<UUID, QueuedVisit>();
		int numLocalQueue = localVisitList.size();
		for(int i=0;i<numLocalQueue;i++) {
			QueuedVisit visit = localVisitList.get(i);
			localVisits.put(visit.getId(), visit);
			Log.i("Local Visit", visit.getName());
		}
		
		HashMap<UUID, QueuedVisit> remoteVisits = new HashMap<UUID, QueuedVisit>();
		ResponseEntity<QueuedVisit[]> response = getRestTemplate().getForEntity(url, QueuedVisit[].class, tenantId, locationId);
//		QueuedVisit[] response = getRestTemplate().getForObject(url, QueuedVisit[].class);
//		if(response.getHeaders().containsKey("X-Server-Time")) {
//			long serverTime = Long.parseLong(response.getHeaders().get("X-Server-Time").get(0)) * 1000;
//			long localTime = System.currentTimeMillis();
//			Config.timeDiff = serverTime - localTime; 
//		
////			System.out.println(timeDiff);
//		}
//		
		QueuedVisit[] serverVisits = response.getBody();
//		QueuedVisit[] serverVisits = response;
		for(QueuedVisit onlineVisit : serverVisits) {
			remoteVisits.put(onlineVisit.getId(), onlineVisit);
			Log.i("Remote Visit", onlineVisit.getName());
 		}
		
		//--------------------LOG DATA SAVING TO FILE -------------------------
		FileOperations.writeToFile(serverVisits);

		List<UUID> localOnly = new ArrayList<UUID>();
		List<UUID> remoteOnly = new ArrayList<UUID>();
		List<UUID> both = new ArrayList<UUID>();
		
		diffLists(localVisits.keySet(), remoteVisits.keySet(), localOnly, remoteOnly, both);
		for(UUID localID : localOnly) {
			QueuedVisit visit = localVisits.get(localID);
			//if fromserver==1 then delete local
			if(visit.getFromServer()==1) {
				loader.delete(localID);
			}
			//if fromserver==0 then create remote
		}
		for(UUID remoteID : remoteOnly) {
			saveLocalVisit(remoteVisits.get(remoteID), true);
		}
		// resave common list in case there are any changes (specifically order_in)
		for(UUID bothID : both) {
			if(localVisits.get(bothID).getRemoved()==0) {
				saveLocalVisit(remoteVisits.get(bothID), false);
			}
		}
	    return modified;
	}

	@Override
	public boolean isOfflineAvailable() {
	    return false;  
	}
	
	private void diffLists(Collection<UUID> local, Collection<UUID> remote, List<UUID> localOnly, List<UUID> remoteOnly, List<UUID> both) {
		localOnly.clear();
		localOnly.addAll(local);
		localOnly.removeAll(remote);
		
		remoteOnly.clear();
		remoteOnly.addAll(remote);
		remoteOnly.removeAll(local);
		
		both.clear();
		both.addAll(local);
		both.retainAll(remote);
		
	}
	
	@SuppressLint("SimpleDateFormat")
	private QueuedVisitWithNestedAttributes saveLocalVisit(QueuedVisit request, boolean isnew) throws Exception {
		QueuedVisitWithNestedAttributes result = null;
		QueuedVisit localVisit = new QueuedVisit();
		
		localVisit.setId(request.getId());
		localVisit.setFromServer(1);
		localVisit.setVisit_id(request.getVisit_id());

//		SimpleDateFormat remoteFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
//		remoteFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
//		long remoteCreatedAt = remoteFormat.parse(request.getCreated_at()).getTime();
//		remoteCreatedAt = remoteCreatedAt - Config.timeDiff;
//		localVisit.setCreated_at(dateFormat.format(remoteCreatedAt));

		localVisit.setCreated_at(request.getCreated_at());
		
		localVisit.setName(request.getName());
		localVisit.setParty_size(request.getParty_size());
		
		if(request.getPhone_number().length()<=3){
			localVisit.setPhone_number(null);	
		}else {
			localVisit.setPhone_number(request.getPhone_number());	
		}
	
		localVisit.setStatus(request.getStatus());
		localVisit.setBooster_seats(request.getBooster_seats());
		localVisit.setHigh_chairs(request.getHigh_chairs());	
		localVisit.setWheel_chair_access(request.isWheel_chair_access());
		localVisit.setSpecialRequests(request.getSpecialRequests());
		localVisit.setOrder_in(request.isOrder_in());
		localVisit.setLow_wait_time(request.getLow_wait_time());
		localVisit.setHigh_wait_time(request.getHigh_wait_time());
		localVisit.setWalkIn(request.getWalkIn());
		
		if(isnew) {
			loader.create(localVisit);
		} else {
			loader.update(localVisit);
		}
		
		return result;
	}
}
