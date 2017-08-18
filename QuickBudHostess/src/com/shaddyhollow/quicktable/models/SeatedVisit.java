package com.shaddyhollow.quicktable.models;

import java.util.Date;
import java.util.UUID;

public class SeatedVisit {
	public UUID id;
	public UUID queued_visit_id;
	public UUID visit_id;
	public String name;
	public int party_size;
	public UUID server_id;
	public String seating_time;
	public String comment;
	public boolean order_in;
}
