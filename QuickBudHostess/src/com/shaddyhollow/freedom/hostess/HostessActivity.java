package com.shaddyhollow.freedom.hostess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.shaddyhollow.quickbud.R;
import com.shaddyhollow.freedom.dinendashhostess.printer.DineInReceipt;
import com.shaddyhollow.freedom.dinendashhostess.printer.PrintRequestAsync;
import com.shaddyhollow.freedom.dinendashhostess.printer.Receipt;
import com.shaddyhollow.freedom.dinendashhostess.requests.AddSeatedVisitRequest;
import com.shaddyhollow.freedom.dinendashhostess.requests.DeleteSeatedVisitRequest;
import com.shaddyhollow.freedom.dinendashhostess.requests.GetCartItemsRequest;
import com.shaddyhollow.freedom.dinendashhostess.requests.PagePatronRequest;
import com.shaddyhollow.freedom.dinendashhostess.requests.RemoveQueuedVisitLocalRequest;
import com.shaddyhollow.freedom.dinendashhostess.requests.RemoveQueuedVisitRemoteRequest;
import com.shaddyhollow.freedom.dinendashhostess.requests.RemoveQueuedVisitRemoteRequest.PatronRemovedCallback;
import com.shaddyhollow.freedom.dinendashhostess.requests.TextPatronRequest;
import com.shaddyhollow.freedom.floorplans.FloorplanAdapter;
import com.shaddyhollow.freedom.hostess.dialogs.ChooseMultipleServersDialogFragment;
import com.shaddyhollow.freedom.hostess.dialogs.ChoosePatronDialogFragment;
import com.shaddyhollow.freedom.hostess.dialogs.ChooseSingleServerDialogFragment;
import com.shaddyhollow.freedom.hostess.dialogs.IntegerInputDialog;
import com.shaddyhollow.freedom.sectionplans.SectionPlanAdapter;
import com.shaddyhollow.freedom.servers.ServerManagerActivity;
import com.shaddyhollow.freedom.tables.TablesAdapter;
import com.shaddyhollow.home.UpdateActivity;
import com.shaddyhollow.quickbud.Config;
import com.shaddyhollow.quickbud.DataResetUtil;
import com.shaddyhollow.quickbud.FlurryEvents;
import com.shaddyhollow.quickbud.datastore.CarryoutLoader;
import com.shaddyhollow.quickbud.datastore.DatabaseHelper;
import com.shaddyhollow.quickbud.datastore.QueuedVisitLoader;
import com.shaddyhollow.quickbud.datastore.SectionPlanFactory;
import com.shaddyhollow.quickbud.datastore.ServerFactory;
import com.shaddyhollow.quickbud.datastore.TableFactory;
import com.shaddyhollow.quicktable.models.CartItems;
import com.shaddyhollow.quicktable.models.Floorplan;
import com.shaddyhollow.quicktable.models.QueuedVisit;
import com.shaddyhollow.quicktable.models.SeatedVisit;
import com.shaddyhollow.quicktable.models.Section;
import com.shaddyhollow.quicktable.models.SectionPlan;
import com.shaddyhollow.quicktable.models.Server;
import com.shaddyhollow.quicktable.models.Table;
import com.shaddyhollow.quicktable.models.Table.Status;
import com.shaddyhollow.robospice.BaseListener;
import com.shaddyhollow.updatechecker.UpdateChecker;
import com.shaddyhollow.util.FileOperations;
import com.shaddyhollow.util.SingleChoiceDialogFragment;
import com.shaddyhollow.util.SingleChoiceDialogFragment.OnItemSelectedListener;

