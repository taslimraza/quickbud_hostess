package com.shaddyhollow.quicktable.models;

import java.util.ArrayList;


public class ListQueuedVisits {
    private ArrayList<QueuedVisit> visits;

    public ArrayList< QueuedVisit > getVisits() {
        return visits;
    }

    public void setVisits( ArrayList< QueuedVisit > visits ) {
        this.visits = visits;
    }
}
