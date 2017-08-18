package com.shaddyhollow.quicktable.generic.listeditor;

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

public class SimpleIdentifiableEditorFragment extends DialogFragment {
	public final static String KEY_ID = "ID";
	public final static String KEY_NAME = "NAME";
	private UUID itemID;
	private String name;
	private String itemType;

    public static SimpleIdentifiableEditorFragment newInstance(String itemType) {
    	SimpleIdentifiableEditorFragment fragment = new SimpleIdentifiableEditorFragment();
    	fragment.itemType = itemType;
    	return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.floorplan_editor, container, false);

        Bundle data = getArguments();
        if(data!=null) {
            getDialog().setTitle("Edit " + itemType);
        	setItemID(UUID.fromString(data.getString(KEY_ID)));
	        setName(data.getString(KEY_NAME));
        } else {
            getDialog().setTitle("Add " + itemType);
        }
        final TextView name = (TextView)v.findViewById(R.id.editName);
        name.setText(getName());

        final Button btnOK = (Button)v.findViewById(R.id.btnOK);
        btnOK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent data = new Intent();
				data.putExtra(KEY_ID, getItemID());
				data.putExtra(KEY_NAME, name.getText().toString());
				if(getActivity() instanceof ItemListManagerActivity<?>) {
					((ItemListManagerActivity<?>)getActivity()).onActivityResult(getTargetRequestCode(), 0, data);
				}
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

	public UUID getItemID() {
		return itemID;
	}

	public void setItemID(UUID itemID) {
		this.itemID = itemID;
	}

}
