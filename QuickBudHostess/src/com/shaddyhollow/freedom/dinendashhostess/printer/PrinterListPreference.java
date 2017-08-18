package com.shaddyhollow.freedom.dinendashhostess.printer;

import java.util.List;

import android.content.Context;
import android.preference.ListPreference;
import android.util.AttributeSet;

import com.starmicronics.stario.PortInfo;
import com.starmicronics.stario.StarIOPort;
import com.starmicronics.stario.StarIOPortException;

public class PrinterListPreference extends ListPreference {

    public PrinterListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

    	List<PortInfo> BTPortList;

    	try {
    		BTPortList  = StarIOPort.searchPrinter("BT:"); 
    		
//    		for(int i=0;i<2;i++) {
//    			BTPortList.add(new PortInfo(String.valueOf(i), String.valueOf(i), String.valueOf(i)));
//    		}
    		
            CharSequence[] entries = new CharSequence[BTPortList.size()+1];
            CharSequence[] entryValues = new CharSequence[BTPortList.size()+1];

    		int i = 0;
    		for(PortInfo discovery : BTPortList)
    		{
    			String portName;
    			entryValues[i] = "BT:" + discovery.getMacAddress();
    			
    			portName = discovery.getPortName();

    			if(discovery.getMacAddress().equals("") == false)
    			{
    				portName += "\n - " + discovery.getMacAddress();
    				if(discovery.getModelName().equals("") == false)
    				{
    					portName += "\n - " + discovery.getModelName();
    				}
    			}
    			entries[i] = portName;
    			i++;
    		}
    		entries[entries.length-1] = "None";
    		entryValues[entryValues.length-1] = "";
    		
    		setEntries(entries);
    		setEntryValues(entryValues);
    		
    	} catch (StarIOPortException e) {
    		e.printStackTrace();
    	}
    	
    }

    public PrinterListPreference(Context context) {
        this(context, null);
    }
}



