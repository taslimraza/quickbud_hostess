package com.shaddyhollow.freedom.dinendashhostess.requests;

import java.util.UUID;

import org.springframework.http.ResponseEntity;

import com.shaddyhollow.quickbud.Config;
import com.shaddyhollow.quicktable.models.HostessLogin;
import com.shaddyhollow.quicktable.models.LocationEntry;
import com.shaddyhollow.robospice.BaseRequest;

public class LoginHostessRequest extends BaseRequest<LocationEntry> {
//	private final String hostessSignInUrl =
//    		Config.getServerRoot() + "hostesses/signin.json";
	private final String hostessSignInUrl =
			Config.getServerRoot() + "qb/api/login/";
	
	private String email;
	private String password;
	private String serialId;
	private boolean offline;
	
	public LoginHostessRequest(String email, String password, boolean offline, String serialId) {
		super(LocationEntry.class);
		this.email = email;
		this.password = password;
		this.serialId = serialId;
		this.offline = offline;
	}

	@Override
	public LocationEntry loadOnlineData() throws Exception {
    	HostessLogin login = new HostessLogin();
    	login.setEmail(email);
    	login.setPassword(password);
//    	login.setSerailId(serialId);

//    	LocationEntry location = getRestTemplate().postForObject(hostessSignInUrl, login, LocationEntry.class);
    	ResponseEntity<LocationEntry> response = getRestTemplate().postForEntity(hostessSignInUrl, login, LocationEntry.class);
//    	
    	response.getHeaders();
    	return response.getBody();	
//    	return location;
	}
	
	@Override
	public LocationEntry loadOfflineData() {
		LocationEntry location = null; 
		if(offline) {
			location = new LocationEntry();
			location.setLocationId(0);
			location.setName("Offline");
		}
		return location;
	}
	
}
