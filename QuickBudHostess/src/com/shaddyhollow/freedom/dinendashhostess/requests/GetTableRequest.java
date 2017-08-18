package com.shaddyhollow.freedom.dinendashhostess.requests;

import java.util.UUID;

import com.shaddyhollow.quickbud.Config;
import com.shaddyhollow.quicktable.models.Table;
import com.shaddyhollow.robospice.BaseRequest;

public class GetTableRequest extends BaseRequest<Table>{
    private final String url =
    Config.getServerRoot() + "locations/{location_id}/tables/{table_id}.json";

    private UUID locationId;
    private UUID tableId;

	public GetTableRequest(UUID locationId, UUID tableId) {
		super(Table.class);
		this.locationId = locationId;
		this.tableId = tableId;
	}

	@Override
	public Table loadOnlineData() throws Exception {
 		return getRestTemplate().getForObject(url, Table.class, locationId, tableId);
	}

}
