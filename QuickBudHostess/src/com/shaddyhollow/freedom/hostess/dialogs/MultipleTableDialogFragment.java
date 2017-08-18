package com.shaddyhollow.freedom.hostess.dialogs;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.os.Bundle;

import com.shaddyhollow.quicktable.models.Table;

@SuppressLint("ValidFragment")
public class MultipleTableDialogFragment extends DialogFragment {
	Context mContext;
	int key;
	String title;
	String message;
	String positiveText;
	String negativeText;
	CharSequence[] choices;
	boolean[] selected;
	List<Table> tables;
	boolean cancelable;
	OnItemSelectedListener mListener;
	
	public void setListener(OnItemSelectedListener mListener) {
		this.mListener = mListener;
	}

	public interface OnItemSelectedListener {
		public void OnItemSelected(int key, List<Table> objects);
	}
	
	public MultipleTableDialogFragment(Context context, int key) {
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
	
	public void setChoices(List<String> displayText, List<Boolean> selected, List<Table> tables) {
		this.choices = new CharSequence[tables.size()];
		this.selected = new boolean[tables.size()];
		this.tables = new ArrayList<Table>();
		
		for(int i=0;i<tables.size();i++) {
			this.choices[i] = displayText.get(i);
			this.selected[i] = selected.get(i).booleanValue();
			this.tables.add(tables.get(i));
		}
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		if(mContext==null) {
			mContext = getActivity();
		}
	    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
	    alertDialogBuilder.setTitle(getTitle());
	    alertDialogBuilder.setMultiChoiceItems(choices, selected, new OnMultiChoiceClickListener() {

			@Override
			public void onClick(DialogInterface  dialog, int which, boolean isChecked) {
				selected[which]= isChecked; 
			}
	    	
	    });
	    		
		alertDialogBuilder.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
	        @Override
	        public void onClick(DialogInterface dialog, int which) {
	        	if(mListener!=null) {
	        		List<Table> selections = new ArrayList<Table>();
	        		for(int i=0;i<choices.length;i++) {
	        			if(selected[i]) {
	        				selections.add(tables.get(i));
	        			}
	        		}
	        		mListener.OnItemSelected(MultipleTableDialogFragment.this.key, selections);
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
