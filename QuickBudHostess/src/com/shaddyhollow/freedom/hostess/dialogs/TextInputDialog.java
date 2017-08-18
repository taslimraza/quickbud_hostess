package com.shaddyhollow.freedom.hostess.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;

public class TextInputDialog extends DialogFragment {
	private TextInputListener listener;
	private String title;
	private String dfltText;
    
	public static TextInputDialog newInstance(String title, String dfltText, TextInputListener listener) {
    	TextInputDialog fragment = new TextInputDialog();
    	fragment.listener = listener;
    	fragment.title = title;
    	fragment.dfltText = dfltText;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	final EditText input = new EditText(getActivity());
    	input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
    	if(dfltText!=null) {
    		input.setText(dfltText);
    	}
    	
        return new AlertDialog.Builder(getActivity())
        		.setTitle(title)
                .setView(input)
                .setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        	listener.onValueSelected(input.getText().toString());
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

	public interface TextInputListener {
		public void onValueSelected(String value);
	}
}
