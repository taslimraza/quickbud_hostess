package com.shaddyhollow.freedom.sectionplans;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shaddyhollow.quickbud.R;
import com.shaddyhollow.freedom.data.BaseDataAdapter;
import com.shaddyhollow.quickbud.datastore.SectionPlanFactory;
import com.shaddyhollow.quicktable.models.SectionPlan;

public class SectionPlanAdapter extends BaseDataAdapter<SectionPlan> {
	public SectionPlanAdapter(Context context) {
		super(context);
		addAll(SectionPlanFactory.getInstance().bulkRead(null));
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final SectionPlan area = getItem(position);
		View view = convertView;
		if(view==null) {
			view = layoutInflater.inflate(R.layout.simple_text_selected, null);
		}

		TextView name = (TextView)view.findViewById(R.id.text);
		name.setText(String.valueOf(area.name));
		return view;
	}

}
