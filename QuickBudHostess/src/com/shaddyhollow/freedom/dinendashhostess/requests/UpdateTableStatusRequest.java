package com.shaddyhollow.freedom.dinendashhostess.requests;

import java.util.UUID;

import com.shaddyhollow.quickbud.Config;
import com.shaddyhollow.quicktable.models.Table;
import com.shaddyhollow.robospice.BaseRequest;

public class UpdateTableStatusRequest extends BaseRequest<Void>{
    private final String url =
    		Config.getServerRoot() + "locations/{location_id}/tables/{table_id}.json";;

    private UUID location_id;
    private Table table;
    private TableStatus tableStatus;
    
	public UpdateTableStatusRequest(UUID location_id, Table table) {
		super(Void.class);
        this.location_id = location_id;
        this.table = table;
        
        tableStatus = new TableStatus();
        tableStatus.status = table.status;
	}

    private class TableStatus {
   	 @SuppressWarnings("unused")
		public String status;
    }

	@Override
	public Void loadOnlineData() throws Exception {
		getRestTemplate().put(url, tableStatus, location_id, table.id);
		return null;
	}

}