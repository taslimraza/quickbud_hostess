package com.shaddyhollow.freedom.sections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shaddyhollow.quickbud.R;
import com.shaddyhollow.freedom.data.BaseDataAdapter;
import com.shaddyhollow.quickbud.Config;
import com.shaddyhollow.quickbud.datastore.Selectable;
import com.shaddyhollow.quicktable.models.Section;
import com.shaddyhollow.quicktable.models.Table;

public class SectionsAdapter extends BaseDataAdapter<Section> implements Selectable<Section> {
	private Context mContext;
	String dataDir = Config.getStorageDir("areas");
	Section selection = null;
	
	public SectionsAdapter(Context context) {
		super(context);
		this.mContext = context;
	}
	
	public Section findSectionForTable(UUID tableID) {
		for(Section section : getAll()) {
			for(Table table : section.tables) {
				if(table.id==tableID) {
					return section;
				}
			}
		}
		return null;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(getCurrentSelectionPosition()==position) {
			return getDetailView(position, convertView, parent);
		} else {
			return getSummaryView(position, convertView, parent);
		}
	}

	private View getSummaryView(int position, View convertView, ViewGroup parent) {
		SummaryViewHolder holder = null;
		Section section = getItem(position);
		View view = convertView;

		if(view==null || !(view.getTag() instanceof SummaryViewHolder)) {
			holder = new SummaryViewHolder();
			view = layoutInflater.inflate(R.layout.sectionarea_sectionsummary, null);
			holder.sectionTitle = (TextView)view.findViewById(R.id.section_title);
			holder.colorSection = (TextView)view.findViewById(R.id.list_image);
			holder.tableCount = (TextView)view.findViewById(R.id.tablecount);
			view.setTag(holder);
		}
		holder = (SummaryViewHolder)view.getTag();
		holder.colorSection.setText(section.name);
		holder.colorSection.setBackgroundColor(Config.getSectionPalette()[section.colorID]);
		if(section.tables!=null) {
			holder.tableCount.setText(section.tables.length + " tables");
		}
		view.setBackgroundColor(Color.TRANSPARENT);
		return view;
	}

	private View getDetailView(int position, View convertView, ViewGroup parent) {
		DetailViewHolder holder = null;
		Section section = getItem(position);
		View view = convertView;
		int numTables = 0;
		int numSeats = 0;

		if(view==null || !(view.getTag() instanceof DetailViewHolder)) {
			holder = new DetailViewHolder();
			view = layoutInflater.inflate(R.layout.sectionarea_sectiondetail, null);
			holder.colorSection = (TextView)view.findViewById(R.id.list_image);
			holder.listTables = (LinearLayout)view.findViewById(R.id.listTables);
			holder.listTablesHeader = (TextView)view.findViewById(R.id.listTablesHeader);
			view.setTag(holder);
		}
		holder = (DetailViewHolder)view.getTag();

		holder.colorSection.setBackgroundColor(Config.getSectionPalette()[section.colorID]);
		holder.colorSection.setText(section.name);
		if(section.tables!=null) {
			numTables = section.tables.length;
			for(Table table : section.tables) {
				numSeats += table.seats;
			}
		}

		holder.listTables.removeAllViews();
		if(section.tables!=null && section.tables.length>0) {
			holder.listTablesHeader.setText(numTables + " Tables with " + numSeats + " seats");
			List<String> tableNames = new ArrayList<String>();
			for(Table table : section.tables) {
				tableNames.add("# " + table.name + " (" + table.seats + ")");
			}			
			Collections.sort(tableNames);
			for(String tableName : tableNames) {
				TextView textView = new TextView(mContext);
				textView.setTextSize(14);
				textView.setText(tableName);
				holder.listTables.addView(textView);
				holder.listTables.setVisibility(View.VISIBLE);
			}
		} else {
			holder.listTablesHeader.setText("No Tables in section");
			holder.listTables.setVisibility(View.GONE);
		}
		
		view.setBackgroundColor(Config.getSectionPalette()[section.colorID]);
		return view;
	}

	static class SummaryViewHolder {
		TextView sectionTitle;
		TextView colorSection;
		TextView tableCount;
	}

	static class DetailViewHolder {
		TextView colorSection;
		TextView tableCount;
		LinearLayout listTables;
		TextView listTablesHeader;
		
		TextView seatCount;
		TextView server;
		TextView state;
	}

	public int getUnusedColor() {
		ArrayList<Integer> unusedColors = new ArrayList<Integer>();
		int[] colors = Config.getSectionPalette();
		
		for(int i=0;i<colors.length;i++) {
			unusedColors.add(Integer.valueOf(i));
		}
		for(Section section : getAll()) {
			try {
				unusedColors.remove(Integer.valueOf(section.colorID));
			} catch (Exception e) {
			}
		}
		if(unusedColors.size()==0) {
			unusedColors.add(0);
		}
		int newIndex = unusedColors.get(0);
		return newIndex;
	}

	@Override
	public void setSelection(Section selection) {
		this.selection = selection;
	}

	@Override
	public Section getSelection() {
		return selection;
	}
	
	@Override
	public void clearSelectionPosition() {
		selection = null;
	}
	

}
