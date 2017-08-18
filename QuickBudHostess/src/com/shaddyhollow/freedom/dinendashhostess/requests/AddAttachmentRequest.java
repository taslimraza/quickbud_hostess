package com.shaddyhollow.freedom.dinendashhostess.requests;

import java.util.UUID;

import android.util.Log;

import com.flurry.android.FlurryAgent;
import com.shaddyhollow.quickbud.Config;
import com.shaddyhollow.quickbud.FlurryEvents;
import com.shaddyhollow.quicktable.models.Attachment;
import com.shaddyhollow.robospice.BaseRequest;

public class AddAttachmentRequest extends BaseRequest<Void>{
//	private final String url = Config.getServerRoot() + "locations/{location_id}/attachments/{key}";
	private final String url = Config.getServerRoot() + "qt/api/location/{location_id}/attachments/?tenant_id={tenant_id}";
	private Integer location_id;
	private Integer tenantId;
    private Attachment attachment;
    
    public AddAttachmentRequest(Integer tenantId, Integer location_id, String key, String value) {
       super(Void.class);
       this.tenantId = tenantId;
       this.location_id = location_id;
       this.attachment = new Attachment();
       this.attachment.setKey(key);
       this.attachment.setValue(value);
       Log.i("value",value);
   	   FlurryAgent.logEvent(FlurryEvents.SERVER_CREATE.name());
    }
    
	@Override
	public Void loadOnlineData() throws Exception {
		getRestTemplate().postForObject(url, attachment, Attachment.class, location_id, tenantId);
		return null;
	}

}
