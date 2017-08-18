package com.shaddyhollow.freedom.hostess;

import java.util.ArrayList;
import java.util.Calendar;

class SeatThrottle {
 private int count;
 private int max;
 private int period;

 private ArrayList<Party> parties;

 private class Party {
	 private Calendar entryTime;
	 private int partySize;
	 
	 public Party(int partySize) {
		 this.partySize = partySize;
		 this.entryTime = Calendar.getInstance();
	 }
 }


 public SeatThrottle(int max, int period) {
		 this.setMax(max);
		 this.setPeriod(period);
		 this.setCount(0);
		 this.parties = new ArrayList<Party>();
	 }
	 
	 public void addParty(int partySize) {
		 removeExpired();
		 count += partySize;
		 parties.add(new Party(partySize));
	 }
	 
	 public void removeExpired() {
		 Calendar expireTime =  Calendar.getInstance();
		 expireTime.setTimeInMillis(expireTime.getTimeInMillis() - period * 60 * 1000);
		 
		 while(parties.size() > 0 && 
				 (expireTime.getTimeInMillis() >= parties.get(0).entryTime.getTimeInMillis()) ) {
			 count -= parties.get(0).partySize;
			 parties.remove(0);
		 }
	 }

	public int getCount() {
		removeExpired();
		return count;
	}

	private void setCount(int count) {
		this.count = count;
	}

	public int getMax() {
		return max;
	}

	private void setMax(int max) {
		this.max = max;
	}

	public int getPeriod() {
		return period;
	}

	public void setPeriod(int period) {
		this.period = period;
	}
}