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

import com.android.colorpicker.ColorPickerDialog;
import com.android.colorpicker.ColorPickerSwatch;
import com.shaddyhollow.quickbud.R;
import com.shaddyhollow.quickbud.Config;

public class EditorDetailFragment extends DialogFragment {
	public final static String KEY_ID = "SECTIONID";
	public final static String KEY_NAME = "NAME";
	public final static String KEY_COLOR = "COLOR";
	private UUID sectionID;
	private String name;
	private int colorID = 0;

    public static EditorDetailFragment newInstance() {
        return new EditorDetailFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.sectionarea_sectioneditor, container, false);
        getDialog().setTitle("Edit Section");

        Bundle data = getArguments();
        if(data!=null) {
	        setName(data.getString(KEY_NAME));
	        setColorID(data.getInt(KEY_COLOR));
	        setSectionID(UUID.fromString(data.getString(KEY_ID)));
        }
        final TextView name = (TextView)v.findViewById(R.id.editName);
        name.setText(getName());

        final Button btnColor = (Button)v.findViewById(R.id.btnColor);
        btnColor.setBackgroundColor(Config.getSectionPalette()[getColorID()]);
        btnColor.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ColorPickerDialog colorcalendar = ColorPickerDialog.newInstance(
			              R.string.color_picker_default_title, 
			              Config.getSectionPalette(),
			              Config.getSectionPalette()[getColorID()],
			              5,
			              ColorPickerDialog.SIZE_LARGE);

			  //Implement listener to get selected color value
			  colorcalendar.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener(){

			                @Override
			                public void onColorSelected(int selectedColor) {
			            		int[] colors = Config.getSectionPalette();
			            		for(int i=0;i<colors.length;i++) { 
			            			if(colors[i]==selectedColor) {
			            				setColorID(i);
			            				break;
			            			}
			            		}

			                	btnColor.setBackgroundColor(Config.getSectionPalette()[getColorID()]);
			                }

			    });

			  colorcalendar.show(getFragmentManager(),"cal");
			}
		});
        
        final Button btnOK = (Button)v.findViewById(R.id.btnOK);
        btnOK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent data = new Intent();
				data.putExtra(KEY_ID, getSectionID());
				data.putExtra(KEY_NAME, name.getText().toString());
				data.putExtra(KEY_COLOR, getColorID());
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

	public int getColorID() {
		return colorID;
	}

	public void setColorID(int colorID) {
		this.colorID = colorID;
	}

	public UUID getSectionID() {
		return sectionID;
	}

	public void setSectionID(UUID sectionID) {
		this.sectionID = sectionID;
	}

}
