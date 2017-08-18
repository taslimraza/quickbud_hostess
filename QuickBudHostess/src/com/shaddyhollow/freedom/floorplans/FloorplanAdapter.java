package com.shaddyhollow.freedom.floorplans;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shaddyhollow.quickbud.R;
import com.shaddyhollow.freedom.data.BaseDataAdapter;
import com.shaddyhollow.quickbud.datastore.FloorplanFactory;
import com.shaddyhollow.quicktable.models.Floorplan;

public class FloorplanAdapter extends BaseDataAdapter<Floorplan> {
	public FloorplanAdapter(Context context) {
		super(context);
		addAll(FloorplanFactory.getInstance().bulkRead(null));
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Floorplan floorplan = getItem(position);
		View view = convertView;
		if(view==null) {
			view = layoutInflater.inflate(R.layout.simple_text_selected, null);
		}
		
		TextView name = (TextView)view.findViewById(R.id.text);
		name.setText(String.valueOf(floorplan.name));
		return view;
	}

}
