package com.shaddyhollow.freedom.dinendashhostess.requests;

import java.util.UUID;

import com.shaddyhollow.quickbud.Config;
import com.shaddyhollow.quicktable.models.Table;
import com.shaddyhollow.robospice.BaseRequest;

public class UpdateVisitPartySizeRequest extends BaseRequest<Void>{
    private final String url =
    		Config.getServerRoot() + "locations/{location_id}/visits/{visit_id}.json";

    private UUID location_id;
    private Table table;
    private SeatedVisitPartySize seatedVisitPartySize;
    
	public UpdateVisitPartySizeRequest(UUID location_id, Table table, int party_size) {
		super(Void.class);
        this.location_id = location_id;
        this.table = table;
        
        seatedVisitPartySize = new SeatedVisitPartySize();
        seatedVisitPartySize.party_size = party_size;
	}

    private class SeatedVisitPartySize {
   	 @SuppressWarnings("unused")
		public int party_size;
    }

	@Override
	public Void loadOnlineData() throws Exception {
		getRestTemplate().put(url, seatedVisitPartySize, location_id, table.seated_visit.id);
		return null;
	}

}
