package com.shaddyhollow.quicktable.models;

import java.util.UUID;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class CarryOutVisit implements Identifiable {
	private UUID id;
	private UUID visit_id;
//	private Integer id;
//	private Integer visit_id;
	private String order_status;
	private String patron_status;
	private String phone_number;
	private String name;
	private String created_at;
	private String special_requests;
	private CartItem[] cart_items;
	private int removed;
	private int printFailures;
	private String order_time;
	private String tenant_id;
	private String coupon_code;
	private boolean pickup;
	private boolean delivery;
	private String address_line;
	private String city;
	private String state;
	private String zip;
	
	@Override
	public UUID getId() {
		return id;
	}
	@Override
	public String getName() {
		return name;
	}
	@Override
	public void setId(UUID ID) {
		this.id = ID;		
	}
	@Override
	public void setName(String name) {
		if(name==null) {
			name = "Guest";
		}
		name = name.trim();
		if(name.length()==0) {
			name = "Guest";
		}
		String[] parts = name.split(" ");
		if(parts.length>0) {
			this.name = parts[0];
			if(parts.length>1 && parts[1].length()>0) {
				this.name = this.name + " " + parts[1].substring(0, 1) + ".";
			}
		}
	}
	public String getPhone_number() {
		return phone_number;
	}
	public void setPhone_number(String phone_number) {
		this.phone_number = phone_number;
	}
	public UUID getVisit_id() {
		return visit_id;
	}
	public void setVisit_id(UUID visit_id) {
		this.visit_id = visit_id;
	}
	public String getOrderStatus() {
		return order_status;
	}
	public void setOrderStatus(String order_status) {
		this.order_status = order_status;
	}
	public String getPatronStatus() {
		return patron_status;
	}
	public void setPatronStatus(String patron_status) {
		this.patron_status = patron_status;
	}
	public String getCreated_at() {
		return created_at;
	}
	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}
	public String getSpecial_requests() {
		return special_requests;
	}
	public void setSpecial_requests(String special_requests) {
		this.special_requests = special_requests;
	}
	public CartItem[] getCart_items() {
		return cart_items;
	}
	public void setCart_items(CartItem[] cart_items) {
		this.cart_items = cart_items;
	}
	
	public void setCartItemsFromString(String cart_items) {
		setCart_items(decodeCartItems(cart_items));
	}
	
	public String getCartItemsString() {
		return encodeCartItems(getCart_items());
	}
	
	public int getRemoved() {
		return removed;
	}
	public void setRemoved(int removed) {
		this.removed = removed;
	}
	
	private CartItem[] decodeCartItems(String cartItemsStructure) {
		CartItem[] cartItems = null;

		if(cartItemsStructure!=null && cartItemsStructure.length()>0) {
			try {
		        GsonBuilder gsonBuilder = new GsonBuilder();
				Gson gson = gsonBuilder.create();
				cartItems = gson.fromJson(cartItemsStructure, CartItem[].class);
			} catch (Exception e) {
				Log.w("CarryOutVisit", "problem with cart items decoding", e);
			}
		}
		return cartItems;
	}
	
	private String encodeCartItems(CartItem[] cartItems) {
		if(cartItems==null || cartItems.length==0) {
			return null;
		}
		Gson gson = new GsonBuilder().create();
		String jsonvalue = gson.toJson(cartItems);
	
		return jsonvalue;
	}
	public int getPrintFailures() {
		return printFailures;
	}
	public void setPrintFailures(int printFailures) {
		this.printFailures = printFailures;
	}
	public String getOrder_time() {
		return order_time;
	}
	public void setOrder_time(String order_time) {
		this.order_time = order_time;
	}
	public String getTenant_id() {
		return tenant_id;
	}
	public void setTenant_id(String tenant_id) {
		this.tenant_id = tenant_id;
	}
	public String getCoupon_code() {
		return coupon_code;
	}
	public void setCoupon_code(String coupon_code) {
		this.coupon_code = coupon_code;
	}
	
	public boolean getpickUp() {
		return pickup;
	}
	public void setPickUp(boolean pickup) {
		this.pickup = pickup;
	}
	
	public boolean getDelivery() {
		return delivery;
	}
	public void setDelivery(boolean delivery) {
		this.delivery = delivery;
	}
	
	public String getAddressLine() {
		return address_line;
	}
	public void setAddressLine(String address_line) {
		this.address_line = address_line;
	}
	
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}

}
