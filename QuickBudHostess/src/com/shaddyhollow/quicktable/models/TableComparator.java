package com.shaddyhollow.quicktable.models;

import java.util.Comparator;


public class TableComparator implements Comparator<Table>{

	@Override
    public int compare(Table t1, Table t2) {
        int retval = 0;
        try {
                retval = Integer.valueOf(t1.name)-Integer.valueOf(t2.name);
        } catch (NumberFormatException e) {
                retval = t1.name.compareTo(t2.name);
        }
        return retval;
	}
}

