package com.shaddyhollow.freedom.hostess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.shaddyhollow.quickbud.R;
import com.shaddyhollow.freedom.sections.SectionSuggestor;
import com.shaddyhollow.freedom.sections.SectionsAdapter;
import com.shaddyhollow.quickbud.Config;
import com.shaddyhollow.quickbud.datastore.SectionPlanFactory;
import com.shaddyhollow.quicktable.models.Section;
import com.shaddyhollow.quicktable.models.Server;
import com.shaddyhollow.quicktable.models.Table;
import com.shaddyhollow.quicktable.models.Table.Status;

public class DiningSectionsAdapter extends SectionsAdapter {
	Context mContext = null;
	CompoundButton.OnCheckedChangeListener listener = null;
	SectionSuggestor sectionSuggestor = null;
	
	public DiningSectionsAdapter(Context context, CompoundButton.OnCheckedChangeListener listener) {
		super(context);
		mContext = context;
		this.listener = listener;
		sectionSuggestor = new SectionSuggestor(this);
	}

	public void moveItem(int oldPos, int newPos) {
		Section item = getItem(oldPos);
		getAll().remove(oldPos);
		getAll().add(newPos, item);
		SectionPlanFactory.getInstance().updateDailySortOrder(getAll());
	}
	
	public void incrementNextSection() {
		sectionSuggestor.incrementNextSection();
	}
	
	public void markSectionServed(UUID sectionID) {
		sectionSuggestor.markSectionServed(sectionID);
		incrementNextSection();
	}
	
	public UUID getNextSectionID() {
		return sectionSuggestor.getNextSectionID();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Section thisSection = getItem(position);
		if(getSelection()!=null && getSelection().getId().equals(thisSection.getId())) {
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
			view = layoutInflater.inflate(R.layout.hostess_sectionsummary, null);
			holder.colorSection = (TextView)view.findViewById(R.id.list_image);
			holder.totalTables = (TextView)view.findViewById(R.id.total_tables);
			holder.btnServers = (ImageButton)view.findViewById(R.id.btn_servers);
			holder.cardBackground = (View)view.findViewById(R.id.cardbackground);
			view.setTag(holder);
		}
		holder = (SummaryViewHolder)view.getTag();
		holder.colorSection.setText(section.name);
		holder.colorSection.setTextColor(section.open ? Color.WHITE : Color.BLACK);
		holder.colorSection.setBackgroundColor(Config.getSectionPalette()[section.colorID]);
		holder.btnServers.setTag(section.getId());
		LinearLayout list = (LinearLayout)view.findViewById(R.id.server_list);
		list.removeAllViews();
		Server[] servers = section.getServers();
		if(servers!=null && servers.length>0) {
			holder.btnServers.setVisibility(View.GONE);
			list.setVisibility(View.VISIBLE);
			for(Server server : servers) {
				if(server==null) {
					continue;
				}
				View serverView = layoutInflater.inflate(R.layout.hostess_sections_server, null);
				TextView serverName = (TextView)serverView.findViewById(R.id.server_name);
				Button btnResetTables = (Button)serverView.findViewById(R.id.server_tables);
				LinearLayout tableList = (LinearLayout)serverView.findViewById(R.id.listTables);
				
				tableList.setVisibility(View.GONE);
				serverName.setText(server.name);
				btnResetTables.setText(String.valueOf(server.getTables_served()));
				btnResetTables.setTextColor(Color.BLACK);
				btnResetTables.setTag(server.getId());

				if(server.colorstate==Color.RED) {
					btnResetTables.setTextColor(Color.RED);
				}
				list.addView(serverView);
			}
		} else {
			list.setVisibility(View.GONE);
			holder.btnServers.setVisibility(View.VISIBLE);
		}
		if(section.tables!=null) {
			int openTables = 0;
			for(Table table : section.tables) {
				if(table.getState().equals(Status.OPEN) && table.seated_visit==null && section.open) {
					openTables++;
				}
			}
			holder.totalTables.setText(openTables + " open");
		}

		UUID nextSectionID = getNextSectionID();
		if(nextSectionID!=null && nextSectionID.equals(section.getId())) {
			holder.cardBackground.setBackgroundResource(R.drawable.bg_card_next);
		} else {
			holder.cardBackground.setBackgroundResource(section.open ? R.drawable.bg_card_selected : R.drawable.bg_card_normal);
		}

		
		view.setBackgroundColor(Color.TRANSPARENT);
		return view;
	}

