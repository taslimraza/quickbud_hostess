package com.shaddyhollow.freedom.dinendashhostess.printer;

import java.util.ArrayList;

import com.shaddyhollow.quickbud.Config;
import com.shaddyhollow.quicktable.models.CarryOutVisit;
import com.shaddyhollow.util.DateUtils;

public class CarryOutReceipt extends Receipt {
	private CarryOutVisit visit;
	private int requestCounter;
	private String couponCode;
	private String specialRequest;
	
	public CarryOutReceipt(CarryOutVisit visit, int requestCounter, String couponCode, String specialRequest) {
		super(visit.getCart_items());
		this.visit = visit;
		this.requestCounter = requestCounter;
		this.couponCode = couponCode;
		this.specialRequest = specialRequest;
	}
	
	private void addHeader() {
		enableUnderlinePrinting();
		centerAlignPrinting();
		addString("CARRY OUT ORDER");
		leftAlignPrinting();
		disableUnderlinePrinting();
		addString("\n\n");
	}
	
	public ArrayList<Byte> getPrintList() {
		list.clear();
		
		init();
		addHeader();
		addLine("Print Counter", String.valueOf(requestCounter));
        addDateStamp();
        addLine("Time In", DateUtils.getTime(visit.getOrder_time(), 0));
        addLine("Pick Up", DateUtils.getTime(visit.getOrder_time(), Config.defaultCarryoutTime));
        addString("\n");
        addLine("Name", visit.getName());
        addLine("Number", visit.getPhone_number());
		addString("\n");
        addCartItems();
        addString("\n");
        if (couponCode != null && couponCode.length() > 0) {
        	couponCode = couponCode.toUpperCase();
            addLine("Coupon Code ", couponCode);
            addString("\n");
		}
        
        if (specialRequest != null && specialRequest.length() > 0) {
            addLine("Special Request ", specialRequest);
            addString("\n\n");
		}

        addEndOfReceipt();

		return list;
	}
}
