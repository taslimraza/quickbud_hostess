package com.shaddyhollow.freedom.hostess;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shaddyhollow.quickbud.R;
import com.shaddyhollow.quickbud.datastore.CarryoutLoader;
import com.shaddyhollow.quickbud.datastore.Selectable;
import com.shaddyhollow.quicktable.models.CarryOutVisit;
import com.shaddyhollow.util.DateUtils;

public class CarryoutAdapter extends CursorAdapter implements Selectable<CarryOutVisit> {
	protected LayoutInflater layoutInflater = null;
	CarryOutVisit selection = null;

	public CarryoutAdapter(Context context) {
		super(context, null, 0);
		layoutInflater = LayoutInflater.from(context);
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
		View view = layoutInflater.inflate(R.layout.carryouts_list_item, parent, false);
		ViewHolder holder = new ViewHolder(position, view);
		view.setTag(holder);

		setItemProperties(view, holder, position);

		return view;
	}

	private void setItemProperties(View view, ViewHolder holder, int position) {
		Cursor cursor = (Cursor)getItem(position);
		CarryOutVisit carryout = CarryoutLoader.convertToObject(cursor);

		if(selection!=null && selection.getId().equals(carryout.getId())) {
			view.setBackgroundColor(Color.LTGRAY);
		} else {
			view.setBackgroundColor(Color.TRANSPARENT);
		}

		holder.name.setText(carryout.getName());
		holder.phone.setText(carryout.getPhone_number());
		holder.timeIn.setText(DateUtils.getTime(carryout.getOrder_time()));
		holder.patron_status.setText(carryout.getPatronStatus());
		holder.order_status.setText(carryout.getOrderStatus());
	}

	static class ViewHolder {
		public TextView name;
		public TextView phone;
		public TextView timeIn;
		public TextView order_status;
		public TextView patron_status;
		public TextView partySize;
		public TextView waitTime;
		public ImageView orderIn;
		public TextView specialNeeds;
		public int position;

		public ViewHolder(int position, View view) {
			this.name = (TextView) view.findViewById(R.id.name);
			this.phone = (TextView) view.findViewById(R.id.phone);
			this.timeIn = (TextView) view.findViewById(R.id.time_in);
			this.order_status = (TextView) view.findViewById(R.id.order_status);
			this.patron_status = (TextView) view.findViewById(R.id.patron_status);
			this.partySize = (TextView) view.findViewById(R.id.party_size);
			this.waitTime = (TextView) view.findViewById(R.id.wait_time);
			this.orderIn = (ImageView) view.findViewById(R.id.order_in);
			this.specialNeeds = (TextView) view.findViewById(R.id.special_needs);
			this.position = position;
		}
	}

	@Override
	public void clearSelectionPosition() {
		selection = null;
		this.notifyDataSetChanged();
	}

	@Override
	public void setSelection(CarryOutVisit selection) {
		this.selection = selection;
		this.notifyDataSetChanged();
	}

	@Override
	public CarryOutVisit getSelection() {
		return selection;
	}

}
