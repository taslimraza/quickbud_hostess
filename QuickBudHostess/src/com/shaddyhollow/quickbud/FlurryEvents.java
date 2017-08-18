package com.shaddyhollow.quickbud;

public enum FlurryEvents {
	HOME_DAILYRESET,
	HOME_LOGOUT,
	HOME_LOGIN,
	HOME_SERVERS,
	HOME_FLOORPLANS,
	HOME_SECTIONPLANS,
	HOME_SETTINGS,
	HOME_TABLEMANAGEMENT,
	HOME_CARRYOUTS,
	LOGIN_SUCCESS,
	LOGIN_FAIL,
	
	FLOORPLAN_ACTIVITY,
	FLOORPLAN_UPDATE,
	FLOORPLAN_DELETE,
	
	SECTIONPLAN_ACTIVITY,
	SECTIONPLAN_UPDATE,
	SECTIONPLAN_DELETE,
	
	SERVER_ACTIVITY,
	SERVER_CREATE,
	SERVER_UPDATE,
	SERVER_DELETE,

	SETTINGS_ACTIVITY,
	SETTINGS_BACKUP,
	SETTINGS_RESTORE,
	SETTINGS_DELETE,
	SETTINGS_UPDATECHECK,
	
	UPDATE_FOUND,
	UPDATE_ACCEPTED,
	UPDATE_DECLINED,
	
	HOSTESS_ACTIVITY,
	HOSTESS_DAILYRESET,
	HOSTESS_CHANGEPLAN,
	HOSTESS_MANAGESERVERS,
	
	QUEUE_ADDWALKIN,
	QUEUE_SELECT,
	
	PATRON_SEAT,
	PATRON_EDIT,
	PATRON_PAGE,
	PATRON_TEXT,
	PATRON_REMOVE,
	PATRON_PRINT,
	
	SECTION_OPEN,
	SECTION_CLOSE,
	SECTION_SERVERS,
	SECTION_COUNTRESET,
	
	SERVER_COUNTRESET, 
	
	TABLE_SEATANONYMOUS,
	TABLE_SEATFROMQUEUE,
	TABLE_HOLD,
	TABLE_REASSIGN,
	TABLE_COMBINE,
	TABLE_SPLIT,
	TABLE_CLOSE,
	TABLE_CLEAR,
	TABLE_RESEAT,
	TABLE_OPEN,
	
	VISIT_COMMENT,
	VISIT_MOVE,
	VISIT_RESIZE,
	VISIT_PRINT,
	
	CARRYOUT_PRINT,
	CARRYOUT_REMOVE,
	
	NETWORK_OFFLINE,
	NETWORK_ONLINE,
}