package com.shaddyhollow.freedom.dinendashhostess.requests;

import java.util.UUID;

import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.shaddyhollow.quickbud.Config;
import com.shaddyhollow.quicktable.models.Server;
import com.shaddyhollow.robospice.BaseRequest;

public class UpdateSectionServerRequest extends BaseRequest<Void>{
    private final String url =
    Config.getServerRoot() + "locations/{location_id}/sections/{section_id}.json";

	 private UUID location_id;
	 private UUID section_id;
	 private SectionServer sectionServer;
    
	public UpdateSectionServerRequest(UUID location_id, UUID section_id, Server server) {
		super(Void.class);
	    this.location_id = location_id;
	    this.section_id = section_id;
	    
	    sectionServer = new SectionServer();
	    sectionServer.server_id = server != null ? server.id : null;
	}

    private class SectionServer {
   	 @SuppressWarnings("unused")
		public UUID server_id;
    }

	@Override
	public Void loadOnlineData() throws Exception {
    	RestTemplate restTemplate = new RestTemplate();
    	restTemplate.getMessageConverters().add(new GsonHttpMessageConverter(true));
		restTemplate.put(url, sectionServer, location_id, section_id);

		return null;  
	}

}
