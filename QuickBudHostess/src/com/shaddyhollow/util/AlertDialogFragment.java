package com.shaddyhollow.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

public class AlertDialogFragment extends DialogFragment {
	Context mContext;
	String title;
	String message;
	String positiveText;
	String negativeText;
	boolean cancelable;
	
	public AlertDialogFragment() {
	    mContext = getActivity();
	    setPositiveText("OK");
	    setNegativeText("Cancel");
	}
	
	public void setPositiveText(String positiveText) {
		this.positiveText = positiveText;
	}

	public void setNegativeText(String negativeText) {
		this.negativeText = negativeText;
	}

	public void setCancelable(boolean cancelable) {
		this.cancelable = cancelable;
	}
	
	public boolean isCancelable() {
		return cancelable;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		if(mContext==null) {
			mContext = getActivity();
		}
	    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
	    alertDialogBuilder.setTitle(getTitle());
	    alertDialogBuilder.setMessage(getMessage());
	    alertDialogBuilder.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
	        @Override
	        public void onClick(DialogInterface dialog, int which) {
	        	if(getTargetFragment()!=null) {
	        		getTargetFragment().onActivityResult(getTargetRequestCode(), 1, null);
	        	}
	            dialog.dismiss();
	        }
	    });
	    if(isCancelable()) {
		    alertDialogBuilder.setNegativeButton(negativeText, new DialogInterface.OnClickListener() {
		        @Override
		        public void onClick(DialogInterface dialog, int which) {
		            dialog.dismiss();
		        }
		    });
	    }
	
	
	    return alertDialogBuilder.create();
	}
}
