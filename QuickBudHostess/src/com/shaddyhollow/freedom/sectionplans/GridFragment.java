package com.shaddyhollow.freedom.sectionplans;

import java.util.UUID;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.shaddyhollow.quickbud.R;

public class GridFragment extends DialogFragment {
	public final static String KEY_ID = "ID";
	public final static String KEY_NAME = "NAME";
	private UUID areaID;
	private UUID floorplanID;
	private String name;
	
    public static GridFragment newInstance() {
        return new GridFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.area_editor, container, false);
        getDialog().setTitle("Edit Area");

        Bundle data = getArguments();
        if(data!=null) {
        	setAreaID(UUID.fromString(data.getString(KEY_ID)));
	        setName(data.getString(KEY_NAME));
        }
        final TextView name = (TextView)v.findViewById(R.id.editName);
        name.setText(getName());

        final Button btnOK = (Button)v.findViewById(R.id.btnOK);
        btnOK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent data = new Intent();
				data.putExtra(KEY_ID, getAreaID());
				data.putExtra(KEY_NAME, name.getText().toString());
				((SectionPlanManagerActivity)getActivity()).onActivityResult(getTargetRequestCode(), 0, data);
				dismiss();
			}
		});
        
        final Button btnCancel = (Button)v.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
        return v;
    }

	public String getName() {
		if(name!=null) {
			return name;
		} else {
			return "";
		}
	}

	public void setName(String name) {
		this.name = name;
	}

	public UUID getAreaID() {
		return areaID;
	}

	public void setAreaID(UUID areaID) {
		this.areaID = areaID;
	}

	public UUID getFloorplanID() {
		return floorplanID;
	}

	public void setFloorplanID(UUID floorplanID) {
		this.floorplanID = floorplanID;
	}

}
