package com.shaddyhollow.freedom.dinendashhostess.requests;

import java.util.UUID;

import com.shaddyhollow.quickbud.Config;
import com.shaddyhollow.quicktable.models.Table;
import com.shaddyhollow.robospice.BaseRequest;

public class AddAnonymousSeatedVisitRequest extends BaseRequest<Void>{
	private final String url =
	        Config.getServerRoot() + "locations/{location_id}/tables/{table_id}/seat.json?party_size={party_size}";
    private UUID location_id;
    private int party_size;
    private Table table;
    
    public AddAnonymousSeatedVisitRequest(UUID location_id, Table table, int party_size) {
       super(Void.class);
       this.location_id = location_id;
       this.table = table;
       this.party_size = party_size;
    }
    
	@Override
	public Void loadOnlineData() throws Exception {
		getRestTemplate().put(url, null, location_id, table.id, party_size); 
		return null;
	}
}
