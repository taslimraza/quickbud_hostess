package com.shaddyhollow.freedom.hostess.dialogs;

import java.util.UUID;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.shaddyhollow.quickbud.R;
import com.shaddyhollow.quicktable.models.QueuedVisit;
import com.shaddyhollow.quicktable.models.QueuedVisitRequest;

public class AddWalkinDialogFragment extends DialogFragment {
	private QueuedVisitRequest queuedVisitRequest;
	private Listener listener;
	private Integer locationID;
	private Integer tenantID;
	private QueuedVisit patron;
	private String action;
	
    public static AddWalkinDialogFragment newInstance(Listener listener, Integer tenantId, Integer locationID, QueuedVisit patron) {
    	AddWalkinDialogFragment fragment = new AddWalkinDialogFragment();
    	fragment.initialize(listener);
    	fragment.locationID = locationID;
    	fragment.tenantID = tenantId;
    	fragment.patron = patron;
        return fragment;
    }
    
    public interface Listener {
		void onWalkinAdded(QueuedVisitRequest queuedVisitRequest, QueuedVisit patron);
    }
    
    public void initialize(Listener listener) {
    	this.listener = listener;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.newwalkin, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        if(patron!=null) {
        	action = "Update";
        	populateView(v);
        } else {
        	action = "Add";
        }
        
        queuedVisitRequest = new QueuedVisitRequest();
        
        Button addButton = (Button)v.findViewById(R.id.add_walkin);
        addButton.setText(action + " Walk-In Patron");
        addButton.setOnClickListener(new OnClickListener() {
			@Override
            public void onClick(View v) {
            	LoadQueuedVisitRequest();
            	if (validate()) {
	            	dismiss();
	            	listener.onWalkinAdded(queuedVisitRequest, patron);
            	}
            }
        });
        
        Button cancelButton = (Button)v.findViewById(R.id.cancel);
        cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
        
        return v;
    }

    private boolean validate() {
    	boolean ok = true;
    	if (queuedVisitRequest.getParty_size() < 1) {
    		Toast.makeText(getActivity().getApplicationContext(), "Please enter a valid party size.", Toast.LENGTH_SHORT).show();
    		ok = false;
    	}
//    	if(queuedVisitRequest.getName()==null || queuedVisitRequest.getName().length()==0) {
//    		Toast.makeText(getActivity().getApplicationContext(), "Please enter a valid name for the party.", Toast.LENGTH_SHORT).show();
//    		ok = false;
//    	}
    	return ok;
    }
    
    private void populateView(View view) {
		TextView name = (TextView) view.findViewById(R.id.name);
		TextView phone = (TextView) view.findViewById(R.id.phone);
		TextView size = (TextView) view.findViewById(R.id.party_size);
		CheckBox highChair = (CheckBox) view.findViewById(R.id.high_chair);
		CheckBox boosterSeat = (CheckBox) view.findViewById(R.id.booster_seat);
		CheckBox wheelChairAccess = (CheckBox) view.findViewById(R.id.wheelchair_access);
		TextView comment = (TextView) view.findViewById(R.id.comment);

		if(patron!=null) {
			name.setText(patron.getName());
			phone.setText(patron.getPhone_number());
			size.setText(String.valueOf(patron.getParty_size()));
			highChair.setChecked(patron.getHigh_chairs()>0);
			boosterSeat.setChecked(patron.getBooster_seats()>0);
			wheelChairAccess.setChecked(patron.isWheel_chair_access());
			comment.setText(patron.getSpecialRequests());
    	}
    	
    }
    
    private void LoadQueuedVisitRequest() {
		TextView name = (TextView) getView().findViewById(R.id.name);
		TextView phone = (TextView) getView().findViewById(R.id.phone);
		TextView size = (TextView) getView().findViewById(R.id.party_size);
		CheckBox highChair = (CheckBox) getView().findViewById(R.id.high_chair);
		CheckBox boosterSeat = (CheckBox) getView().findViewById(R.id.booster_seat);
		CheckBox wheelChairAccess = (CheckBox) getView().findViewById(R.id.wheelchair_access);
		TextView comment = (TextView) getView().findViewById(R.id.comment);

		queuedVisitRequest.setTenant_id(tenantID);
		queuedVisitRequest.setLocation_id(locationID);
		queuedVisitRequest.setName(name.getText().toString());

		if(queuedVisitRequest.getName()==null || queuedVisitRequest.getName().trim().length()==0) {
			queuedVisitRequest.setName("Guest");
		}
		queuedVisitRequest.setPhone_number(phone.getText().toString());
		
		try {
			queuedVisitRequest.setParty_size(Integer.parseInt(size.getText().toString()));
		} catch(Exception e) {
			queuedVisitRequest.setParty_size(0);
		}

		if (highChair.isChecked())
		{
			queuedVisitRequest.setHigh_chairs(1);
		}

		if (boosterSeat.isChecked())
		{
			queuedVisitRequest.setBooster_seats(1);
		}

		queuedVisitRequest.setWheel_chair_access(wheelChairAccess.isChecked());

		queuedVisitRequest.setSpecialRequests(comment.getText().toString());

		if(patron!=null && patron.getStatus()!=null && patron.getStatus().length()>0) {
			queuedVisitRequest.setStatus(patron.getStatus());
		} else {
			queuedVisitRequest.setStatus("ARRIVED");
		}
    }
}
