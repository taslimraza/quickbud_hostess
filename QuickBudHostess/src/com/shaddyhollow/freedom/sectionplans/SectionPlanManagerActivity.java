package com.shaddyhollow.freedom.sectionplans;

import java.util.UUID;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.shaddyhollow.freedom.data.BaseDataAdapter;
import com.shaddyhollow.freedom.floorplans.FloorplanAdapter;
import com.shaddyhollow.quickbud.datastore.SectionPlanFactory;
import com.shaddyhollow.quicktable.generic.listeditor.ItemListManagerActivity;
import com.shaddyhollow.quicktable.models.Identifiable;
import com.shaddyhollow.quicktable.models.SectionPlan;

public class SectionPlanManagerActivity extends ItemListManagerActivity<SectionPlan> {
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FloorplanAdapter floorplanAdapter;

		floorplanAdapter = new FloorplanAdapter(this);
        if(floorplanAdapter.getCount()==0) {
			Toast.makeText(this, "Please build a floorplan prior to creating section plans.", Toast.LENGTH_LONG).show();
			finish();
			return;
        }

    }

	public void showEditor(SectionPlan area) {
		GridFragment dlg = GridFragment.newInstance();
		if(area!=null) {
			Bundle bundle = new Bundle();
			bundle.putString(GridFragment.KEY_ID, String.valueOf(area.id));
			bundle.putString(GridFragment.KEY_NAME, area.name);
			dlg.setArguments(bundle);
		}

		dlg.show(getFragmentManager(), "EditorDialog");
	}

	@Override
	public BaseDataAdapter<SectionPlan> initAdapter() {
		return new SectionPlanAdapter(this);
	}

	@Override
	public String getItemType() {
		return "Section Plan";
	}

	@Override
	public void performItemDelete(Identifiable item) {
		SectionPlanFactory.getInstance().delete(item.getId());
		if(adapter.getCurrentSelectionPosition()!=-1) {
			adapter.remove(adapter.getCurrentSelectionPosition());
		}
		adapter.notifyDataSetChanged();
	}

	@Override
	public void performItemUpdate(Identifiable item) {
		SectionPlan area;
		area = adapter.getItemByID(item.getId());
		area.name = item.getName();

		SectionPlanFactory.getInstance().createOrUpdate(area);
		adapter.notifyDataSetChanged();
	}

	@Override
	public void performItemCreate(String itemName) {
		SectionPlan area;
		area = new SectionPlan();
		area.id=UUID.randomUUID();
		area.name = itemName;
		adapter.add(area);
		SectionPlanFactory.getInstance().createOrUpdate(area);
		adapter.setSelectionByID(area.getId());
		adapter.notifyDataSetChanged();
	}

	@Override
	public Intent getDetailEditor() {
		Intent myIntent = new Intent(this, SectionPlanBuilderActivity.class);
		SectionPlan plan = adapter.getCurrentSelection();
		myIntent.putExtra("KEY_ID", plan.getId());
		return myIntent;
	}

}
