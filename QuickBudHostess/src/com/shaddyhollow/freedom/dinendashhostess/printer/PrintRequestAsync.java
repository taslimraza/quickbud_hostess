package com.shaddyhollow.freedom.dinendashhostess.printer;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

public class PrintRequestAsync extends AsyncTask<ArrayList<Byte>, Void, Map<String, String>> {
	private final static String KEY_SUCCESS = "SUCCESS";
	private final static String KEY_ERROR_MSG = "ERROR_MSG";
	private Context context;
	private String printerName = null;
	private PrintListener listener = null;

	public PrintRequestAsync(Context context, String printerName, PrintListener listener) {
		this.context = context;
		this.printerName = printerName;
		this.listener = listener;
	}

	@Override
	protected Map<String, String> doInBackground(ArrayList<Byte>... params) {

		Map<String, String> result = new TreeMap<String, String>();
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		String portName = sp.getString(printerName, "BT:");

		if(portName!=null && portName.length()>0) {
			ArrayList<Byte> list = params[0];
			try {
				MiniPrinterFunctions.sendCommand(context, portName, "mini", list);
				result.put(KEY_SUCCESS, Boolean.TRUE.toString());
			} catch (Exception e) {
				result.put(KEY_ERROR_MSG, e.getMessage());
			}
		}
		return result;
	}
	
	@Override
	protected void onPostExecute(Map<String, String> result) {
		super.onPostExecute(result);
		if(result!=null) {
			if(result.get(KEY_SUCCESS)!=null && result.get(KEY_SUCCESS).equals(Boolean.TRUE.toString())) {
				listener.onSuccess();
			} else {
				listener.onFailure(result.get(KEY_ERROR_MSG));
			}
		}
	}
	
	public interface PrintListener {
		public void onSuccess();
		public void onFailure(String reason);
	}

}
