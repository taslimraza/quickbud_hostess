package com.shaddyhollow.freedom.dinendashhostess.requests;

import java.util.UUID;

import com.shaddyhollow.quickbud.Config;
import com.shaddyhollow.quicktable.models.Table;
import com.shaddyhollow.robospice.BaseRequest;

public class AddTableRequest extends BaseRequest<Void>{
	private final String url =
	        Config.getServerRoot() + "locations/{location_id}/tables.json";
    private UUID location_id;
    private String table_name;
    private int table_seats;
    private String table_type;
    
    public AddTableRequest(UUID location_id, Table table) {
       super(Void.class);
       this.location_id = location_id;
       this.table_name = table.name;
       this.table_seats = table.seats;
       this.table_type = table.table_type;
    }
    
	@Override
	public Void loadOnlineData() throws Exception {
		getRestTemplate().put(url, null, location_id, table_name, table_seats, table_type); 
		return null;
	}
}
