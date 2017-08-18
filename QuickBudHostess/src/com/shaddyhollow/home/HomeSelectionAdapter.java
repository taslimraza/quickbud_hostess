package com.shaddyhollow.home;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shaddyhollow.quickbud.R;

public class HomeSelectionAdapter extends BaseAdapter {
	private Context context;
	private final List<Selection> choices;
 
	public HomeSelectionAdapter(Context context, List<Selection> choices) {
		this.context = context;
		this.choices = choices;
	}
 
	public View getView(int position, View convertView, ViewGroup parent) {
 
		LayoutInflater inflater = (LayoutInflater) context
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
		View view;
 
		if (convertView == null) {
 
			view = new View(context);
 
			// get layout from mobile.xml
			view = inflater.inflate(R.layout.home_selection_item, null);
 
			// set value into textview
			TextView textView = (TextView) view.findViewById(R.id.grid_item_label);
			textView.setText(choices.get(position).title);
 
			// set image based on selected text
			ImageView imageView = (ImageView) view.findViewById(R.id.grid_item_image);
			imageView.setImageResource(choices.get(position).imageResource);
 
		} else {
			view = (View) convertView;
		}
 
		return view;
	}
 
	@Override
	public int getCount() {
		return choices.size();
	}
 
	@Override
	public Object getItem(int position) {
		return null;
	}
 
	@Override
	public long getItemId(int position) {
		return 0;
	}
 
}
