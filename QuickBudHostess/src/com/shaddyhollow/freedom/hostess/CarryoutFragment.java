package com.shaddyhollow.freedom.hostess;

import java.util.UUID;

import android.app.ListFragment;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.shaddyhollow.quickbud.R;
import com.shaddyhollow.freedom.dinendashhostess.requests.GetCarryoutVisitRequestRemote;
import com.shaddyhollow.freedom.dinendashhostess.requests.ListCarryOutVisit;
import com.shaddyhollow.freedom.dinendashhostess.requests.PagePatronRequest;
import com.shaddyhollow.quickbud.Config;
import com.shaddyhollow.quickbud.FlurryEvents;
import com.shaddyhollow.quickbud.datastore.CarryoutLoader;
import com.shaddyhollow.quicktable.models.CarryOutVisit;
import com.shaddyhollow.quicktable.models.QueuedVisit;
import com.shaddyhollow.robospice.BaseListener;

public class CarryoutFragment extends ListFragment {
    private SpiceManager contentManager = null;
	private CarryoutAdapter carryoutAdapter;
	Handler periodicUpdatesHandler = new Handler();
	private boolean autoprint;
	private final static String PREF_AUTOPRINT = "CARRYOUT_AUTOPRINT";
	private Integer locationID;
	private Integer tenantID;
	private boolean printerConnected = false;
	
	public static CarryoutFragment newInstance(Integer tenantID, Integer locationID, CarryoutAdapter carryOutListAdapter, SpiceManager contentManager) {
		CarryoutFragment fragment = new CarryoutFragment();
		fragment.tenantID = tenantID;
		fragment.locationID = locationID;
		fragment.carryoutAdapter = carryOutListAdapter;
		fragment.contentManager = contentManager;

		return fragment;
	}
	
	private Runnable periodicUpdatesRunnable = new Runnable() {
		   @Override
		   public void run() {
			   performPeriodicUpdates();

			   periodicUpdatesHandler.postDelayed(this, Config.carryoutPollingMS);
		   }
	};

	private void performPeriodicUpdates() {
		if(getActivity()!=null && ((CarryoutActivity)getActivity()).carryoutLoader!=null) {
			GetCarryoutVisitRequestRemote request = new GetCarryoutVisitRequestRemote(tenantID, locationID, ((CarryoutActivity)getActivity()).carryoutLoader) ;
			request.execute(contentManager, new GetRemoteCarryoutVisitsRequestListener());
		}
	}

	public class GetRemoteCarryoutVisitsRequestListener extends BaseListener<ListCarryOutVisit> {

		@Override
		public void onFailure(SpiceException e) {
			((CarryoutActivity)getActivity()).networkStatus.setError(e);
		}

		@Override
		public void onRequestSuccess(ListCarryOutVisit modified) {
			((CarryoutActivity)getActivity()).networkStatus.setSuccess();

			if(autoprint && modified!=null && modified.size()>0) {
				((CarryoutActivity)getActivity()).playNotification(R.raw.qt_notification);
			}

			if(autoprint && printerConnected) {
				int carryouts = carryoutAdapter.getCount();
				for(int i=0;i<carryouts;i++) {
					Cursor carryoutCursor = (Cursor)carryoutAdapter.getItem(i);
					CarryOutVisit visit = CarryoutLoader.convertToObject(carryoutCursor);
					if(!visit.getOrderStatus().equals("PRINTED") && !visit.getOrderStatus().equals("PRINTING") && visit.getPrintFailures()<3) {
						((CarryoutActivity)getActivity()).printCarryOutReceipt(visit);
					}
				}
			}
		}
	}
	
    public CarryoutAdapter getCarryOutAdapter() {
    	return carryoutAdapter;
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.carryouts, container, false);

//		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
//		String portName = sp.getString("carryout_printer", "BT:");
//		printerConnected = (portName!=null && portName.length()>0);

//		final Switch switch_autoprint = (Switch)view.findViewById(R.id.switch_autoprint);
//		switch_autoprint.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//				if(isChecked) {
//					switch_autoprint.setBackgroundResource(R.drawable.btn_green);
//				} else {
//					switch_autoprint.setBackgroundResource(R.drawable.btn_beige);
//				}
//				switch_autoprint.setPadding(8, 8, 8, 8);
//				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
//				Editor editor = prefs.edit();
//				editor.putBoolean(PREF_AUTOPRINT, isChecked);
//				editor.commit();
//
//				autoprint = isChecked;
//			}
//		});
//		
//		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
//		switch_autoprint.setChecked(prefs.getBoolean(PREF_AUTOPRINT, true));
//		switch_autoprint.setVisibility(printerConnected ? View.VISIBLE : View.GONE);
		
		setListAdapter(getCarryoutListAdapter());
		periodicUpdatesHandler.post(periodicUpdatesRunnable);

		return view;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
   	 	FlurryAgent.logEvent(FlurryEvents.QUEUE_SELECT.name());
		Cursor cursor = (Cursor)carryoutAdapter.getItem(position);
		carryoutAdapter.setSelection(CarryoutLoader.convertToObject(cursor));
		((CarryoutActivity)getActivity()).updateDetails(Mode.CARRYOUT_SINGLE);
	}

	public ListAdapter getCarryoutListAdapter() {
		return carryoutAdapter;
	}
	
	private int getPositionFromView(View v) {
		Button button = (Button) v;
	 	View parent = (View) button.getParent().getParent();
	 	ViewHolder holder = (ViewHolder) parent.getTag();
	 	final int position = holder.position;
		return position;
	}	

	OnClickListener pageButtonListener = new OnClickListener() {
	     @Override
	     public void onClick(View v) {
	     	final int position = getPositionFromView(v);
	     	pagePatron((QueuedVisit) getCarryoutListAdapter().getItem(position));
	     }
		};

		private void pagePatron(QueuedVisit queuedVisit) {
			PagePatronRequest request = new PagePatronRequest(tenantID, locationID, queuedVisit.getId(), "Your Carryout order is ready!");
			request.execute(contentManager, new PagePatronRequestListener() );
		}
		
		private class PagePatronRequestListener extends BaseListener<Void> {
			@Override
			public void onFailure(SpiceException e) {
		        Toast.makeText( getActivity(), "Error paging patron: " + e.getMessage(), Toast.LENGTH_LONG ).show();
			}

			@Override
			public void onRequestSuccess(Void v) {
		        Toast.makeText( getActivity(), "Patron Paged!", Toast.LENGTH_LONG).show();
			}
			
		}

	static class ViewHolder {
		public ViewHolder(int position, View view) {
			this.name = (TextView) view.findViewById(R.id.name);
			this.timeIn = (TextView) view.findViewById(R.id.time_in);
			this.status = (TextView) view.findViewById(R.id.status);
			this.partySize = (TextView) view.findViewById(R.id.party_size);
			this.waitTime = (TextView) view.findViewById(R.id.wait_time);
			this.orderIn = (ImageView) view.findViewById(R.id.order_in);
			this.specialNeeds = (TextView) view.findViewById(R.id.special_needs);
			this.position = position;
		}
		public TextView name;
		public TextView timeIn;
		public TextView status;
		public TextView partySize;
		public TextView waitTime;
		public ImageView orderIn;
		public TextView specialNeeds;

		public int position;
	}

}
