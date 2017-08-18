package com.shaddyhollow.freedom.floorplans;

import java.util.UUID;

import android.content.Intent;

import com.flurry.android.FlurryAgent;
import com.shaddyhollow.freedom.data.BaseDataAdapter;
import com.shaddyhollow.quickbud.FlurryEvents;
import com.shaddyhollow.quickbud.datastore.FloorplanFactory;
import com.shaddyhollow.quicktable.generic.listeditor.ItemListManagerActivity;
import com.shaddyhollow.quicktable.models.Floorplan;
import com.shaddyhollow.quicktable.models.Identifiable;

public class FloorplanManagerActivity extends ItemListManagerActivity<Floorplan> {

	@Override
	public BaseDataAdapter<Floorplan> initAdapter() {
		return new FloorplanAdapter(this);
	}

	@Override
	public String getItemType() {
		return "Floorplan";
	}

	@Override
	public void performItemDelete(Identifiable item) {
		FloorplanFactory.getInstance().delete(item.getId());
		if(adapter.getCurrentSelectionPosition()!=-1) {
			adapter.remove(adapter.getCurrentSelectionPosition());
		}
		adapter.notifyDataSetChanged();
        FlurryAgent.logEvent(FlurryEvents.FLOORPLAN_DELETE.name());
	}

	@Override
	public void performItemUpdate(Identifiable item) {
		Floorplan floorplan;
		floorplan = adapter.getItemByID(item.getId());
		floorplan.name = item.getName();
		FloorplanFactory.getInstance().createOrUpdate(floorplan);
        FlurryAgent.logEvent(FlurryEvents.FLOORPLAN_UPDATE.name());
		adapter.notifyDataSetChanged();
	}

	@Override
	public void performItemCreate(String itemName) {
		Floorplan floorplan;
		floorplan = new Floorplan();
		floorplan.id=UUID.randomUUID();
		floorplan.name = itemName;

		adapter.add(floorplan);
		FloorplanFactory.getInstance().createOrUpdate(floorplan);
        FlurryAgent.logEvent(FlurryEvents.FLOORPLAN_UPDATE.name());
		adapter.setSelectionByID(floorplan.getId());
		adapter.notifyDataSetChanged();
	}

	@Override
	public Intent getDetailEditor() {
		Intent myIntent = new Intent(this, FloorplanBuilderActivity.class);
		Floorplan floorplan = adapter.getCurrentSelection();
		myIntent.putExtra("KEY_ID", floorplan.getId());
		return myIntent;
	}

	
}
