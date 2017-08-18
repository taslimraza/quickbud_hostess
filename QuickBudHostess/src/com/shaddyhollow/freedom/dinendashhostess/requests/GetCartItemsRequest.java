package com.shaddyhollow.freedom.dinendashhostess.requests;

import java.util.UUID;

import com.shaddyhollow.quickbud.Config;
import com.shaddyhollow.quicktable.models.CartItems;
import com.shaddyhollow.robospice.BaseRequest;

public class GetCartItemsRequest extends BaseRequest<CartItems>{
//    private final String url =
//    Config.getServerRoot() + "locations/{location_id}/visits/{visit_id}/cart_items.json";
	private final String url =
			Config.getServerRoot() + "qb/api/print/visits/?tenant_id={tenant_id}&location_id={location_id}&visit_id={visit_id}";
	
    private Integer locationId;
    private Integer tenantId;
    private UUID visitId;

	public GetCartItemsRequest(Integer tenantId, Integer locationId, UUID visitId) {
		super(CartItems.class);
		this.tenantId = tenantId;
		this.locationId = locationId;
		this.visitId = visitId;
	}

	@Override
	public CartItems loadOnlineData() throws Exception {
		return getRestTemplate().getForObject(url, CartItems.class, tenantId, locationId, visitId);
	}

}