	private View getDetailView(int position, View convertView, ViewGroup parent) {
		DetailViewHolder holder = null;
		Section section = getItem(position);
		View view = convertView;

		if(view==null || !(view.getTag() instanceof DetailViewHolder)) {
			holder = new DetailViewHolder();
			view = layoutInflater.inflate(R.layout.hostess_sectiondetail, null);
			holder.colorSection = (TextView)view.findViewById(R.id.list_image);
			holder.totalTables = (TextView)view.findViewById(R.id.total_tables);
			holder.btnServers = (ImageButton)view.findViewById(R.id.btn_servers);
			holder.btnResetTables = (ImageButton)view.findViewById(R.id.btn_resettables);
			holder.switchOpen = (Switch)view.findViewById(R.id.switch_open);
			holder.cardBackground = (View)view.findViewById(R.id.cardbackground);
			view.setTag(holder);
		}
		holder = (DetailViewHolder)view.getTag();

		holder.switchOpen.setChecked(section.open);
		holder.switchOpen.setOnCheckedChangeListener(listener);

		if(section.open) { 
			holder.colorSection.setBackgroundColor(Config.getSectionPalette()[section.colorID]);
			holder.colorSection.setText(section.name);
			holder.colorSection.setTextColor(Color.WHITE);
			
		} else {
			holder.colorSection.setBackgroundColor(Config.getSectionPalette()[section.colorID]);
			holder.colorSection.setText(section.name);
			holder.colorSection.setTextColor(Color.BLACK);
		}

		holder.btnServers.setTag(section.getId());
		holder.btnServers.setVisibility(View.VISIBLE);
		holder.btnResetTables.setVisibility(View.VISIBLE);
		holder.btnResetTables.setTag(section.getId());

		LinearLayout list = (LinearLayout)view.findViewById(R.id.server_list);
		list.removeAllViews();
		Server[] servers = section.getServers();
		if(servers!=null) {
			
			list.setVisibility(View.VISIBLE);
			for(Server server : servers) {
				if(server==null) {
					continue;
				}
				View serverView = layoutInflater.inflate(R.layout.hostess_sections_server, null);
				TextView serverName = (TextView)serverView.findViewById(R.id.server_name);
				Button btnResetTables = (Button)serverView.findViewById(R.id.server_tables);
				LinearLayout tableList = (LinearLayout)serverView.findViewById(R.id.listTables);
				
				serverName.setText(server.name);
				btnResetTables.setText(String.valueOf(server.getTables_served()));
				btnResetTables.setTextColor(Color.BLACK);
				btnResetTables.setTag(server.getId());

				if(server.min_party==1 || server.max_party>7) {
					btnResetTables.setTextColor(Color.RED);
				}
				if(section.tables!=null) {
					int openTables = 0;

					tableList.setVisibility(View.VISIBLE);
					List<Table> sectionTables = new ArrayList<Table>();
					sectionTables.addAll(Arrays.asList(section.tables));
					for(Table table : section.tables) {
						if(table.getState().equals(Status.OPEN) && table.seated_visit==null && section.open) {
							openTables++;
						}
						if(table.seated_visit!=null && table.seated_visit.server_id==server.getId()) {
							TextView textView = new TextView(mContext);
							textView.setPadding(20, 0, 0, 0);
							textView.setTextSize(14);
							textView.setTextColor(Color.BLACK);
							textView.setText("# " + table.name + "/" + table.seated_visit.name + " (" + table.seated_visit.party_size + ")");
							if(table.seated_visit.party_size==1 || table.seated_visit.party_size>7) {
								textView.setTextColor(Color.RED);
							}
							tableList.addView(textView);

							sectionTables.remove(table);
						}
					}
				
					holder.totalTables.setText(openTables + " open");
				}
					
				tableList.setVisibility(View.VISIBLE);

				list.addView(serverView);
			}
		} else {
			list.setVisibility(View.GONE);
		}

		UUID nextSectionID = getNextSectionID();
		if(nextSectionID!=null && nextSectionID.equals(section.getId())) {
			holder.cardBackground.setBackgroundResource(R.drawable.bg_card_next);
		} else {
			holder.cardBackground.setBackgroundResource(section.open ? R.drawable.bg_card_selected : R.drawable.bg_card_normal);
		}
		view.setBackgroundColor(Color.TRANSPARENT);
		return view;
	}

	static class SummaryViewHolder {
		TextView colorSection;
		TextView totalTables;
		View cardBackground;
		ImageButton btnServers;
		Button btnResetTables;
	}
	
	static class DetailViewHolder {
		TextView colorSection;
		TextView totalTables;
		TextView tableCount;
		LinearLayout listServers;

		TextView seatCount;
		TextView server;
		TextView state;
		
		ImageButton btnServers;
		ImageButton btnResetTables;
		Switch switchOpen; 
		View cardBackground;
	}

}
