package com.shaddyhollow.quickbud;

import java.io.File;
import java.util.UUID;

import android.os.Environment;

import com.shaddyhollow.quicktable.models.LocationEntry;
/**
 * 
 * @author sashikolli
 *	Placeholder for misc global strings (and the current logged in location)
 *
 */
public class Config {
	public static String versionName;
	public static String packageName;

	private static boolean isOnline = true;
	private static boolean forceOffline;
	public static int defaultCarryoutTime = 15; //minutes
	public static boolean CARRYOUTONLY_ENABLED = Boolean.FALSE;
//	public static String serverRoot = "http://159.203.88.161/";
	public static String serverRoot = "http://qtloadbalancer-1162110760.us-west-2.elb.amazonaws.com/";
	public static String adminUrl = "http://52.25.3.27";
	public static String serverName;
	public static long timeDiff = 0;
	public static int carryoutPollingMS = 20000;
	public static int queuePollingMS = 20000;
	
	public static boolean isForceOffline() {
		return forceOffline;
	}
	
	public static boolean isOnline() {
		boolean online = false;
		if(!forceOffline) {
			online = isOnline;
		}
		return online;
	}
	
	public static void setOnline(boolean isOnline) {
		if(!forceOffline) {
			Config.isOnline = isOnline;
		}
	}
	
	public static void forceOffline(boolean forceOffline) {
		Config.forceOffline = forceOffline;
	}
	
	public static String getServerRoot() {
		return serverRoot;
	}
	
	public static String validateUsername(String username) {
		String updatedName = username;
		int atPos = username.indexOf('@');
		int dotPos = username.indexOf('.', atPos);
		
		int openBracket = username.indexOf("[");
		int closeBracket = username.indexOf("]");
		
		if(forceOffline) {
			serverName = "Offline";
			return username;
		}
		if(atPos!=-1 && dotPos!=-1) {
			String domain = username.substring(atPos+1, dotPos).toLowerCase();
			try {
				serverName = domain.substring(0, 1).toUpperCase() + domain.substring(1).toLowerCase();
			} catch (Exception e) {
				serverName = "Unknown";
			}
			if(domain.equals("shaddy")) {
				serverName = "Rocknes";
				serverRoot = "https://api1.quicktable.com/api/hostess/v2/";
			} else if(domain.equals("rocknes")) {
				serverRoot = "https://api1.quicktable.com/api/hostess/v2/";
			} else if(domain.equals("bobevans")) {
				serverRoot = "https://redrobin-api.quicktable.com/api/hostess/v2/";
			} else {
				serverRoot = "https://" + domain + "-api.quicktable.com/api/hostess/v2/";
			}
		}
		
		if(openBracket!=-1 && closeBracket!=-1) {
			String serverSpec = username.substring(openBracket+1, closeBracket);
			serverName = serverSpec;
			if(serverSpec.equalsIgnoreCase("staging")) {
				serverRoot = "http://dinendash-staging.herokuapp.com/api/hostess/v2/";
			} else {
				serverRoot = "http://" + serverSpec + "/api/hostess/v2/";
			}
			updatedName = updatedName.substring(0, openBracket);
		}
		
//		serverRoot = "http://qtloadbalancer-1162110760.us-west-2.elb.amazonaws.com/";
		serverRoot = "http://52.25.3.27/";
//		serverRoot = "http://159.203.88.161/";
//		serverRoot = "http://qtloadbalancer-v-1-1-56157812.us-west-2.elb.amazonaws.com/";
		
		return updatedName;
	}
	
	public static String getVersionURL() {
		if(serverName.equalsIgnoreCase("staging")) {
			return "https://www.dropbox.com/s/n8fp13996ieog5e/staging_version.txt?dl=1";
		} else {
			return "https://www.dropbox.com/s/f4yjzwqllzimqpt/areaeditor_version.txt?dl=1";
		}
	}
	
	public static String getAPKURL() {
		return "https://www.dropbox.com/s/3arfgtp6q64tuvm/AreaEditor.apk?dl=1";
	}
	
	public static String getStorageDir(String subdir) {
		String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "areaeditor";
		if(subdir!=null && subdir.length()>0) {
			dir += File.separator + subdir;
		}
		return dir;
	}
	
	public static int[] getSectionPalette() {
	    return new int[] { 
				    		0xFF00B0F0, 0xFF0FE16E, 0xFFE60A9D, 0xFFFF9933, 0xFFFE3802, 0xFFF4F92F,
				    		0xFF005776, 0xFF098943, 0xFF800657, 0xFFA85400, 0xFF9A0000, 0xFFC9C400,
				    		0xFF030DCD, 0xFF00FF00, 0xFFED059A, 0xFFFF3F00, 0xFFFF0000, 0xFFFFFF00
	    				 };
	}
	
	public final static int COLS = 32;
	public final static int ROWS = 20;
	
	//TODO this is not really a static item, but set at login.  Think about finding a new place for this
	public static LocationEntry location;
	
	public static Integer getTenantID() {
		Integer tenantId = null;
		if(location!=null) {
			tenantId = location.getTenantId();
		}
		return tenantId;
	}
	
	public static Integer getLocationID() {
		Integer locationID = null;
		if(location!=null) {
			locationID = location.getLocationId();
		}
		return locationID;
	}
	
	public static String getSessionKey() {
		String sessionKey = null;
		if(location!=null) {
			sessionKey = location.getSessionKeys();
		}
		return sessionKey;
	}
	
	public static String getCsrfToken() {
		String csrfToken = null;
		if(location!=null) {
			csrfToken = location.getCsrfToken();
		}
		return csrfToken;
	}
	
	public static String getUserId(){
		String userId = null;
		if(location != null){
			userId = location.getUserId();
		}
		return userId;
	}
	
	public static String getRestaurantName() {
		String restaurantName = null;
		if(location!=null) {
			restaurantName = location.getName();
		}
		return restaurantName;
	}
}
