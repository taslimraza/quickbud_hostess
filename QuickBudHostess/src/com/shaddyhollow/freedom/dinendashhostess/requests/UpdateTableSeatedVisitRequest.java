package com.shaddyhollow.freedom.dinendashhostess.requests;

import java.util.UUID;

import com.shaddyhollow.quickbud.Config;
import com.shaddyhollow.quicktable.models.Table;
import com.shaddyhollow.robospice.BaseRequest;

public class UpdateTableSeatedVisitRequest extends BaseRequest<Void>{
    private final String url =
    		Config.getServerRoot() + "locations/{location_id}/tables/{table_id}.json";;

    private UUID location_id;
    private Table table;
    private TableVisit tableVisit;
    
	public UpdateTableSeatedVisitRequest(UUID location_id, Table table, int seated_visit_id) {
		super(Void.class);
        this.location_id = location_id;
        this.table = table;
        
        tableVisit = new TableVisit();
        tableVisit.seated_visit_id = seated_visit_id;
	}

    private class TableVisit {
   	 @SuppressWarnings("unused")
		public int seated_visit_id;
    }

	@Override
	public Void loadOnlineData() throws Exception {
		getRestTemplate().put(url, tableVisit, location_id, table.id);
		return null;
	}

}