package com.shaddyhollow.freedom.sections;

import java.util.UUID;

import com.shaddyhollow.freedom.data.DataAdapter;
import com.shaddyhollow.quickbud.datastore.SectionPlanFactory;
import com.shaddyhollow.quicktable.models.Section;

public class SectionSuggestor {
	DataAdapter<Section> adapter;
	int nextSection=-1;
	int currentRound = 0;
	
	public SectionSuggestor(DataAdapter<Section> adapter) {
		this.adapter = adapter;
		currentRound = getMaxServiceRound() + 1;
		setServiceRound(currentRound-1);
	}
	
	public void incrementNextSection() {
		int next;
		
		// try to find next section in this round that has an open table
		next = getFirstUnderservedSection(false);
		if(next==-1) {
			// since there are no sections with open tables, find the next section in this round regardless of open tables
			next = getFirstUnderservedSection(true);
		} 
		if(next==-1) {
			// this round must be all played out.  advance the counter to the next round and find a section regardless of open tables
			setServiceRound(currentRound);
			currentRound = getMaxServiceRound()+1;
			next = getFirstUnderservedSection(true);
		}
		
		nextSection = next;
	}
	
	public UUID getNextSectionID() {
		UUID nextSectionID = null;
		if(nextSection>=adapter.getCount() || nextSection<0) {
			incrementNextSection();
		}
		if(nextSection<adapter.getCount() && nextSection>=0) {
			nextSectionID = adapter.getItem(nextSection).getId();
		}
		return nextSectionID;
	}

	public void markSectionServed(UUID sectionID) {
		if(sectionID==null) {
			return;
		}
		Section section = adapter.getItemByID(sectionID);
		if(section==null) {
			return;
		}
		section.serviceRound = currentRound;
		SectionPlanFactory.getInstance().updateSection(section);
	}
	
	private int getMaxServiceRound() {
		int maxRound = 0;
		for(int i=0;i<adapter.getCount();i++) {
			maxRound = Math.max(maxRound, adapter.getItem(i).serviceRound);
		}
		return maxRound;
	}
	
	private void setServiceRound(int serviceRound) {
		for(int i=0;i<adapter.getCount();i++) {
			adapter.getItem(i).serviceRound = serviceRound;
		}
	}
	
	private int getFirstUnderservedSection(boolean ignoreFullSections) {
		int underservedSection = -1;
		
		for(int i=0;i<adapter.getCount();i++) {
			Section section = adapter.getItem(i);
			if(section.serviceRound<currentRound) {
				if(section.open && section.getServers()!=null && section.getServers().length>0) {
					if(ignoreFullSections || section.getOpenTableCount()>0) {
						underservedSection = i;
						break;
					}
				}
			}
		}
		return underservedSection;
	}

}
