package com.shaddyhollow.freedom.dinendashhostess.requests;

import java.util.UUID;

import com.shaddyhollow.quickbud.Config;
import com.shaddyhollow.quicktable.models.Table;
import com.shaddyhollow.robospice.BaseRequest;

public class ClearTableRequest extends BaseRequest<Void>{
    private final String url =
    		Config.getServerRoot() + "locations/{location_id}/tables/{table_id}/clear.json";

    private UUID locationId;
    private Table table;
    
	public ClearTableRequest(UUID locationId, Table table) {
		super(Void.class);
		this.table = table;
    	this.locationId = locationId;
    }
	
	@Override
	public Void loadOnlineData() throws Exception {
		getRestTemplate().put(url, null, locationId, table.id);
		return null;
	}
}
