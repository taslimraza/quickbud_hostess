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

import com.shaddyhollow.quickbud.Config;
import com.shaddyhollow.quickbud.datastore.CarryoutFactory;
import com.shaddyhollow.quickbud.datastore.CarryoutLoader;
import com.shaddyhollow.quicktable.models.CarryOutVisit;
import com.shaddyhollow.robospice.BaseRequest;

public class GetCarryoutVisitRequestRemote extends
		BaseRequest<ListCarryOutVisit> {
	// private final String url = Config.getServerRoot() +
	// "locations/{location_id}/carry_out_visits.json";
	// private final String url = "https://api.myjson.com/bins/3gurj";
	private final String url = Config.getServerRoot()
			+ "qb/api/carryout/order_detail/?tenant_id={tanant_id}&location_id={location_id}";

	@SuppressLint("SimpleDateFormat")
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	long timeDiff;
	private Integer locationId;
	private Integer tenantId;
	private CarryoutLoader loader;

	public GetCarryoutVisitRequestRemote(Integer tenantId, Integer locationId,
			CarryoutLoader loader) {
		super(ListCarryOutVisit.class);
		this.tenantId = tenantId;
		this.locationId = locationId;
		this.loader = loader;
	}

	@Override
	public ListCarryOutVisit loadOnlineData() throws Exception {
		List<CarryOutVisit> localVisitList = CarryoutFactory.getInstance()
				.bulkRead(null);
		ListCarryOutVisit newVisits = new ListCarryOutVisit();
		HashMap<UUID, CarryOutVisit> localVisits = new HashMap<UUID, CarryOutVisit>();
		int numLocalQueue = localVisitList.size();
		for (int i = 0; i < numLocalQueue; i++) {
			CarryOutVisit visit = localVisitList.get(i);
			localVisits.put(visit.getId(), visit);
		}

		HashMap<UUID, CarryOutVisit> remoteVisits = new HashMap<UUID, CarryOutVisit>();
		ResponseEntity<CarryOutVisit[]> response = getRestTemplate()
				.getForEntity(url, CarryOutVisit[].class, tenantId, locationId);
		// CarryOutVisit[] response = getRestTemplate().getForObject(url,
		// CarryOutVisit[].class);
		// if(response.getHeaders().containsKey("X-Server-Time")) {
		// long serverTime =
		// Long.parseLong(response.getHeaders().get("X-Server-Time").get(0)) *
		// 1000;
		// long localTime = System.currentTimeMillis();
		// timeDiff = serverTime - localTime;
		// }
		//
		CarryOutVisit[] serverVisits = response.getBody();
		// CarryOutVisit[] serverVisits = response;
		for (CarryOutVisit onlineVisit : serverVisits) {
			if (onlineVisit.getOrderStatus() != null) {
				remoteVisits.put(onlineVisit.getId(), onlineVisit);
			}
		}

		List<UUID> localOnly = new ArrayList<UUID>();
		List<UUID> remoteOnly = new ArrayList<UUID>();
		List<UUID> both = new ArrayList<UUID>();

		diffLists(localVisits.keySet(), remoteVisits.keySet(), localOnly,
				remoteOnly, both);
		for (UUID localID : localOnly) {
			// if visit is missing from remote, then the local copy should be
			// deleted
			loader.delete(localID);
		}
		for (UUID remoteID : remoteOnly) {
			CarryOutVisit newVisit = saveLocalVisit(remoteVisits.get(remoteID),
					null);
			newVisits.add(newVisit);
		}
		// re-save common list in case there are any changes (specifically
		// order_in)
		for (UUID bothID : both) {

			CarryOutVisit localVisit = localVisits.get(bothID);
			CarryOutVisit remoteVisit = remoteVisits.get(bothID);

			if (localVisits.get(bothID).getRemoved() == 0) {
				remoteVisit.setOrderStatus(localVisit.getOrderStatus());
				saveLocalVisit(remoteVisit, localVisit);
			}
			// CarryOutVisit localVisit = localVisits.get(bothID);
			// CarryOutVisit remoteVisit = remoteVisits.get(bothID);
			//
			// remoteVisit.setRemoved(localVisit.getRemoved());
			// remoteVisit.setOrderStatus(localVisit.getOrderStatus());
			//
			// saveLocalVisit(remoteVisit, localVisit);
		}
		return newVisits;
	}

	private void diffLists(Collection<UUID> local, Collection<UUID> remote,
			List<UUID> localOnly, List<UUID> remoteOnly, List<UUID> both) {
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
	private CarryOutVisit saveLocalVisit(CarryOutVisit request,
			CarryOutVisit originalLocalVisit) throws Exception {
		CarryOutVisit localVisit = new CarryOutVisit();

		localVisit.setId(request.getId());
		localVisit.setVisit_id(request.getVisit_id());

		// SimpleDateFormat remoteFormat = new
		// SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		// remoteFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		// long remoteCreatedAt =
		// remoteFormat.parse(request.getCreated_at()).getTime();
		// remoteCreatedAt = remoteCreatedAt - timeDiff;
		// localVisit.setCreated_at(dateFormat.format(remoteCreatedAt));
		//
		// long remoteOrderTime =
		// remoteFormat.parse(request.getOrder_time()).getTime();
		// remoteOrderTime = remoteOrderTime - timeDiff;
		// localVisit.setOrder_time(dateFormat.format(remoteOrderTime));

		if (request.getCart_items() != null) {
			if (request.getName() != null) {
				localVisit.setName(request.getName());
			} else {
				localVisit.setName("Carryout Guest");
			}
			String phoneNum = request.getPhone_number();
			String phone = phoneNum.substring(phoneNum.length() - 10);
			localVisit.setPhone_number("(" + phone.substring(0, 3) + ") " + phone.substring(3, 6) + "-" + phone.substring(6));
			localVisit.setOrderStatus(request.getOrderStatus());

			if (request.getPatronStatus().equalsIgnoreCase("E")) {
				localVisit.setPatronStatus("EN ROUTE");
			} else if (request.getPatronStatus().equalsIgnoreCase("A")) {
				localVisit.setPatronStatus("ARRIVED");
			}
			// localVisit.setPatronStatus(request.getPatronStatus());
			localVisit.setSpecial_requests(request.getSpecial_requests());
			localVisit.setCart_items(request.getCart_items());
			localVisit.setOrder_time(request.getOrder_time());
			localVisit.setAddressLine(request.getAddressLine());
			localVisit.setCity(request.getCity());
			localVisit.setState(request.getState());
			localVisit.setZip(request.getZip());
		}

		if (originalLocalVisit == null) {
			if (request.getpickUp()){
				localVisit.setOrderStatus("Pick Up");	
			}else if (request.getDelivery()){
				localVisit.setOrderStatus("Delivery");
			}
			loader.create(localVisit);
		} else {
//			localVisit.setPrintFailures(originalLocalVisit.getPrintFailures());
			loader.update(localVisit);
		}

		return localVisit;
	}

	@Override
	public boolean isOfflineAvailable() {
		return false;
	}
}
