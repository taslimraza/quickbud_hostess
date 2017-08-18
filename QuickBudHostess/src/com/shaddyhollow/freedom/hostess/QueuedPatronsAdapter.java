package com.shaddyhollow.freedom.hostess;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shaddyhollow.quickbud.R;
import com.shaddyhollow.quickbud.datastore.QueuedVisitLoader;
import com.shaddyhollow.quickbud.datastore.Selectable;
import com.shaddyhollow.quicktable.models.QueuedVisit;
import com.shaddyhollow.util.DateUtils;

public class QueuedPatronsAdapter extends CursorAdapter implements Selectable<QueuedVisit> {
	protected LayoutInflater layoutInflater = null;
	QueuedVisit selection = null;

	public QueuedPatronsAdapter(Context context) {
		super(context, null, 0);
		layoutInflater = LayoutInflater.from(context);
	}

	private void setItemOrderIn(ViewHolder holder, QueuedVisit queuedVisit) {
		holder.orderIn.setVisibility(queuedVisit.isOrder_in() ? View.VISIBLE : View.INVISIBLE);
	}

	private void setItemWaitTime(ViewHolder holder, QueuedVisit queuedVisit, int position) {
		int lowWaitTime = (position+2)/3 * 5;
		queuedVisit.setLow_wait_time(String.valueOf(lowWaitTime));
		queuedVisit.setHigh_wait_time(String.valueOf(lowWaitTime+5));
		
		holder.waitTime.setText((queuedVisit.getLow_wait_time()) + " - " + 
				(queuedVisit.getHigh_wait_time()) + " min");
	}

	private void setItemPartySize(ViewHolder holder, QueuedVisit queuedVisit) {
		holder.partySize.setText(String.valueOf(queuedVisit.getParty_size()));
	}

	private void setItemStatus(ViewHolder holder, QueuedVisit queuedVisit) {
		if(queuedVisit.getStatus().equalsIgnoreCase("E")){
			holder.status.setText("EN ROUTE");
		}else if(queuedVisit.getStatus().equalsIgnoreCase("A")){
			holder.status.setText("ARRIVED");
		}
//		holder.status.setText(queuedVisit.getStatus());
	}

	private void setItemName(ViewHolder holder, QueuedVisit queuedVisit) {
		holder.name.setText(queuedVisit.getName());
	}

	private void setItemTimeIn(ViewHolder holder, QueuedVisit queuedVisit) {
		holder.timeIn.setText(DateUtils.getTime(queuedVisit.getCreated_at()));
	}

	public void setSpecialNeeds(ViewHolder holder, QueuedVisit queuedVisit) {
		holder.specialNeeds.setVisibility(View.VISIBLE);
		String needs = new String();
		if(queuedVisit.getHigh_chairs()>0) {
			needs += "High Chair";
		}
		if(queuedVisit.getBooster_seats()>0) {
			if(needs.length()>0) {
				needs +="\n";
			}
			needs += "Booster Seat";
		}
		if(queuedVisit.isWheel_chair_access()) {
			if(needs.length()>0) {
				needs +="\n";
			}
			needs += "Wheelchair Access";
		}
		if(queuedVisit.getSpecialRequests()!=null && queuedVisit.getSpecialRequests().length()>0) {
			if(needs.length()>0) {
				needs +="\n";
			}
			needs += queuedVisit.getSpecialRequests();
		}

		holder.specialNeeds.setText(needs);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		int position = cursor.getPosition();
		ViewHolder holder = (ViewHolder)view.getTag();
		setItemProperties(view, holder, position);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		int position = cursor.getPosition();
		View view = layoutInflater.inflate(R.layout.queued_patron_list_item, parent, false);
		ViewHolder holder = new ViewHolder(position, view);
		view.setTag(holder);

		setItemProperties(view, holder, position);
		
		return view;
	}

	private void setItemProperties(View view, ViewHolder holder, int position) {
		Cursor cursor = (Cursor)getItem(position);
		QueuedVisit queuedVisit = QueuedVisitLoader.convertToObject(cursor);
		
		Log.i("List Name", queuedVisit.getName().toString() + "\t" + position + "\t" + queuedVisit.getCreated_at());

		if(selection!=null && selection.getId().equals(queuedVisit.getId())) {
			view.setBackgroundColor(Color.LTGRAY);
		} else {
			view.setBackgroundColor(Color.TRANSPARENT);
		}
		setItemName(holder, queuedVisit);
		setItemTimeIn(holder, queuedVisit);
		setItemStatus(holder, queuedVisit);
		setItemPartySize(holder, queuedVisit);
		setItemWaitTime(holder, queuedVisit, position);
		setItemOrderIn(holder, queuedVisit);
		setSpecialNeeds(holder, queuedVisit);
	}

	static class ViewHolder {
		public ViewHolder(int position, View view) {
			this.name = (TextView) view.findViewById(R.id.name);
			this.timeIn = (TextView) view.findViewById(R.id.time_in);
			this.status = (TextView) view.findViewById(R.id.status);
			this.partySize = (TextView) view.findViewById(R.id.party_size);
			this.waitTime = (TextView) view.findViewById(R.id.wait_time);
			this.orderIn = (ImageView) view.findViewById(R.id.order_in);
			this.specialNeeds = (TextView) view.findViewById(R.id.special_needs);
			this.position = position;
		}
		public TextView name;
		public TextView timeIn;
		public TextView status;
		public TextView partySize;
		public TextView waitTime;
		public ImageView orderIn;
		public TextView specialNeeds;
	
		public int position;
	}

	@Override
	public void clearSelectionPosition() {
		selection = null;
		this.notifyDataSetChanged();
	}

	@Override
	public void setSelection(QueuedVisit selection) {
		this.selection = selection;
		this.notifyDataSetChanged();
	}

	@Override
	public QueuedVisit getSelection() {
		return selection;
	}

}
