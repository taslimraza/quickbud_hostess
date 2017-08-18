package com.shaddyhollow.freedom.dinendashhostess.printer;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.shaddyhollow.robospice.BaseRequest;

/**
 * 
 * @author sashikolli
 * @deprecated
 */
public class PrintRequest extends BaseRequest<Boolean>{
	private ArrayList<Byte> list;
	private Context context;
	private String printerName = null;
	boolean printed = false;

	public PrintRequest(Context context, String printerName, ArrayList<Byte> list) {
		super(Boolean.class);
		this.list = list;
		this.context = context;
		this.printerName = printerName;
	}

	@Override
	public Boolean loadOfflineData() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		String portName = sp.getString(printerName, "BT:");
		
		if(portName!=null && portName.length()>0) {
			try {
				MiniPrinterFunctions.sendCommand(context, portName, "mini", list);
				printed = true;
			} catch (Exception e) {
				printed = false;
			}
		}
		return printed;
	}

	@Override
	public boolean isOnlineAvailable() {
		return false;
	}
	
}
