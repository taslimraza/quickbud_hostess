package com.shaddyhollow.freedom.floorplans;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.shaddyhollow.quickbud.R;

public class EditorDetailFragment extends DialogFragment {
	public final static String KEY_ID = "ID";
	public final static String KEY_NAME = "NAME";
	public final static String KEY_SEATS = "SEATS";
	public final static String KEY_TYPE = "TYPE";
	private UUID tableID;
	private String name;
	private int seats;
	private String type;

    public static EditorDetailFragment newInstance() {
        return new EditorDetailFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.table_editor, container, false);
        getDialog().setTitle("Edit Table");

        Bundle data = getArguments();
        if(data!=null) {
        	setTableID(UUID.fromString(data.getString(KEY_ID)));
	        setName(data.getString(KEY_NAME));
	        setSeats(data.getInt(KEY_SEATS));
	        setType(data.getString(KEY_TYPE));
        }
        final TextView name = (TextView)v.findViewById(R.id.editName);
        name.setText(getName());

        final TextView seats = (TextView)v.findViewById(R.id.editSeats);
        seats.setText(String.valueOf(getSeats()));
        
        final Spinner typeSelector = (Spinner)v.findViewById(R.id.editType);
        List<String> list = Arrays.asList(getResources().getStringArray(R.array.tabletype_arrays));
        int pos = list.indexOf(getType());
        if(pos<0) {
        	pos = 0;
        }
        typeSelector.setSelection(pos);
        typeSelector.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				setType((String)typeSelector.getItemAtPosition(position));
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
        
        final Button btnOK = (Button)v.findViewById(R.id.btnOK);
        btnOK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent data = new Intent();
				data.putExtra(KEY_ID, getTableID());
				data.putExtra(KEY_NAME, name.getText().toString());
				try {
					data.putExtra(KEY_SEATS, Integer.valueOf(seats.getText().toString()));
				} catch (Exception e) {
				}
				data.putExtra(KEY_TYPE, getType());
				getTargetFragment().onActivityResult(getTargetRequestCode(), 0, data);
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

	public int getSeats() {
		return seats;
	}

	public void setSeats(int seats) {
		this.seats = seats;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public UUID getTableID() {
		return tableID;
	}

	public void setTableID(UUID tableID) {
		this.tableID = tableID;
	}

}
