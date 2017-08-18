package com.shaddyhollow.freedom.hostess;


import java.util.UUID;

import android.app.Activity;
import android.app.ListFragment;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.shaddyhollow.quickbud.R;
import com.shaddyhollow.freedom.dinendashhostess.requests.AddQueuedVisitLocalRequest;
import com.shaddyhollow.freedom.dinendashhostess.requests.AddQueuedVisitRemoteRequest;
import com.shaddyhollow.freedom.dinendashhostess.requests.GetQueuedVisitRequestRemote;
import com.shaddyhollow.freedom.dinendashhostess.requests.PagePatronRequest;
import com.shaddyhollow.freedom.hostess.dialogs.AddWalkinDialogFragment;
import com.shaddyhollow.quickbud.Config;
import com.shaddyhollow.quickbud.FlurryEvents;
import com.shaddyhollow.quickbud.datastore.QueuedVisitLoader;
import com.shaddyhollow.quicktable.models.QueuedVisit;
import com.shaddyhollow.quicktable.models.QueuedVisitRequest;
import com.shaddyhollow.robospice.BaseListener;
import com.shaddyhollow.util.FileOperations;

public class QueuedPatronsFragment extends ListFragment {
    private SpiceManager contentManager = null;
	QueuedVisitRequest queuedVisitRequest;
	private QueuedPatronsAdapter patronsAdapter;
	Handler periodicUpdatesHandler = new Handler();
//	Activity mActivity;
	private Integer tenantId;
	private Integer locationId;
	
	public static QueuedPatronsFragment newInstance(Integer tenantId, Integer locationID, QueuedPatronsAdapter queuedPatronsListAdapter, SpiceManager contentManager) {
		QueuedPatronsFragment fragment = new QueuedPatronsFragment();
		fragment.tenantId = tenantId;
		fragment.locationId = locationID;
		fragment.patronsAdapter = queuedPatronsListAdapter;
		fragment.contentManager = contentManager;

		return fragment;
	}
	
	private Runnable periodicUpdatesRunnable = new Runnable() {
	   @Override
	   public void run() {
		   performPeriodicUpdates();
		   periodicUpdatesHandler.postDelayed(this, Config.queuePollingMS);
	   }
	};

	private void performPeriodicUpdates() {
		if(getActivity()!=null) {
			GetQueuedVisitRequestRemote request = new GetQueuedVisitRequestRemote(tenantId, locationId, patronsAdapter, ((HostessActivity)getActivity()).patronsLoader) ;
			request.execute(contentManager, new GetRemoteQueuedVisitsRequestListener() );
		}
	}

	public class GetRemoteQueuedVisitsRequestListener extends BaseListener<Boolean> {
		@Override
		public void onFailure(SpiceException e) {
			((CarryoutActivity)getActivity()).networkStatus.setError(e);
			Crashlytics.log(Log.WARN, "Network", e.getCause().getMessage());
		}

		@Override
		public void onRequestSuccess(Boolean modified) {
			((CarryoutActivity)getActivity()).networkStatus.setSuccess();
			if(modified!=null && modified) {
				QueuedPatronsFragment.this.patronsAdapter.notifyDataSetChanged();
			}
		}
	}
	
    public QueuedPatronsAdapter getPatronsAdapter() {
    	return patronsAdapter;
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.queued_patrons, container, false);

		Button addWalkinButton = (Button)view.findViewById(R.id.add_walkin);
		addWalkinButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AddWalkinDialogFragment newFragment = AddWalkinDialogFragment.newInstance(walkinAddedListener, tenantId, locationId, null);
			    newFragment.show(getFragmentManager(), "dialog");
			}
		});
		
    	setListAdapter(getQueuedPatronsListAdapter());
		periodicUpdatesHandler.postDelayed(periodicUpdatesRunnable, 0);

		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);        
