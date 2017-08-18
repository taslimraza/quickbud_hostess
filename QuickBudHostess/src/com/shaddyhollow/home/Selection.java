package com.shaddyhollow.home;

import android.app.Activity;

public class Selection {
	public String title;
	public int imageResource;
	public Class<? extends Activity> activity;
	
	public Selection(String title, int imageResource, Class<? extends Activity> activity) {
		this.title = title;
		this.imageResource = imageResource;
		this.activity = activity;
	}
}
