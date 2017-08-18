package com.shaddyhollow.freedom.dinendashhostess.printer;

import java.util.ArrayList;

import com.shaddyhollow.quickbud.datastore.ServerFactory;
import com.shaddyhollow.quicktable.models.CartItems;
import com.shaddyhollow.quicktable.models.Server;
import com.shaddyhollow.quicktable.models.Table;
import com.shaddyhollow.util.DateUtils;

public class DineInReceipt extends Receipt {
	private Table table;
	private CartItems cartItems;

	public DineInReceipt(Table table, CartItems cartItems) {
		super(cartItems.cart_items);
		this.table = table;
		this.cartItems = cartItems;
	}

	private void addHeader() {
		enableUnderlinePrinting();
		centerAlignPrinting();
		addString("Dine In");
		leftAlignPrinting();
		disableUnderlinePrinting();
		addString("\n\n");

	}
	
	public ArrayList<Byte> getPrintList() {
		list.clear();
		
		init();
		addHeader();
        addDateStamp();
        
        enableEmphasizedPrinting();
    	if(table!=null) {
	        try {
	    		addLine("Time Seated", DateUtils.getTime(table.seated_visit.seating_time));
	        } catch (Exception e) {
	        }
	        addLine("Table #", 2, table.name);
	        try {
		        Server server = ServerFactory.getInstance().read(table.seated_visit.server_id);
		        addLine("Server", 2, server.getName());
	        } catch (Exception e) {
	        }
	        addLine("Time Seated", DateUtils.getTime(table.seated_visit.seating_time));
    	}
        disableEmphasizedPrinting();
        addString("\n\n");
        addCartItems();
        
        addString("\n");
        String couponCode = cartItems.coupon_code;
        
        if (couponCode != null && couponCode.length() > 0) {
        	couponCode = couponCode.toUpperCase();
            addLine("Coupon Code ", couponCode);
            addString("\n");
        }
        
        String specialRequest = cartItems.special_requests;
        
        if (specialRequest != null && specialRequest.length() > 0) {
            addLine("Special Request ", specialRequest);
            addString("\n\n");
        }

        addEndOfReceipt();

		return list;
	}
	



}