//		mActivity = activity;
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
   	 	FlurryAgent.logEvent(FlurryEvents.QUEUE_SELECT.name());
		Cursor cursor = (Cursor)patronsAdapter.getItem(position);
		patronsAdapter.setSelection(QueuedVisitLoader.convertToObject(cursor));
		((HostessActivity)getActivity()).updateDetails(Mode.PATRON_SINGLE);
	}

	public void addToQueue() {
//		AddQueuedVisitLocalRequest localRequest = new AddQueuedVisitLocalRequest(((HostessActivity)getActivity()).patronsLoader, getQueuedVisitRequest(), patronsAdapter.getCount(), false);
//		try {
//			localRequest.loadOfflineData();
//			QueuedPatronsFragment.this.patronsAdapter.notifyDataSetChanged();
			
//			Toast.makeText( getActivity(), "Adding to queue!", Toast.LENGTH_LONG ).show();
		
			QueuedVisit queuedVisit = new QueuedVisit();

			AddQueuedVisitRemoteRequest remoteRequest = new AddQueuedVisitRemoteRequest(getQueuedVisitRequest(), queuedVisit /*localRequest.localVisit*/);
			remoteRequest.execute(contentManager, new AddQueuedVisitRequestListener());
//		} catch (Exception e) {
//			Crashlytics.logException(e);
//		}
	}

	private class AddQueuedVisitRequestListener extends BaseListener<QueuedVisit> {
		@Override
		public void onFailure(SpiceException e) {
			Toast.makeText( getActivity(), "Error adding to queue: " + e.getMessage(), Toast.LENGTH_LONG ).show();
		}

		@Override
		public void onRequestSuccess(QueuedVisit queuedVisit) {
			// queuedVisit would be null if the hostess is offline
			if(queuedVisit!=null) {
//				((HostessActivity)getActivity()).patronsLoader.update(queuedVisit);
				((HostessActivity)getActivity()).patronsLoader.create(queuedVisit);
				QueuedPatronsFragment.this.patronsAdapter.notifyDataSetChanged();
				Toast.makeText( getActivity(), "Added to queue! ", Toast.LENGTH_LONG ).show();
				
				//--------------------LOG DATA SAVING TO FILE -------------------------
				FileOperations.writeToFile(queuedVisit, false);
				
//				AddQueuedVisitLocalRequest localRequest = new AddQueuedVisitLocalRequest(((HostessActivity)getActivity()).patronsLoader, getQueuedVisitRequest(), patronsAdapter.getCount(), false);
//				try {
//					localRequest.loadOfflineData();
//					QueuedPatronsFragment.this.patronsAdapter.notifyDataSetChanged();
//				} catch (Exception e) {
//					Crashlytics.logException(e);
//				}
				
				// ----------------------- Modified Code -------------------------------------------
//				GetQueuedVisitRequestRemote request = new GetQueuedVisitRequestRemote(tenantId, locationId, patronsAdapter, ((HostessActivity)getActivity()).patronsLoader) ;
//				request.execute(contentManager, new GetRemoteQueuedVisitsRequestListener() );

			}
		}
	}

	public QueuedVisitRequest getQueuedVisitRequest() {
		return queuedVisitRequest;
	}

	public void setQueuedVisitRequest(QueuedVisitRequest queuedVisitRequest) {
		this.queuedVisitRequest = queuedVisitRequest;
	}

	public ListAdapter getQueuedPatronsListAdapter() {
		return patronsAdapter;
	}
	
	private AddWalkinDialogFragment.Listener walkinAddedListener = new AddWalkinDialogFragment.Listener() {

		@Override
		public void onWalkinAdded(QueuedVisitRequest queuedVisitRequest, QueuedVisit patron) {
			queuedVisitRequest.setId(UUID.randomUUID());
	    	setQueuedVisitRequest(queuedVisitRequest);
	    	addToQueue();
		}
	};

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
	     	pagePatron((QueuedVisit) getQueuedPatronsListAdapter().getItem(position));
	     }
		};

		private void pagePatron(QueuedVisit queuedVisit) {
			PagePatronRequest request = new PagePatronRequest(tenantId, locationId, queuedVisit.getId(), "Your Table is ready, Please see the hostess to get seated.");
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
