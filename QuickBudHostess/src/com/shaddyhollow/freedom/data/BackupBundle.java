package com.shaddyhollow.freedom.data;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shaddyhollow.quickbud.datastore.FloorplanFactory;
import com.shaddyhollow.quickbud.datastore.SectionPlanFactory;
import com.shaddyhollow.quicktable.models.Floorplan;
import com.shaddyhollow.quicktable.models.HostessConfig;
import com.shaddyhollow.quicktable.models.SectionPlan;

public class BackupBundle {
	public List<Floorplan> floorplans = new ArrayList<Floorplan>();;
	public List<SectionPlan> sectionPlans = new ArrayList<SectionPlan>();
	
	public void inititalize() {
		floorplans.addAll(FloorplanFactory.getInstance().bulkRead(null));
		sectionPlans.addAll(SectionPlanFactory.getInstance().bulkRead(null));
	}
	
	public void clear() {
		floorplans.clear();
		sectionPlans.clear();
	}
	
	public HostessConfig serialize() {
		Gson gson = new GsonBuilder().create();
		String jsonvalue = gson.toJson(this);
		
		HostessConfig config = new HostessConfig();
		config.hostess_config = Base64Coder.encodeString(jsonvalue); 
        return config;
	}
	
	public void deserialize(HostessConfig config) {
        String jsonValue = null;
        jsonValue = Base64Coder.decodeString(config.hostess_config);

        GsonBuilder gsonBuilder = new GsonBuilder();
		Gson gson = gsonBuilder.create();

        BackupBundle bundle = gson.fromJson(jsonValue, BackupBundle.class);
        this.floorplans = bundle.floorplans;
        this.sectionPlans = bundle.sectionPlans;
        
	}
}
