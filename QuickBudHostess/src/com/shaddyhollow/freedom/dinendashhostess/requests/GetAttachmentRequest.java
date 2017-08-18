package com.shaddyhollow.freedom.dinendashhostess.requests;

import java.util.UUID;

import com.shaddyhollow.quickbud.Config;
import com.shaddyhollow.quicktable.models.Attachment;
import com.shaddyhollow.robospice.BaseRequest;

public class GetAttachmentRequest extends BaseRequest<Attachment>{
//    private final String url = Config.getServerRoot() + "locations/{location_id}/attachments/{key}.json";
	private final String url = 
			Config.getServerRoot() + "qt/api/location/{location_id}/attachments/?tenant_id={tenant_id}";
    private Integer locationId;
    private Integer tenantId;
    private String key;

	public GetAttachmentRequest(Integer tenantId, Integer locationId, String key) {
		super(Attachment.class);
		this.tenantId = tenantId;
		this.locationId = locationId;
		this.key = key;
	}

	@Override
	public Attachment loadOnlineData() throws Exception {
 		Attachment attachment = getRestTemplate().getForObject(url, Attachment.class, locationId, tenantId);
	    return attachment;
    }
}
