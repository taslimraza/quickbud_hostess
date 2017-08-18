package com.shaddyhollow.freedom.hostess;

import java.io.IOException;
import java.util.Locale;
import java.util.UUID;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.Loader;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.shaddyhollow.quickbud.R;
import com.shaddyhollow.freedom.dinendashhostess.printer.CarryOutReceipt;
import com.shaddyhollow.freedom.dinendashhostess.printer.PrintRequest;
import com.shaddyhollow.freedom.dinendashhostess.printer.PrintRequestAsync;
import com.shaddyhollow.freedom.dinendashhostess.printer.Receipt;
import com.shaddyhollow.freedom.dinendashhostess.requests.DeleteCarryOutVisitRequest;
import com.shaddyhollow.freedom.dinendashhostess.requests.GetCartItemsRequest;
import com.shaddyhollow.freedom.dinendashhostess.requests.PageCarryOutRequest;
import com.shaddyhollow.freedom.dinendashhostess.requests.TextPatronRequest;
import com.shaddyhollow.freedom.sectionplans.SectionPlanAdapter;
import com.shaddyhollow.quickbud.Config;
import com.shaddyhollow.quickbud.FlurryEvents;
import com.shaddyhollow.quickbud.NetworkStatus;
import com.shaddyhollow.quickbud.NetworkStatus.OnStatusChangedListener;
import com.shaddyhollow.quickbud.datastore.CarryoutLoader;
import com.shaddyhollow.quickbud.datastore.DatabaseHelper;
import com.shaddyhollow.quicktable.models.CarryOutVisit;
import com.shaddyhollow.quicktable.models.CartItems;
import com.shaddyhollow.robospice.BaseListener;
import com.shaddyhollow.robospice.BaseRoboSpiceActivity;

public class CarryoutActivity extends BaseRoboSpiceActivity implements ActionBar.TabListener, LoaderManager.LoaderCallbacks<Cursor>, OnStatusChangedListener {

	private static int printRequestCounter = 0;
	FragmentPagerAdapter mSectionsPagerAdapter;

	private DatabaseHelper db=null;
	public CarryoutLoader carryoutLoader = null;
	public CarryoutAdapter carryoutAdapter = null;
	NetworkStatus networkStatus;

	private final static int CARRYOUT_LOADER = 101;

	Mode mode = Mode.SECTION_LIST;

	ViewPager mViewPager;
	Integer locationID;
	Integer tenantID;
	String restaurantName;
	Handler carryOutHandler = new Handler();

	protected TextView throttleCountView;
	public SeatThrottle seatThrottle;
	protected TextView queuedPatronCountView;
	protected ImageView networkStatusIndicator;
	protected TextView openTableCountView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setupActivity();
	}

	protected void setupActivity() {
		getWindow().addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.hostess_main);
		FlurryAgent.logEvent(FlurryEvents.HOSTESS_ACTIVITY.name());
		View root = findViewById(android.R.id.content); 
		if (root != null) {
			root.setKeepScreenOn(true);
		}
		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setIcon(R.drawable.quickbud_image);
		actionBar.setTitle("");
		
		actionBar.setCustomView(R.layout.network_status_layout);

		locationID = (Integer)getIntent().getExtras().get("LOCATIONID");
		tenantID = (Integer)getIntent().getExtras().get("TENANTID");
		restaurantName = getIntent().getExtras().getString("RESTNAME");
		
		db=new DatabaseHelper(this);
		initLoader(CARRYOUT_LOADER, null, this);
//		getLoaderManager().initLoader(CARRYOUT_LOADER, null, this);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

		// TODO: update carryout order counter displays
		carryoutAdapter = new CarryoutAdapter(this);
		carryoutAdapter.registerDataSetObserver(new DataSetObserver() {

			@Override
			public void onChanged() {
				//				HostessActivity.this.updateQueuedPatronCount();
			}

			@Override
			public void onInvalidated() {
				//				HostessActivity.this.updateQueuedPatronCount();
			}

		});

		setupViewPager();

//		throttleCountView = (TextView) findViewById(R.id.throttle_count);
//		throttleCountView.setVisibility(View.INVISIBLE);
//
		networkStatusIndicator = (ImageView) getActionBar().getCustomView().findViewById(R.id.network_status);
		networkStatusIndicator.setVisibility(View.VISIBLE);
