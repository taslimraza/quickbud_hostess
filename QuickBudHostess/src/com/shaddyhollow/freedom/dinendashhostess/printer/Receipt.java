package com.shaddyhollow.freedom.dinendashhostess.printer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import android.util.Log;

import com.shaddyhollow.quicktable.models.CartItem;
import com.shaddyhollow.quicktable.models.CartItemOption;

public abstract class Receipt {
	private CartItem[] cartItems;
	protected ArrayList<Byte> list = new ArrayList<Byte>();

	public Receipt(CartItem[] cartItems) {
		this.cartItems = cartItems;
	}

	public abstract ArrayList<Byte> getPrintList();

	protected void addParty(String name, int partySize) {
		addPatron(name);
		addString("Party of " + Integer.toString(partySize) + "\n");
		addString("\n");
	}

	protected void addPatron(String name) {
		addString("Patron: " + name + "\n");
	}

	protected void addCartItems() {
		if (cartItems == null) {
			return;
		}
		for (CartItem item : cartItems) {
			addItem(item);
		}
	}

	private void addItem(CartItem item) {
		enableEmphasizedPrinting();
		addString(item.name + "\t" + item.quantity + "\n");
		Log.i("menu_item", item.name + "\t" + item.quantity);
		disableEmphasizedPrinting();
		addOptions(item.cart_item_options);
		if (item.special_requests != null && item.special_requests.length() > 0) {
			addString("  *\t" + item.special_requests);
		}
		addString("\n");
	}

	private void addOptions(CartItemOption[] options) {
		if(options != null){
			for (CartItemOption option : options) {
				addOption(option);
			}	
		}	
	}

	private void addOption(CartItemOption option) {
		String action = "";
		String name = option.name;
		// if (option.action.equalsIgnoreCase("add")) {
		if (option.action.equalsIgnoreCase("A")) {
//			if (option.quantity > 1) {
				action = "  " + String.valueOf(option.quantity);
				name = option.menu_option.name;
//			} else {
//				if (option.name.equalsIgnoreCase("Yes")) {
//					action = "Add" + option.name;
//					name = option.menu_option.name;
//				} else if (option.name.equalsIgnoreCase("No")) {
//					action = "";
//					name = "";
//				} else {
//					action = "Add";
//					name = option.menu_option.name;
//				}
//			}
			// } else if (option.action.equalsIgnoreCase("remove")) {
		} else if (option.action.equalsIgnoreCase("R")) {
			action = " No";
			name = option.menu_option.name;
		}
		if (action.length() > 0 && name.length() > 0) {
			addString(action + "\t" + name + "\n");
			Log.i("menu_item", action + "\t" + name);
		}
	}

	protected void addEndOfReceipt() {
		addString("\n\n\n");
	}

	protected void addDateStamp() {
		Calendar calendar = Calendar.getInstance();
		DateFormat outputFormat = SimpleDateFormat.getDateTimeInstance();
		String date = outputFormat.format(calendar.getTime());

		alignLeft();
		addLine("Date", date);
		addString("\n");
	}

	protected void addTimeStamp() {
		Calendar calendar = Calendar.getInstance();
		DateFormat outputFormat = SimpleDateFormat.getTimeInstance();
		String time = outputFormat.format(calendar.getTime());

		alignLeft();
		addString("Time seated:\t" + time + "\n\n");
	}

	protected void alignLeft() {
		list.addAll(Arrays.asList(new Byte[] { 0x1b, 0x61, 0x00 })); // Left
																		// Alignment
	}

	protected void addString(String string) {
		Byte[] tempList;
		byte[] outputByteBuffer = null;
		outputByteBuffer = string.getBytes();
		tempList = new Byte[outputByteBuffer.length];
		CopyArray(outputByteBuffer, tempList);
		list.addAll(Arrays.asList(tempList));
	}

	protected void addLine(String name, int numTabs, String val) {
		if (name != null) {
			addString(name + ":");
			for (int i = 0; i < numTabs; i++) {
				addString("\t");
			}
		}
		if (val != null) {
			addString(val);
		}
		addString("\n");
	}

	protected void addLine(String name, String val) {
		addLine(name, 1, val);
	}

	protected void enableEmphasizedPrinting() {
		list.addAll(Arrays.asList(new Byte[] { 0x1b, 0x45, 0x01 })); // Set
																		// Emphasized
																		// Printing
																		// ON
	}

	protected void disableEmphasizedPrinting() {
		list.addAll(Arrays.asList(new Byte[] { 0x1b, 0x45, 0x00 })); // Set
																		// Emphasized
																		// Printing
																		// OFF
																		// (same
																		// command
																		// as
																		// on)
	}

	protected void setPageArea2Inch() {
	}

	protected void enableUnderlinePrinting() {
		list.addAll(Arrays.asList(new Byte[] { 0x1b, 0x2d, 0x01 }));
	}

	protected void disableUnderlinePrinting() {
		list.addAll(Arrays.asList(new Byte[] { 0x1b, 0x2d, 0x00 }));
	}

	protected void leftAlignPrinting() {
		list.addAll(Arrays.asList(new Byte[] { 0x1b, 0x61, 0x00 }));
	}

	protected void centerAlignPrinting() {
		list.addAll(Arrays.asList(new Byte[] { 0x1b, 0x61, 0x01 }));
	}

	protected void init() {
		list.addAll(Arrays.asList(new Byte[] { 0x1d, 0x57, (byte) 0x80, 0x31 })); // Page
																					// Area
																					// Setting
																					// <GS>
																					// <W>
																					// nL
																					// nH
																					// (nL
																					// =
																					// 128,
																					// nH
																					// =
																					// 1)
		list.addAll(Arrays.asList(new Byte[] { 0x1b, 0x44, 0x05, 0x0A, 0x0F,
				0x14, 0x19, 0x1E, 0x00 })); // set tab stops 8, 16, 24, 32, ...
	}

	private static void CopyArray(byte[] srcArray, Byte[] cpyArray) {
		for (int index = 0; index < cpyArray.length; index++) {
			cpyArray[index] = srcArray[index];
		}
	}
}
