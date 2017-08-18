package com.shaddyhollow.freedom.dinendashhostess.requests;

import java.util.UUID;

import com.shaddyhollow.quickbud.Config;
import com.shaddyhollow.quicktable.models.Table;
import com.shaddyhollow.robospice.BaseRequest;

public class UpdateTableSectionRequest extends BaseRequest<Void>{
    private final String url =
    		Config.getServerRoot() + "locations/{location_id}/tables/{table_id}.json";;

    private UUID location_id;
    private Table table;
    private TableSection tableSection;
    
	public UpdateTableSectionRequest(UUID location_id, Table table, int section_id) {
		super(Void.class);
        this.location_id = location_id;
        this.table = table;
        
        tableSection = new TableSection();
        tableSection.section_id = section_id;
	}

    private class TableSection {
   	 @SuppressWarnings("unused")
		public int section_id;
    }

	@Override
	public Void loadOnlineData() throws Exception {
		getRestTemplate().put(url, tableSection, location_id, table.id);
		return null;
	}

}