//
//		queuedPatronCountView = (TextView)findViewById(R.id.queued_count);
//		queuedPatronCountView.setVisibility(View.INVISIBLE);
//
//		openTableCountView = (TextView) findViewById(R.id.open_count);
//		openTableCountView.setVisibility(View.INVISIBLE);

		updateDetails(Mode.INFO);
	}

	protected void setupViewPager() {
		final ActionBar actionBar = getActionBar();

		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setOffscreenPageLimit(2);

		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {

				actionBar.setSelectedNavigationItem(position);
				if(position==0) {
					updateDetails(Mode.CARRYOUT_SINGLE);
				}
			}
		});

		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}

	public boolean isCarryoutOnly() {
		return true;
	}
	
	public void updateDetails(Mode mode) {
		this.mode = mode;
		Fragment detailFragment = null;

		if(mode==Mode.CARRYOUT_SINGLE) {
			detailFragment = DiningCarryoutFragment.newInstance(mode, carryoutAdapter, locationID, tenantID);
		}

		if(detailFragment!=null) {
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.replace(R.id.list, detailFragment);
			ft.commit();
		}
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {
		SectionPlanAdapter areasAdapter = null;
		int fixedpages;

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
			fixedpages = 1; //carry out only
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = null;

			Bundle bundle = new Bundle();
			bundle.putString("LOCATIONID", String.valueOf(locationID));
			bundle.putString("TENANTID", String.valueOf(tenantID));

			switch(position) {
			case 0:
				fragment = CarryoutFragment.newInstance(tenantID, locationID, carryoutAdapter, contentManager);
				break;
			};
			fragment.setArguments(bundle);
			return fragment;
		}

		@Override
		public int getCount() {
			return fixedpages; // carryout fragment
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			String title = "";
			switch(position) {
			case 0: 
				title = getString(R.string.carryout_title).toUpperCase(l);
				break;
			}
			return title;
		}

		public Fragment findFragment(int position) {
			String name = "android:switcher:" + mViewPager.getId() + ":" + position;
			FragmentManager fm = getFragmentManager();
			Fragment fragment = fm.findFragmentByTag(name);
			if (fragment == null) {
				fragment = getItem(position);
			}
			return fragment;
		}
	}

	public void printCarryOutReceipt(CarryOutVisit visit) {
		try {
			if(visit!=null && visit.getId()!=null) {
				GetCartItemsRequest request = new GetCartItemsRequest(tenantID, locationID, visit.getVisit_id());
				request.execute(contentManager, new GetCarryOutCartItemsRequestListener(visit));
			} else {
				Toast.makeText(this, "No ticket to print", Toast.LENGTH_SHORT).show();
			}
		} catch (NullPointerException e) {
			Toast.makeText(this, "Problem printing ticket", Toast.LENGTH_SHORT).show();
		}

	}

	private class GetCarryOutCartItemsRequestListener extends BaseListener<CartItems> {
		private CarryOutVisit visit;

		public GetCarryOutCartItemsRequestListener(CarryOutVisit visit) {
			this.visit = visit;
		}

		@Override
		public void onFailure(SpiceException e) {
		}

		@Override
		public void onRequestSuccess(CartItems cart) {
			if(cart==null) {
				return;
			}
			if(cart.cart_items != null && cart.cart_items.length > 0) {
				printCarryOutReceipt(visit, cart);
			} else {
				Toast.makeText( CarryoutActivity.this, "No Order to Print", Toast.LENGTH_LONG ).show();
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void printCarryOutReceipt(final CarryOutVisit visit, CartItems cartItems) {
		Receipt receipt = new CarryOutReceipt(visit, ++printRequestCounter, cartItems.coupon_code, cartItems.special_requests);

		visit.setOrderStatus("PRINTING");
		carryoutLoader.updateStatus(visit.getId(), "PRINTING");
		carryoutAdapter.notifyDataSetChanged();
		
//		PrintRequest request = new PrintRequest(this, "carryout_printer", receipt.getPrintList());
//		request.execute(contentManager, new PrintRequestListener(visit));
		PrintRequestAsync request = new PrintRequestAsync(this, "carryout_printer", new PrintRequestAsync.PrintListener() {
			@Override
			public void onSuccess() {
				if(visit!=null && visit.getId()!=null) {
					visit.setOrderStatus("PRINTED");
					carryoutLoader.updateStatus(visit.getId(), "PRINTED");
					System.out.println("carryout status -> printed");
				}
			}
			
			@Override
			public void onFailure(String reason) {
				if(visit!=null && visit.getId()!=null) {
					int printerrors = visit.getPrintFailures()+1;
					visit.setOrderStatus("PRINT ERROR " + printerrors);
					visit.setPrintFailures(printerrors);
					carryoutLoader.update(visit);
					System.out.println("carryout status -> " + visit.getOrderStatus());
					carryoutAdapter.notifyDataSetChanged();
					if(printerrors==3) {
//						Crashlytics.logException(e.getCause());
					}
					Toast.makeText(CarryoutActivity.this, "There was an error printing receipt: " + reason, Toast.LENGTH_SHORT).show();
				}
			}
		});
		request.execute(receipt.getPrintList());
	}

//	private class PrintRequestListener extends BaseListener<Boolean> {
//		CarryOutVisit visit;
//		
//		public PrintRequestListener(CarryOutVisit visit) {
//			this.visit = visit;
//		}
//		@Override
//		public void onRequestFailure(SpiceException arg0) {
//			if(visit!=null && visit.getId()!=null) {
//				int printerrors = visit.getPrintFailures()+1;
//				visit.setOrderStatus("PRINT ERROR " + printerrors);
//				visit.setPrintFailures(printerrors);
//				carryoutLoader.update(visit);
//				System.out.println("carryout status -> " + visit.getOrderStatus());
//				carryoutAdapter.notifyDataSetChanged();
//				if(printerrors==3) {
////					Crashlytics.logException(e.getCause());
//				}
//				Toast.makeText(CarryoutActivity.this, "There was an error printing receipt", Toast.LENGTH_SHORT).show();
//			}
//		}
//
//		@Override
//		public void onRequestSuccess(Boolean arg0) {
//			visit.setOrderStatus("PRINTED");
//			carryoutLoader.updateStatus(visit.getId(), "PRINTED");
//		}
//		
//	}
	
	public void deleteCarryoutVisit(CarryOutVisit visit) {
		DeleteCarryOutVisitRequest request = new DeleteCarryOutVisitRequest(carryoutLoader, tenantID, visit.getId());
		request.execute(contentManager, null);
	}

	public void textCarryOut(CarryOutVisit carryOutVisit, String message) {
		if(message!=null && message.length()>0) {
			TextPatronRequest request = new TextPatronRequest(tenantID, locationID, carryOutVisit.getId(), message);
			request.execute(contentManager, new PageCarryoutRequestListener());
		}
	}

	public void pageCarryout(CarryOutVisit carryOutVisit) {
		PageCarryOutRequest request = new PageCarryOutRequest(tenantID, locationID, carryOutVisit.getId(), "Your carry out order is now ready to be picked up.");
		request.execute(contentManager, new PageCarryoutRequestListener());
	}

	private class PageCarryoutRequestListener extends BaseListener<Void> {
		@Override
		public void onFailure(SpiceException e) {
			Toast.makeText( CarryoutActivity.this, "Error paging customer: " + e.getMessage(), Toast.LENGTH_LONG ).show();
		}

		@Override
		public void onRequestSuccess(Void arg0) {
			Toast.makeText( CarryoutActivity.this, "“Customer Paged!", Toast.LENGTH_LONG).show();
		}

	}
	
	/**
	 * playNotification is using a more complex prepare/setDataSource/prepare instead of create
	 * so that we can set the stream type to notification which makes the volume button function
	 * more natural
	 */
	public void playNotification(int notificationID) {
		AssetFileDescriptor file  = null;
		try {
			file = getResources().openRawResourceFd(notificationID);
			MediaPlayer mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
			mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
			mediaPlayer.prepare();

			mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer mp) {
					mp.start();
				}
			});
			mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
				
				@Override
				public void onCompletion(MediaPlayer mp) {
	            	if(mp!=null && !mp.isPlaying()) {
	            		mp.release();
	            		mp = null;
	            	}
				}
			});

