package com.shaddyhollow.freedom.dinendashhostess.printer;

import java.util.ArrayList;

public class TestReceipt extends Receipt {
	private String testString;
	
	public TestReceipt() {
		super(null);
	}
	
	public TestReceipt(String testString) {
		super(null);
		this.testString = testString;
	}
	
	public ArrayList<Byte> getPrintList() {
		list.clear();
		
		setPageArea2Inch();
        addTimeStamp();
		addString(testString + "\n");
		addParty("Test Receipt", 8);
		addString("\n");

        addEndOfReceipt();

		return list;
	}
}
