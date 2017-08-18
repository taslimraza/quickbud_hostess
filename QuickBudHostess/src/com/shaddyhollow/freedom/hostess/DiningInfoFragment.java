package com.shaddyhollow.freedom.hostess;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shaddyhollow.quickbud.R;

public class DiningInfoFragment extends Fragment {
	@SuppressWarnings("unused")
	private int locationID;

	public DiningInfoFragment newInstance(int locationID) {
		DiningInfoFragment fragment = new DiningInfoFragment();
		fragment.locationID = locationID;
		return fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.hostess_info, container, false);
		return view;
	}

}