//			mediaPlayer.start();
        } catch (Exception e) {
        	e.printStackTrace();
        	// ignore audio errors for now.
        } finally {
        	if(file!=null) {
        		try {
					file.close();
				} catch (IOException e) {
				}
        		file = null;
        	}
        }
	}


	public void initLoader(final int loaderId, final Bundle args, final LoaderManager.LoaderCallbacks<Cursor> callbacks) {
		final LoaderManager loaderManager = getLoaderManager();
	    final Loader<Cursor> loader = loaderManager.getLoader(loaderId);
	    if (loader != null && loader.isReset()) {
	        loaderManager.restartLoader(loaderId, args, callbacks);
	    } else {
	        loaderManager.initLoader(loaderId, args, callbacks);
	    }
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Loader<Cursor> loader = null;
		switch(id) {
		case CARRYOUT_LOADER:
			loader = new CarryoutLoader(this, db);
			break;
		}

		return loader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		switch(loader.getId()) {
		case CARRYOUT_LOADER:
			this.carryoutLoader=(CarryoutLoader)loader;
			carryoutAdapter.changeCursor(cursor);
			break;
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		switch(loader.getId()) {
		case CARRYOUT_LOADER:
			carryoutAdapter.changeCursor(null);
			break;
		}
	}

	@Override
	protected void onPause() {
		if(networkStatus!=null) {
			networkStatus.setStatusChangedListener(null);
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		if(networkStatus==null) {
			networkStatus = new NetworkStatus();
		}

		if(networkStatus!=null) {
			networkStatus.setStatusChangedListener(this);
		}
		super.onResume();
	}

	@Override
	public void toError(long uptime) {
		if(networkStatusIndicator!=null) {
			networkStatusIndicator.setColorFilter(networkStatus.getColor());
		}
	}

	@Override
	public void toSuccess(long downtime) {
		if(networkStatusIndicator!=null) {
			networkStatusIndicator.setColorFilter(networkStatus.getColor());
		}
	}

}