public class HostessActivity extends CarryoutActivity implements ActionBar.TabListener, 
																	  OnItemSelectedListener, 
																	  CompoundButton.OnCheckedChangeListener, 
																	  LoaderManager.LoaderCallbacks<Cursor>
																	  {
	private final static int CHANGEPLAN = 101;
	private final static int UPDATESERVERS = 102; 
	public final static int HOSTESS_RESULT = 4100;
	
	int nextSection = 0;
    int printFailure = 0;
    private static final String PREF_ACTIVE_PLAN_ID = "active_planid";
    
	private DatabaseHelper db=null;
	public QueuedVisitLoader patronsLoader=null;
	public QueuedPatronsAdapter patronsAdapter = null;
	
	private final static int PATRON_LOADER = 100;
	private final static int CARRYOUT_LOADER = 101;
	
	Handler periodicUpdatesHandler = new Handler();
	SectionPlan activeArea = null;

	private int openTableCount;
	private int throttleCount;
	private int queuedPatronCount;
	
	private DiningSectionsAdapter sectionsAdapter;
	private TablesAdapter tablesAdapter;
	private FloorplanAdapter floorplanAdapter;
	private ServerAdapter serverAdapter;
	
	private boolean isCarryoutEnabled = true;
	
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
		actionBar.setIcon(R.drawable.quickbud_image);

		locationID = (Integer)getIntent().getExtras().get("LOCATIONID");
		tenantID = (Integer)getIntent().getExtras().get("TENANTID");

		db=new DatabaseHelper(this);
		initLoader(PATRON_LOADER, null, this);
		initLoader(CARRYOUT_LOADER, null, this);
		//	    getLoaderManager().initLoader(PATRON_LOADER, null, this);
		//	    getLoaderManager().initLoader(CARRYOUT_LOADER, null, this);

		resetAdapters();
		refreshPlanInfo();

		mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

		patronsAdapter = new QueuedPatronsAdapter(this);
		patronsAdapter.registerDataSetObserver(new DataSetObserver() {

			@Override
			public void onChanged() {
				HostessActivity.this.updateQueuedPatronCount();
			}

			@Override
			public void onInvalidated() {
				HostessActivity.this.updateQueuedPatronCount();
			}

		});

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
//		seatThrottle = new SeatThrottle(24, 10);
//
//		setThrottleCount(10, 0, 24);	

		networkStatusIndicator = (ImageView) findViewById(R.id.network_status);
		networkStatusIndicator.setVisibility(View.VISIBLE);

//		queuedPatronCountView = (TextView)findViewById(R.id.queued_count);
//		updateQueuedPatronCount();

//		performPeriodicUpdates();
		periodicUpdatesHandler.postDelayed(periodicUpdatesRunnable, 20000);

		updateDetails(Mode.INFO);
	}

	protected void setupViewPager() {
		final ActionBar actionBar = getActionBar();
		actionBar.setIcon(R.drawable.quickbud_image);

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setOffscreenPageLimit(5);
		
		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						int fixedpages = (isCarryoutEnabled) ? 2 : 1; //carry out and queued patrons

						actionBar.setSelectedNavigationItem(position);
						if(mode == Mode.PATRON_SEATING) {
							updateDetails(Mode.PATRON_SEATING);
						} else if(position==fixedpages-2) {
							updateDetails(Mode.CARRYOUT_SINGLE);
						} else if(position==fixedpages-1) {
							updateDetails(Mode.PATRON_SINGLE);
						} else {
							if(tablesAdapter.getCurrentSelection()==null) {
								updateDetails(Mode.SECTION_LIST);
							} else {
								updateDetails(Mode.TABLE_SINGLE);
							}
						}
					}
				});
		
		 // For each of the sections in the app, add a tab to the action bar.
		 for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		 }
	}

	@Override
	public boolean isCarryoutOnly() {
		return false;
	}
	
	public void updateOpenTableCount() {
		int openTables = 0;
		List<Table> tables = tablesAdapter.getAll();
		for(Table table : tables) {
			Section section = sectionsAdapter.getItemByID(table.section_id);
			if(section!=null && section.open){ 
    			if (table.getState() == Table.Status.OPEN && table.seated_visit == null) {
        			openTables++;
    			}
    		}
    	}	
//    	setOpenTableCount(openTables);
   	}

	public int getQueuedPatronCount() {
		return queuedPatronCount;
	}

	public void updateQueuedPatronCount() {
		if(patronsAdapter!=null) {
			this.queuedPatronCount = patronsAdapter.getCount();
		} else {
			this.queuedPatronCount = 0;
		}
		queuedPatronCountView.setText("Queued Patrons: " + Integer.toString(queuedPatronCount));
	}

	private void resetAdapters() {
		tablesAdapter = new TablesAdapter(HostessActivity.this);

		floorplanAdapter = new FloorplanAdapter(this);
		for(Floorplan floorplan : floorplanAdapter.getAll()) {
			if(floorplan.tables!=null) {
				tablesAdapter.addAll(Arrays.asList(floorplan.tables));
			}
		}
		
		serverAdapter = new ServerAdapter(this);
		serverAdapter.addAll(ServerFactory.getInstance().bulkRead(null));
	
		// in refreshplaninfo, we'll replace the section's table list with objects from tablesadapter
//		sectionsAdapter = new DiningSectionsAdapter(this, this);
//		sectionsAdapter.registerDataSetObserver(new DataSetObserver() {
//			@Override
//			public void onInvalidated() {
//				updateOpenTableCount();
//				if(seatThrottle!=null) {
//					setThrottleCount(seatThrottle.getPeriod(), seatThrottle.getCount(), seatThrottle.getMax());
//				}
//				super.onInvalidated();
//			}
//
//			@Override
//			public void onChanged() {
//				updateOpenTableCount();
//				if(seatThrottle!=null) {
//					setThrottleCount(seatThrottle.getPeriod(), seatThrottle.getCount(), seatThrottle.getMax());
//				}
//				super.onChanged();
//			}
//		});

	}
	
	private void refreshPlanInfo() {
		// refresh active plan
		SectionPlanAdapter areasAdapter = new SectionPlanAdapter(HostessActivity.this);
		SharedPreferences prefs = getPreferences(MODE_PRIVATE); 
		String uuidString = "";
		try {
			uuidString = prefs.getString(HostessActivity.PREF_ACTIVE_PLAN_ID, "");
		} catch (Exception e) {
			// possibly invalid pref id based on old version
			Editor editor = prefs.edit();
			editor.putString(HostessActivity.PREF_ACTIVE_PLAN_ID, areasAdapter.getItem(0).getId().toString());
			editor.commit();
		}
		if(uuidString.length()>0) {
			UUID activeAreaID = UUID.fromString(uuidString);
			activeArea = areasAdapter.getItemByID(activeAreaID);
		}
		if(activeArea==null) {
			if(areasAdapter.getCount()==0) {
				Toast.makeText(this, "Please build a floorplan and a section plan prior to using this app.", Toast.LENGTH_LONG).show();
				finish();
				return;
			}
			activeArea=areasAdapter.getItem(0);
		}
		
		// clear all tables.section_id
		List<Table> allTables = tablesAdapter.getAll();
		for(Table table : allTables) {
			table.section_id = null;
		}
		

		// refresh sections from active plan
		// make sure that the tables in the section object are the same as from the tablesadapter
		
		sectionsAdapter.clearSelectionPosition();
		sectionsAdapter.clear();
		if(activeArea.sections!=null) {
			List<Section> orderedSections = new ArrayList<Section>();
			orderedSections.addAll(Arrays.asList(activeArea.sections));
			Collections.sort(orderedSections, new Comparator<Section>() {
				@Override
				public int compare(Section lhs, Section rhs) {
					String lhsComparable = lhs.dailysortorder + "." + lhs.name;
					String rhsComparable = rhs.dailysortorder + "." + rhs.name;
					return lhsComparable.compareTo(rhsComparable);
				}
				
			});
			for(Section section : orderedSections) {
				if(section.tables!=null) {
					List<Table> updatedTables = new ArrayList<Table>();
					for(Table table : section.tables) {
						Table tableFromTablesAdapter = tablesAdapter.getItemByID(table.id);
						tableFromTablesAdapter.section_id = section.id;
						updatedTables.add(tableFromTablesAdapter);
					}
					section.tables = updatedTables.toArray(new Table[0]);
				}
				List<UUID> serverIDs = SectionPlanFactory.getInstance().readServerIDsForSection(section.getId());
				if(serverIDs!=null) {
					List<Server> updatedServers = new ArrayList<Server>();
					for(UUID server_id : serverIDs){
						Server serverFromServerAdapter = serverAdapter.getItemByID(server_id);
						updatedServers.add(serverFromServerAdapter);
					}
					section.setServers(updatedServers.toArray(new Server[0]));
				}
				sectionsAdapter.add(section);
			}
		}
		
		// load temporary sections
		Map<UUID, UUID> temporaryTableSections = SectionPlanFactory.getInstance().readTemporaryTableAssignments();
		for(UUID tableID : temporaryTableSections.keySet()) {
			Table table = tablesAdapter.getItemByID(tableID);
			Section oldSection = sectionsAdapter.getItemByID(table.section_id);
			if(oldSection!=null) {
				oldSection.removeTable(table);
			}
			Section newSection = sectionsAdapter.getItemByID(temporaryTableSections.get(tableID));
			if(newSection!=null) {
				newSection.addTable(table);
			} else {
				// plan does not have the section specified by the temp table
				if(oldSection!=null) {
					oldSection.addTable(table);
				}
			}
		}

		sectionsAdapter.notifyDataSetChanged();
		tablesAdapter.notifyDataSetChanged();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_hostess, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.action_plan:
			SharedPreferences prefs = getPreferences(MODE_PRIVATE); 
			UUID activeAreaID = UUID.fromString(prefs.getString(HostessActivity.PREF_ACTIVE_PLAN_ID, UUID.randomUUID().toString()));

			List<String> planNames = new ArrayList<String>();
			int activePosition = 0;
			SectionPlanAdapter areaAdapter = new SectionPlanAdapter(HostessActivity.this);
			for(int i=0;i<areaAdapter.getCount();i++) {
				SectionPlan area = areaAdapter.getItem(i);
				planNames.add(area.name);
				if(area.id.equals(activeAreaID)) {
					activePosition = i;
				}
			}
	        SingleChoiceDialogFragment dlg = new SingleChoiceDialogFragment(HostessActivity.this, CHANGEPLAN);
	        dlg.setListener(this);
			dlg.setTitle("Plan Selector");
			dlg.setCancelable(true);
			dlg.setMessage("Select plan to activate");
			dlg.setChoices(planNames);
			dlg.setSelected(activePosition);
			dlg.setPositiveText("OK");
			dlg.setNegativeText("CANCEL");
			dlg.show(getFragmentManager(), "planchange");
			break;
		case R.id.action_servers: {
	    	FlurryAgent.logEvent(FlurryEvents.HOSTESS_MANAGESERVERS.name());
	        Intent intent = new Intent(this, ServerManagerActivity.class);
	        startActivityForResult(intent, HostessActivity.UPDATESERVERS);
			}
        	break;
		case R.id.action_dailyreset: {
			DataResetUtil resetUtil = new DataResetUtil(this, contentManager);
			resetUtil.performDailyReset();
			patronsAdapter.clearSelectionPosition();
			}
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case HostessActivity.UPDATESERVERS:
			if(serverAdapter==null) {
				serverAdapter = new ServerAdapter(this);
			}
			serverAdapter.clear();
			serverAdapter.addAll(ServerFactory.getInstance().bulkRead(null));

			break;
		/*case UpdateActivity.UPDATE_RESULT:
			UpdateChecker checker = new UpdateChecker(this, true);
			if(resultCode == Activity.RESULT_OK) {
				checker.downloadAndInstall(Config.getAPKURL());
			} else if(resultCode == UpdateActivity.NO_UPDATE_FOUND) {
				Toast.makeText(this, "There is no update available", Toast.LENGTH_SHORT).show();
				finish();
			} else if(resultCode == Activity.RESULT_CANCELED) {
				finish();
			}
			break;*/
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void updateDetails(Mode mode) {
		long startTime = System.currentTimeMillis();
		this.mode = mode;
		Fragment detailFragment = null;
		Fragment activeFragment = ((SectionsPagerAdapter)mSectionsPagerAdapter).findFragment(mViewPager.getCurrentItem());

		if(mode==Mode.TABLE_SINGLE && tablesAdapter.getCurrentSelection()!=null) {
			SeatedPatronsFragment fragment = (SeatedPatronsFragment)activeFragment;
			sectionsAdapter = fragment.getSectionsAdapter();
			tablesAdapter = fragment.getTablesAdapter();
			detailFragment = DiningTableFragment.newInstance(mode, sectionsAdapter, tablesAdapter, activeArea.getId());
		} else if(mode==Mode.PATRON_SINGLE) {
			detailFragment = DiningPatronFragment.newInstance(mode, patronsAdapter, locationID, tenantID);
		} else if(mode==Mode.PATRON_MOVE) {
			SeatedPatronsFragment fragment = (SeatedPatronsFragment)activeFragment;
			sectionsAdapter = fragment.getSectionsAdapter();
			tablesAdapter = fragment.getTablesAdapter();
			detailFragment = DiningTableFragment.newInstance(mode, sectionsAdapter, tablesAdapter, activeArea.getId());
		} else if(mode==Mode.PATRON_SEATING) {
			detailFragment = DiningPatronFragment.newInstance(mode, patronsAdapter, locationID, tenantID);
		} else if(mode==Mode.SECTION_DETAIL || mode==Mode.SECTION_LIST) {
			if(activeFragment instanceof SeatedPatronsFragment) {
				SeatedPatronsFragment fragment = (SeatedPatronsFragment)activeFragment;
				sectionsAdapter = fragment.getSectionsAdapter();
				tablesAdapter = fragment.getTablesAdapter();
				tablesAdapter.clearSelectionPosition();
				detailFragment = DiningSectionsFragment.newInstance(sectionsAdapter, locationID);
			} else {
				updateDetails(Mode.INFO);
			}
		} else if(mode==Mode.COMBINE_TABLES) {
			SeatedPatronsFragment fragment = (SeatedPatronsFragment)activeFragment;
			sectionsAdapter = fragment.getSectionsAdapter();
			tablesAdapter = fragment.getTablesAdapter();
			detailFragment = DiningTableFragment.newInstance(mode, sectionsAdapter, tablesAdapter, activeArea.getId());
		} else if(mode==Mode.CARRYOUT_SINGLE) {
			detailFragment = DiningCarryoutFragment.newInstance(mode, carryoutAdapter, locationID, tenantID);
		} else { // mode==Mode.INFO
			if(sectionsAdapter!=null) {
				sectionsAdapter.clearSelectionPosition();
			}
			if(tablesAdapter!=null) {
				tablesAdapter.clearSelectionPosition();
			}
			detailFragment = DiningSectionsFragment.newInstance(sectionsAdapter, locationID);
		}

		
		if(detailFragment!=null) {
			FragmentTransaction ft = getFragmentManager().beginTransaction();

			ft.replace(R.id.list, detailFragment);
			ft.commit();
		}
		
		Log.v("HOSTESS_ACTIVITY", "update details took " + (System.currentTimeMillis()-startTime));
	}

	public void seatFromQueue(Table table, final int position) {
		patronsAdapter.clearSelectionPosition();
		ChoosePatronDialogFragment newFragment = ChoosePatronDialogFragment.newInstance(patronsAdapter, table, new ChoosePatronDialogFragment.Listener() {

			@Override
			public void onPatronSelected(QueuedVisit visit, Table table) {
				if (visit == null) {
					seatAnonymous(table, position);
				} else {
					patronsAdapter.setSelection(visit);
					finishSeatingPatron(table.group_id);
				}
			}
			
		});
		
		newFragment.show(getFragmentManager(), "dialog");
	}
	
	private void seatAnonymous(final Table table, int position) {
		IntegerInputDialog newFragment = IntegerInputDialog.newInstance("Number in Party", new IntegerInputDialog.Listener() {

			@Override
			public void onValueSelected(int value) {
				QueuedVisit patron = new QueuedVisit();
				patron.setId(UUID.randomUUID());
				patron.setName("Guest");
				patron.setParty_size(value);
				seatPatron(table, null, null, patron);
				updateDetails(Mode.SECTION_LIST);
				tablesAdapter.notifyDataSetChanged();
			}
			
		});
		
		newFragment.show(getFragmentManager(), "dialog");
	}

	public void prepPatronForSeating(final QueuedVisit queuedVisit) {
		if(tablesAdapter!=null) {
			tablesAdapter.clearSelectionPosition();
		}
		updateDetails(Mode.PATRON_SEATING);
		mViewPager.setCurrentItem(isCarryoutEnabled ? 2 : 1);
	}

	public void movePatron(Table srcTable, Table destTable) {
		QueuedVisit patron = new QueuedVisit();
		patron.setId(srcTable.seated_visit.queued_visit_id);
		patron.setVisit_id(srcTable.seated_visit.visit_id);
		patron.setName(srcTable.seated_visit.name);
		patron.setSpecialRequests(srcTable.seated_visit.comment);
		patron.setParty_size(srcTable.seated_visit.party_size);
		
		Server srcServer = serverAdapter.getItemByID(srcTable.seated_visit.server_id); 
		seatPatron(destTable, srcTable, srcServer, patron); 
		updateDetails(Mode.TABLE_SINGLE);
	}
	
	// TODO REFACTOR!
	///////// methods copied from DingngTableFragment 
	private void splitTables(Table table) {
		List<Table> combinedTables = getCombinedTables(table);
		for(Table curTable : combinedTables) {
			curTable.group_id = curTable.id;
			TableFactory.getInstance().update(curTable);
		}
		tablesAdapter.notifyDataSetChanged();
	}

	private void clearTable(Table table) {
		List<Table> combinedTables = getCombinedTables(table);
		
		deleteSeatedVisit(table.seated_visit);
		
		for(Table curTable : combinedTables) {
			curTable.seated_visit = null;
			curTable.setState(Table.Status.OPEN);
		}
		
		TableFactory.getInstance().update(table);
		tablesAdapter.notifyDataSetChanged();
		sectionsAdapter.notifyDataSetChanged();

		splitTables(table);
		//TODO call ClearTableRequest
	}
	/////// end copy

	public void finishSeatingPatron(UUID table_id) {
		final QueuedVisit patron = patronsAdapter.getSelection();
		
		if(patron==null) {
			updateDetails(Mode.SECTION_LIST);
			return;
		}
		
		//TODO call server function to seat patron (we need tables saved for that function to work)
		Table table = tablesAdapter.getItemByID(table_id);
		seatPatron(table, null, null, patron);
	}

	private List<Table> getCombinedTables(Table table) {
		ArrayList<Table> combinedTables = new ArrayList<Table>();
		for(Table curTable : tablesAdapter.getAll()) {
			if(curTable.group_id.equals(table.group_id)) {
				combinedTables.add(curTable);
			}
		}
		return combinedTables;
	}

	public void printDineInReceipt(Table table, UUID visitId) {
		try {
			if(visitId!=null) {
				GetCartItemsRequest request = new GetCartItemsRequest(tenantID, locationID, visitId);
				request.execute(contentManager, new GetCartItemsRequestListener(table, visitId));
			} else {
				Toast.makeText(this, "No ticket to print", Toast.LENGTH_SHORT).show();
			}
		} catch (NullPointerException e) {
			Toast.makeText(this, "Problem printing ticket", Toast.LENGTH_SHORT).show();
		}
	}
	
	private class GetCartItemsRequestListener extends BaseListener<CartItems> {
		private Table table;
		private UUID visitId;
		
		public GetCartItemsRequestListener(Table table, UUID visitId) {
			this.table = table;
			this.visitId = visitId;
		}

		@Override
		public void onFailure(SpiceException e) {
	        Toast.makeText( HostessActivity.this, "Error getting cart: " + e.getMessage(), Toast.LENGTH_LONG ).show();
		}

		@Override
		public void onRequestSuccess(CartItems cart) {
			if(cart==null) {
				return;
			}
			if(cart.cart_items != null && cart.cart_items.length > 0) {
				printDineInReceipt(table, visitId,  cart);
			} else {
		        Toast.makeText( HostessActivity.this, "No Order to Print", Toast.LENGTH_LONG ).show();
			}
		}
		
	}

	@SuppressWarnings("unchecked")
	private void printDineInReceipt(Table table, UUID visitId, CartItems cart) {
		Receipt receipt = new DineInReceipt(table, cart);

		PrintRequestAsync request = new PrintRequestAsync(this, "dinein_printer", new PrintRequestAsync.PrintListener() {
			@Override
			public void onSuccess() {
			}
			
			@Override
			public void onFailure(String reason) {
				Toast.makeText(HostessActivity.this, "There was an error printing receipt: " + reason, Toast.LENGTH_SHORT).show();
			}
		});
		request.execute(receipt.getPrintList());
	}


	private boolean seatPatron(final Table destTable, final Table srcTable, final Server srcServer, final QueuedVisit patron) {
		Server destServer = null;
		boolean patronSeated = true;
		
		final Section section = sectionsAdapter.getItemByID(destTable.section_id);
		Server[] servers = section.getServers();

		if(servers!=null && servers.length==1) {
			destServer = servers[0];
		}

		if(destServer!=null) {
			seatPatron(destTable, destServer, patron, srcTable, srcServer);
		} else {
			if(servers==null || servers.length==0) {
				ChooseSingleServerDialogFragment newFragment = ChooseSingleServerDialogFragment.newInstance(serverAdapter.getAll(), new ChooseSingleServerDialogFragment.Listener() {
					@Override
					public void onServerSelected(Server selectedServer) {
						if(selectedServer==null) {
							return;
						}
						section.setServers(new Server[] {selectedServer});
						sectionsAdapter.notifyDataSetChanged();
						SectionPlanFactory.getInstance().updateServers(section);
						HostessActivity.this.seatPatron(destTable, selectedServer, patron, srcTable, srcServer);
					}
					
					@Override
					public void onCancel() {
						patronsAdapter.clearSelectionPosition();
						updateDetails(Mode.SECTION_LIST);
					}
				});
				
				newFragment.show(getFragmentManager(), "dialog");
				patronSeated = false;
			} else {
				ChooseSingleServerDialogFragment newFragment = ChooseSingleServerDialogFragment.newInstance(Arrays.asList(servers), new ChooseSingleServerDialogFragment.Listener() {
					@Override
					public void onServerSelected(Server selectedServer) {
						if(selectedServer==null) {
							return;
						}
						HostessActivity.this.seatPatron(destTable, selectedServer, patron, srcTable, srcServer);
					}

					@Override
					public void onCancel() {
						patronsAdapter.clearSelectionPosition();
						updateDetails(Mode.SECTION_LIST);
					}
				});
				
				newFragment.show(getFragmentManager(), "dialog");
			}
		}
		return patronSeated;
	}
	
	private void seatPatron(Table table, Server server, QueuedVisit patron, Table srcTable, Server srcServer) {
		UUID table_id = table.group_id;
		int partySize = patron.getParty_size();
		List<Table> combinedTables = getCombinedTables(tablesAdapter.getItemByID(table_id));
		
		// patron is new, not moving
		if(srcTable==null) {
			seatThrottle.addParty(partySize);
		}
		
		if(partySize==1 || partySize>7) {
			server.colorstate = Color.RED;
			serverAdapter.update(server);
			serverAdapter.notifyDataSetChanged();
		}
		
		if(srcTable!=null) {
			clearTable(srcTable);
		}
		if(srcServer!=null) {
			srcServer.setTables_served(srcServer.getTables_served()-1);
			ServerFactory.getInstance().update(srcServer);
		}
		server.setTables_served(server.getTables_served()+1);
		ServerFactory.getInstance().update(server);

		SeatedVisit seatedVisit = new SeatedVisit();
		seatedVisit.id = UUID.randomUUID();
		seatedVisit.queued_visit_id = patron.getId();
		seatedVisit.visit_id = patron.getVisit_id();
		seatedVisit.name = patron.getName();
		seatedVisit.party_size = partySize;
		seatedVisit.server_id = server.getId();
		seatedVisit.seating_time = new String();
		seatedVisit.comment = patron.getSpecialRequests();
		seatedVisit.order_in = patron.isOrder_in();
		for(Table curTable : combinedTables) {
			curTable.seated_visit = seatedVisit;
			curTable.setState(Status.OPEN);
			TableFactory.getInstance().createSeatedVisit(curTable.getId(), seatedVisit);
		}
		
		AddSeatedVisitRequest request = new AddSeatedVisitRequest(Config.getTenantID(), Config.getLocationID(), patron, seatedVisit);
		request.execute(contentManager, new AddSeatedVisitRequestListener(seatedVisit));
		
		sectionsAdapter.markSectionServed(table.section_id);

		tablesAdapter.notifyDataSetChanged();
		sectionsAdapter.notifyDataSetChanged();

		if(patron.isOrder_in()) {
			printDineInReceipt(table, patron.getVisit_id());
			playNotification(R.raw.pixiedust);
		}
		if(patronsAdapter.getSelection()!=null) {
			patronsLoader.markRemoved(patronsAdapter.getSelection().getId());
			patronsAdapter.clearSelectionPosition();
		}
		patronsAdapter.notifyDataSetChanged();
		updateDetails(Mode.SECTION_LIST);
	}
	
	private class AddSeatedVisitRequestListener extends BaseListener<SeatedVisit> {
		SeatedVisit seatedVisit;
		
		public AddSeatedVisitRequestListener(SeatedVisit seatedVisit) {
			this.seatedVisit = seatedVisit;
		}
		
		@Override
		public void onFailure(SpiceException e) {
//			e.printStackTrace();
		}

		@Override
		public void onRequestSuccess(SeatedVisit obj) {
			if(seatedVisit!=null && obj!=null) {
				seatedVisit.id = obj.id;
				seatedVisit.seating_time = obj.seating_time;
			}
		}
	}
	
	private class RemoveQueuedVisitRequestListener extends BaseListener<Void> {

		@Override
		public void onFailure(SpiceException e) {
			if(patronsAdapter!=null) {
				patronsAdapter.notifyDataSetChanged();
			}
		}

		@Override
		public void onRequestSuccess(Void arg0) {
			patronsAdapter.clearSelectionPosition();
			patronsAdapter.notifyDataSetChanged();
		}
		
	}

	private Runnable periodicUpdatesRunnable = new Runnable() {
		@Override
		public void run() {
//			performPeriodicUpdates();

			periodicUpdatesHandler.postDelayed(periodicUpdatesRunnable, 20000);
		}
	};

//	private void performPeriodicUpdates() {
//		setThrottleCount(seatThrottle.getPeriod(), seatThrottle.getCount(), seatThrottle.getMax());
//	}
		
	public int getOpenTableCount() {
		return openTableCount;
	}

//	public void setOpenTableCount(int openTableCount) {
//		this.openTableCount = openTableCount;
//		if(openTableCountView==null) {
//			openTableCountView = (TextView) findViewById(R.id.open_count);
//		}
//		openTableCountView.setText("Open Tables: " + Integer.toString(openTableCount));
//	}
	
//	public int getThrottleCount() {
//		return throttleCount;
//	}

//	public void setThrottleCount(int period, int throttleCount, int max) {
//		this.throttleCount = throttleCount;
//		throttleCountView.setTextColor((seatThrottle.getCount()<seatThrottle.getMax()) ? Color.BLACK : Color.RED);
//		throttleCountView.setText("Patrons in last " + Integer.toString(period) + " min: " + Integer.toString(throttleCount));
//	}
	
	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {
		SectionPlanAdapter areasAdapter = null;
		int fixedpages;
		
		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
			fixedpages = (isCarryoutEnabled) ? 2 : 1; //carry out and queued patrons
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = null;
			
			Bundle bundle = new Bundle();
			bundle.putString("LOCATIONID", String.valueOf(locationID));
			bundle.putString("TENANTID", String.valueOf(tenantID));

			int tabnum = position;
			if(!isCarryoutEnabled) {
				tabnum++;
			}
			switch(tabnum) {
			case 0:
				{
					fragment = CarryoutFragment.newInstance(tenantID, locationID, carryoutAdapter, contentManager);
				}
				break;
			case 1: 
				{
					fragment = QueuedPatronsFragment.newInstance(tenantID, locationID, patronsAdapter, contentManager);
				}
				break;
			default:
				{
					Floorplan floorplan = floorplanAdapter.getItem(position-fixedpages);
					TablesAdapter tablesOnPage = new TablesAdapter(HostessActivity.this);
					if(floorplan.tables!=null) {
						for(Table floorplanTable : floorplan.tables){
							tablesOnPage.add(tablesAdapter.getItemByID(floorplanTable.getId()));
						}
					}
					if(activeArea.sections!=null) {
						for(Section section : activeArea.sections) {
							if(section.tables!=null) {
								for(Table table : section.tables) {
									Table addedTable = tablesOnPage.getItemByID(table.id);
									if(addedTable!=null) {
										addedTable.section_id = section.id;
									}
								}
							}
						}
					}
					fragment = SeatedPatronsFragment.newInstance(HostessActivity.this, tablesAdapter, sectionsAdapter);
					((SeatedPatronsFragment)fragment).setArea(activeArea);
					bundle.putString(SeatedPatronsFragment.KEY_AREAID, String.valueOf(activeArea.id));
					bundle.putString(SeatedPatronsFragment.KEY_FLOORPLANID, String.valueOf(floorplan.id));
				}
				break;
			};
			fragment.setArguments(bundle);
			return fragment;
		}

		@Override
		public int getCount() {
			return floorplanAdapter.getCount()+fixedpages; // # of areas + queue fragment + carryout fragment
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			String title = "";
			int tabnum = position;
			if(!isCarryoutEnabled) {
				tabnum++;
			}
			switch(tabnum) {
			case 0: {
					title = getString(R.string.carryout_title).toUpperCase(l);
				} break;
			case 1: {
					title = getString(R.string.queued_patrons_title).toUpperCase(l);
				} break;
			default : {
					Floorplan floorplan = floorplanAdapter.getItem(position-(isCarryoutEnabled ? 2 : 1));
					title = floorplan.name.toUpperCase(l);
				} break;
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

	public void deleteSeatedVisit(SeatedVisit visit) {
		if(visit==null) {
			return;
		}
		DeleteSeatedVisitRequest request = new DeleteSeatedVisitRequest(Config.getTenantID(), visit.visit_id);
		request.execute(contentManager, null);
	}

	public void satParty(int partySize) {
		seatThrottle.addParty(partySize);
	}

	@Override
	public void OnItemSelected(int key, String selectedText, int position) {
		switch(key) {
		case CHANGEPLAN : 
			SectionPlanAdapter areaAdapter = new SectionPlanAdapter(this);
			for(SectionPlan area : areaAdapter.getAll()) {
				if(area.name.equals(selectedText)) {
					 SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
					 editor.putString(HostessActivity.PREF_ACTIVE_PLAN_ID, String.valueOf(area.getId()));
					 editor.commit();
					 break;
				}
			}
	    	FlurryAgent.logEvent(FlurryEvents.HOSTESS_CHANGEPLAN.name());
			refreshPlanInfo();
			break;
		}
	}

	public void selectServers(View view) {
		final Section section = sectionsAdapter.getItemByID((UUID)view.getTag());
		if(section==null) {
			return;
		}
		ArrayList<Server> currentServers = new ArrayList<Server>();
		if(section.getServers()!=null) {
			currentServers.addAll(Arrays.asList(section.getServers()));
		}
		ChooseMultipleServersDialogFragment newFragment = ChooseMultipleServersDialogFragment.newInstance(serverAdapter.getAll(), currentServers, new ChooseMultipleServersDialogFragment.Listener() {

			@Override
			public void onServersSelected(Server[] servers) {
				FlurryAgent.logEvent(FlurryEvents.SECTION_SERVERS.name());
				section.setServers(servers);
				sectionsAdapter.notifyDataSetChanged();
				SectionPlanFactory.getInstance().updateServers(section);
			}
			
		});
		
		newFragment.show(getFragmentManager(), "dialog");
	}
	
	public void resetServerTableCounts(final View view) {
		UUID serverID = (UUID)view.getTag();
		final Server currentServer = serverAdapter.getItemByID(serverID);
		
	    new AlertDialog.Builder(this)
        .setIcon(android.R.drawable.ic_dialog_alert)
        .setTitle("Reset Table Count")
        .setMessage("Are you sure you want to reset the table count for  " + currentServer.name + "?")
        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	        @Override
	        public void onClick(DialogInterface dialog, int which) {
				FlurryAgent.logEvent(FlurryEvents.SERVER_COUNTRESET.name());
	    		currentServer.setTables_served(0);
	    		currentServer.colorstate = 0;
	    		serverAdapter.update(currentServer);
	    		serverAdapter.notifyDataSetChanged();
	    		sectionsAdapter.notifyDataSetChanged();
	        }
	    })
	    .setNegativeButton("No", null)
	    .show();
	}
	
	public void resetSectionTableCounts(final View view) {
		UUID sectionID = (UUID)view.getTag();
		final Section currentSection = sectionsAdapter.getItemByID(sectionID);
		
	    new AlertDialog.Builder(this)
        .setIcon(android.R.drawable.ic_dialog_alert)
        .setTitle("Reset Table Count")
        .setMessage("Are you sure you want to reset the table counts for everyone in this section?")
        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	        @Override
	        public void onClick(DialogInterface dialog, int which) {
	    		Server[] servers = currentSection.getServers();
    			if(servers!=null) {
    				FlurryAgent.logEvent(FlurryEvents.SECTION_COUNTRESET.name());
    				for(Server server : servers) {
						server.setTables_served(0);
						server.colorstate = 0;
						serverAdapter.update(server);
			    		serverAdapter.notifyDataSetChanged();
    				}
	    		}
	    		sectionsAdapter.notifyDataSetChanged();
	        }
	    })
	    .setNegativeButton("No", null)
	    .show();

	}
	
	public void removePatron(QueuedVisit queuedVisit, PatronRemovedCallback callback) {
		//--------------------LOG DATA SAVING TO FILE -------------------------
		FileOperations.writeToFile(queuedVisit, true);
		
		RemoveQueuedVisitLocalRequest localRequest = new RemoveQueuedVisitLocalRequest(patronsLoader, locationID, queuedVisit.getId(), null);
		localRequest.execute(contentManager, new RemoveQueuedVisitRequestListener());

		RemoveQueuedVisitRemoteRequest remoteRequest = new RemoveQueuedVisitRemoteRequest(patronsLoader, tenantID, queuedVisit.getVisit_id(), null);
		remoteRequest.execute(contentManager, null);
	}

	public void textPatron(QueuedVisit queuedVisit, String message) {
		if(message!=null && message.length()>0) {
			TextPatronRequest request = new TextPatronRequest(tenantID, locationID, queuedVisit.getId(), message);
			request.execute(contentManager, new PagePatronRequestListener() );
		}
	}

	public void pagePatron(QueuedVisit queuedVisit) {
		if(queuedVisit.getWalkIn()){
			String msg = "Your table for "+ queuedVisit.getParty_size() + " at " + Config.location.getName() + " is ready!";
			TextPatronRequest request = new TextPatronRequest(tenantID, locationID, queuedVisit.getId(), msg);
			request.execute(contentManager, new PagePatronRequestListener() );
		}else{
			PagePatronRequest request = new PagePatronRequest(tenantID, locationID, queuedVisit.getId(),"Your Table is ready, Please see the hostess to get seated.");
			request.execute(contentManager, new PagePatronRequestListener() );
		}
	
	}
	
	private class PagePatronRequestListener extends BaseListener<Void> {
		@Override
		public void onFailure(SpiceException e) {
	        Toast.makeText( HostessActivity.this, "Error paging Patron: " + e.getMessage(), Toast.LENGTH_LONG ).show();
		}

		@Override
		public void onRequestSuccess(Void arg0) {
	        Toast.makeText( HostessActivity.this, "Patron Paged!", Toast.LENGTH_LONG).show();
		}
		
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		Section section = sectionsAdapter.getSelection();
		if(section!=null) {
			section.open = isChecked;
			if(activeArea!=null) {
				if(section.open){ 
					FlurryAgent.logEvent(FlurryEvents.SECTION_OPEN.name());
				} else {
		        	FlurryAgent.logEvent(FlurryEvents.SECTION_CLOSE.name());
				}
				SectionPlanFactory.getInstance().updateSection(section);
			}
			sectionsAdapter.notifyDataSetChanged();
		}
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Loader<Cursor> loader = null;
		switch(id) {
		case PATRON_LOADER:
			loader = new QueuedVisitLoader(this, db);
		    break;
		case CARRYOUT_LOADER:
			loader = new CarryoutLoader(this, db);
			break;
		}
	    
	    return loader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		switch(loader.getId()) {
		case PATRON_LOADER:
		    this.patronsLoader=(QueuedVisitLoader)loader;
		    patronsAdapter.changeCursor(cursor);
		    break;
		case CARRYOUT_LOADER:
			this.carryoutLoader=(CarryoutLoader)loader;
			carryoutAdapter.changeCursor(cursor);
			break;
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		switch(loader.getId()) {
		case PATRON_LOADER:
		    patronsAdapter.changeCursor(null);
		    break;
		case CARRYOUT_LOADER:
			carryoutAdapter.changeCursor(null);
			break;
		}
	}
	
}
