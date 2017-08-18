package com.shaddyhollow.freedom.hostess.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

public class IntegerInputDialog extends DialogFragment {
    public static IntegerInputDialog newInstance(String title, Listener listener) {
    	IntegerInputDialog fragment = new IntegerInputDialog();
    	fragment.listener = listener;
    	fragment.title = title;
        return fragment;
    }

	private Listener listener;
	private String title;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
    }
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	final EditText input = new EditText(getActivity());
    	input.setInputType(InputType.TYPE_CLASS_PHONE);
    	
        return new AlertDialog.Builder(getActivity())
        		.setTitle(title)
                .setView(input)
                .setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        	int selectedValue = 0;
                        	try {
                        		selectedValue = Integer.valueOf(input.getText().toString());
                        	} catch (Exception e) {
                        		Toast.makeText(getActivity(), "Please enter a valid number", Toast.LENGTH_SHORT).show();
                        	}
                        	listener.onValueSelected(selectedValue);
                        }
                    }
                )
                .setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    }
                )
                .create();    
        }

	public interface Listener {
		public void onValueSelected(int value);
	}
}
