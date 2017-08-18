package com.shaddyhollow.util;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

@SuppressLint("ValidFragment")
public class SingleChoiceDialogFragment extends DialogFragment {
	Context mContext;
	int key;
	String title;
	String message;
	String positiveText;
	String negativeText;
	int selected = 0;
	CharSequence[] choices;
	boolean cancelable;
	OnItemSelectedListener mListener;
	
	public void setListener(OnItemSelectedListener mListener) {
		this.mListener = mListener;
	}

	public interface OnItemSelectedListener {
		public void OnItemSelected(int key, String selectedText, int position);
	}
	
	public SingleChoiceDialogFragment(Context context, int key) {
	    mContext = context;
	    this.key = key;
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
	
	public void setChoices(List<String> choices) {
		this.choices = new CharSequence[choices.size()];
		for(int i=0;i<choices.size();i++) {
			this.choices[i] = choices.get(i);
		}
	}
	
	public void setSelected(int selected) {
		this.selected = selected;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		if(mContext==null) {
			mContext = getActivity();
		}
	    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
	    alertDialogBuilder.setTitle(getTitle());
	    alertDialogBuilder.setSingleChoiceItems(choices, selected, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				selected = which;
				
			}
		});
	    alertDialogBuilder.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
	        @Override
	        public void onClick(DialogInterface dialog, int which) {
	        	if(mListener!=null) {
	        		mListener.OnItemSelected(SingleChoiceDialogFragment.this.key, SingleChoiceDialogFragment.this.choices[selected].toString(), selected);
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
