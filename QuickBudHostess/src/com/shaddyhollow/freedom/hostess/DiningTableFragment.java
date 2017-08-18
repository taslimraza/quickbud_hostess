package com.shaddyhollow.freedom.hostess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.shaddyhollow.quickbud.R;
import com.shaddyhollow.freedom.hostess.dialogs.IntegerInputDialog;
import com.shaddyhollow.freedom.hostess.dialogs.IntegerInputDialog.Listener;
import com.shaddyhollow.freedom.hostess.dialogs.TextInputDialog;
import com.shaddyhollow.freedom.hostess.dialogs.TextInputDialog.TextInputListener;
import com.shaddyhollow.freedom.sections.SectionsAdapter;
import com.shaddyhollow.freedom.tables.TablesAdapter;
import com.shaddyhollow.quickbud.FlurryEvents;
import com.shaddyhollow.quickbud.datastore.SectionPlanFactory;
import com.shaddyhollow.quickbud.datastore.ServerFactory;
import com.shaddyhollow.quickbud.datastore.TableFactory;
import com.shaddyhollow.quicktable.models.SeatedVisit;
import com.shaddyhollow.quicktable.models.Section;
import com.shaddyhollow.quicktable.models.Server;
import com.shaddyhollow.quicktable.models.Table;
import com.shaddyhollow.util.DateUtils;
import com.shaddyhollow.util.SingleChoiceDialogFragment;

public class DiningTableFragment extends Fragment {
	private TablesAdapter tablesAdapter = null;
	private SectionsAdapter sectionsAdapter = null;
	Mode mode = null;
	private UUID activeAreaID;

	public static DiningTableFragment newInstance(Mode mode, SectionsAdapter sectionsAdapter, TablesAdapter tablesAdapter, UUID activeAreaID) {
		DiningTableFragment fragment = new DiningTableFragment();
		fragment.mode = mode;
		fragment.tablesAdapter = tablesAdapter;
		fragment.sectionsAdapter = sectionsAdapter;
		fragment.activeAreaID = activeAreaID;
		
		if(tablesAdapter.getCurrentSelection()==null) {
			System.out.println("no selected table");
		}
		return fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.table_detail, container, false);
		updateTableInfo(view);
		updateTableButtons(view);
		return view;
	}

	private void updateTableInfo(View tableView) {
		if(tableView==null) {
			tableView = getView();
		}
		if(tableView==null) {
			return;
		}
		if(tablesAdapter.getCurrentSelection()==null) {
			return;
		}
		TextView tableName = (TextView)tableView.findViewById(R.id.table_name);
		TextView serverName= (TextView)tableView.findViewById(R.id.server_name);
		TextView customerName = (TextView)tableView.findViewById(R.id.patron_name);
		TextView party = (TextView)tableView.findViewById(R.id.patron_party);
		TextView timeseated = (TextView)tableView.findViewById(R.id.patron_timeseated);
		TextView comments = (TextView)tableView.findViewById(R.id.patron_comments);
		TextView comments_label = (TextView)tableView.findViewById(R.id.label_patron_comments);

		Table rootTable = tablesAdapter.getCurrentSelection();
		final Table table = tablesAdapter.getItemByID(rootTable.group_id);

		tableName.setText(table.name);
		if(table.seated_visit!=null) {
			SeatedVisit seatedVisit = table.seated_visit;
			serverName.setVisibility(View.VISIBLE);
			Server server = ServerFactory.getInstance().read(seatedVisit.server_id);
			if(server!=null) {
				serverName.setText(server.getName());
			}
			
			customerName.setVisibility(View.VISIBLE);
			customerName.setText(seatedVisit.name);

			party.setVisibility(View.VISIBLE);
			party.setText(String.valueOf(seatedVisit.party_size));

			timeseated.setVisibility(View.VISIBLE);
			timeseated.setText(DateUtils.getTime(seatedVisit.seating_time));

			if(seatedVisit.comment!=null && seatedVisit.comment.length()>0) {
				comments.setText(seatedVisit.comment);
				comments.setVisibility(View.VISIBLE);
				comments_label.setVisibility(View.VISIBLE);
			} else {
				comments.setVisibility(View.GONE);
				comments_label.setVisibility(View.GONE);
			}
			

		} else {
			serverName.setVisibility(View.INVISIBLE);
			customerName.setVisibility(View.INVISIBLE);
			party.setVisibility(View.INVISIBLE);
			timeseated.setVisibility(View.INVISIBLE);
			comments.setVisibility(View.GONE);
			comments_label.setVisibility(View.GONE);
		}

	}
	
	private void updateTableButtons(View tableView) {
		final Button btn_comment = (Button)tableView.findViewById(R.id.btn_comment);
		final Button btn_seat = (Button)tableView.findViewById(R.id.btn_seat);
		final Button btn_clear = (Button)tableView.findViewById(R.id.btn_clear);
		final Button btn_reseat = (Button)tableView.findViewById(R.id.btn_reseat);
		final Button btn_hold = (Button)tableView.findViewById(R.id.btn_hold);
		final Button btn_reassign = (Button)tableView.findViewById(R.id.btn_reassign);
		final Button btn_combine = (Button)tableView.findViewById(R.id.btn_combine);
		final Button btn_move = (Button)tableView.findViewById(R.id.btn_move);
		final Button btn_resize = (Button)tableView.findViewById(R.id.btn_resize);
		final Button btn_print = (Button)tableView.findViewById(R.id.btn_print);
		final Button btn_open = (Button)tableView.findViewById(R.id.btn_open);
		final Button btn_close = (Button)tableView.findViewById(R.id.btn_close);
		final Button btn_finishcombine = (Button)tableView.findViewById(R.id.btn_finishcombine);
		final Button btn_cancel = (Button)tableView.findViewById(R.id.btn_cancel);
		
		if(tablesAdapter==null) {
			btn_comment.setVisibility(View.GONE);
			btn_seat.setVisibility(View.GONE);
			btn_clear.setVisibility(View.GONE);
			btn_reseat.setVisibility(View.GONE);
			btn_hold.setVisibility(View.GONE);
			btn_reassign.setVisibility(View.GONE);
			btn_combine.setVisibility(View.GONE);
			btn_move.setVisibility(View.GONE);
			btn_resize.setVisibility(View.GONE);
			btn_print.setVisibility(View.GONE);
			btn_open.setVisibility(View.GONE);
			btn_close.setVisibility(View.GONE);
			btn_finishcombine.setVisibility(View.GONE);
			btn_cancel.setVisibility(View.GONE);
			return;
		}
		final Table table = tablesAdapter.getCurrentSelection();
		if(table==null) {
			return;
		}
		Section section = sectionsAdapter.getItemByID(table.section_id);

		if(mode==Mode.COMBINE_TABLES) {
			btn_comment.setVisibility(View.GONE);
			btn_seat.setVisibility(View.GONE);
			btn_clear.setVisibility(View.GONE);
			btn_reseat.setVisibility(View.GONE);
			btn_hold.setVisibility(View.GONE);
			btn_reassign.setVisibility(View.GONE);
			btn_combine.setVisibility(View.GONE);
			btn_move.setVisibility(View.GONE);
			btn_resize.setVisibility(View.GONE);
			btn_print.setVisibility(View.GONE);
			btn_open.setVisibility(View.GONE);
			btn_close.setVisibility(View.GONE);
			btn_finishcombine.setVisibility(View.VISIBLE);
			btn_cancel.setVisibility(View.GONE);
		} else if(mode==Mode.PATRON_MOVE) { 
			btn_comment.setVisibility(View.GONE);
			btn_seat.setVisibility(View.GONE);
			btn_clear.setVisibility(View.GONE);
			btn_reseat.setVisibility(View.GONE);
			btn_hold.setVisibility(View.GONE);
			btn_reassign.setVisibility(View.GONE);
			btn_combine.setVisibility(View.GONE);
			btn_move.setVisibility(View.GONE);
			btn_resize.setVisibility(View.GONE);
			btn_print.setVisibility(View.GONE);
			btn_open.setVisibility(View.GONE);
			btn_close.setVisibility(View.GONE);
			btn_finishcombine.setVisibility(View.GONE);
			btn_cancel.setVisibility(View.VISIBLE);
		} else {
			if(section!=null && !section.open) {
				btn_comment.setVisibility(View.GONE);
				btn_seat.setVisibility(View.GONE);
				btn_clear.setVisibility(View.GONE);
				btn_reseat.setVisibility(View.GONE);
				btn_hold.setVisibility(View.GONE);
				btn_reassign.setVisibility(View.VISIBLE);
				btn_combine.setVisibility(View.GONE);
				btn_move.setVisibility(View.GONE);
				btn_resize.setVisibility(View.GONE);
				btn_print.setVisibility(View.GONE);
				btn_open.setVisibility(View.GONE);
				btn_close.setVisibility(View.GONE);
				btn_finishcombine.setVisibility(View.GONE);
				btn_cancel.setVisibility(View.GONE);
			} else if(table.seated_visit!=null) {
				btn_comment.setVisibility(View.VISIBLE);
				btn_seat.setVisibility(View.GONE);
				btn_clear.setVisibility(View.VISIBLE);
				btn_reseat.setVisibility(View.VISIBLE);
				btn_hold.setVisibility(View.VISIBLE);
				btn_reassign.setVisibility(View.VISIBLE);
				btn_combine.setVisibility(View.VISIBLE);
				btn_move.setVisibility(View.VISIBLE);
				btn_resize.setVisibility(View.VISIBLE);
				if(table.seated_visit!=null && table.seated_visit.order_in) {
					btn_print.setVisibility(View.VISIBLE);
				} else {
					btn_print.setVisibility(View.GONE);
				}
				btn_open.setVisibility(View.GONE);
				btn_close.setVisibility(View.GONE);
				btn_finishcombine.setVisibility(View.GONE);
				btn_cancel.setVisibility(View.GONE);
			} else if(table.getState().equals(Table.Status.CLOSED)) {
				btn_comment.setVisibility(View.GONE);
				btn_seat.setVisibility(View.GONE);
				btn_clear.setVisibility(View.VISIBLE);
				btn_reseat.setVisibility(View.VISIBLE);
				btn_hold.setVisibility(View.GONE);
				btn_reassign.setVisibility(View.VISIBLE);
				btn_combine.setVisibility(View.GONE);
				btn_move.setVisibility(View.GONE);
				btn_resize.setVisibility(View.GONE);
				btn_print.setVisibility(View.GONE);
				btn_open.setVisibility(View.VISIBLE);
				btn_close.setVisibility(View.GONE);
				btn_finishcombine.setVisibility(View.GONE);
				btn_cancel.setVisibility(View.GONE);
			} else if(table.getState().equals(Table.Status.HELD)) {
				btn_comment.setVisibility(View.GONE);
				btn_seat.setVisibility(View.GONE);
				btn_clear.setVisibility(View.GONE);
				btn_reseat.setVisibility(View.GONE);
				btn_hold.setVisibility(View.GONE);
				btn_reassign.setVisibility(View.VISIBLE);
				btn_combine.setVisibility(View.VISIBLE);
				btn_move.setVisibility(View.GONE);
				btn_resize.setVisibility(View.GONE);
				btn_print.setVisibility(View.GONE);
				btn_open.setVisibility(View.VISIBLE);
				btn_close.setVisibility(View.VISIBLE);
				btn_finishcombine.setVisibility(View.GONE);
				btn_cancel.setVisibility(View.GONE);
			} else { // default to open
				if(section!=null) {
					btn_seat.setVisibility(View.VISIBLE);
				} else {
					btn_seat.setVisibility(View.GONE);
				}
				btn_comment.setVisibility(View.GONE);
				btn_clear.setVisibility(View.GONE);
				btn_reseat.setVisibility(View.GONE);
				btn_hold.setVisibility(View.VISIBLE);
				btn_reassign.setVisibility(View.VISIBLE);
				btn_combine.setVisibility(View.VISIBLE);
				btn_move.setVisibility(View.GONE);
				btn_resize.setVisibility(View.GONE);
				btn_print.setVisibility(View.GONE);
				btn_open.setVisibility(View.GONE);
				btn_close.setVisibility(View.VISIBLE);
				btn_finishcombine.setVisibility(View.GONE);
				btn_cancel.setVisibility(View.GONE);
			}
		}
		
		btn_comment.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
		 		String curComment = null;
		 		String title = "Add Comment";
				if(table.seated_visit!=null && table.seated_visit.comment!=null && table.seated_visit.comment.length()>0) {
					curComment = table.seated_visit.comment;
			 		title = "Edit Comment";
				}
				TextInputDialog dlg = TextInputDialog.newInstance(title, curComment, new TextInputListener() {
					@Override
					public void onValueSelected(String value) {
						if(table.seated_visit!=null) {
							table.seated_visit.comment = value;
						}

						FlurryAgent.logEvent(FlurryEvents.VISIT_COMMENT.name());
						TableFactory.getInstance().update(table);
						tablesAdapter.notifyDataSetChanged();
						sectionsAdapter.notifyDataSetChanged();
						((HostessActivity)getActivity()).updateDetails(Mode.TABLE_SINGLE);			
					}
				});
				dlg.show(getFragmentManager(), "comment");

			}
		});
		
		btn_seat.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				((HostessActivity)getActivity()).seatFromQueue(table, 0);			
				((HostessActivity)getActivity()).updateDetails(Mode.TABLE_SINGLE);			
			}
		});

		btn_clear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				FlurryAgent.logEvent(FlurryEvents.TABLE_CLEAR.name());
				DiningTableFragment.this.clearTable(table);
				((HostessActivity)getActivity()).updateDetails(Mode.TABLE_SINGLE);			
			}
		});
		
		btn_reseat.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				FlurryAgent.logEvent(FlurryEvents.TABLE_RESEAT.name());
				DiningTableFragment.this.clearTable(table);
				((HostessActivity)getActivity()).seatFromQueue(table, 0);			
				((HostessActivity)getActivity()).updateDetails(Mode.TABLE_SINGLE);			
			}
		});

		btn_hold.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				List<Table> combinedTables = getCombinedTables(table);
				FlurryAgent.logEvent(FlurryEvents.TABLE_HOLD.name());

				clearTable(table);
				for(Table curTable : combinedTables) {
					curTable.setState(Table.Status.HELD);
				}

				TableFactory.getInstance().update(table);
				tablesAdapter.notifyDataSetChanged();
				sectionsAdapter.notifyDataSetChanged();
				((HostessActivity)getActivity()).updateDetails(Mode.TABLE_SINGLE);	
				//TODO call UpdateTableStatusRequest
			}
		});

		btn_reassign.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				FlurryAgent.logEvent(FlurryEvents.TABLE_REASSIGN.name());

				List<String> sectionNames = new ArrayList<String>();
				for(Section section : sectionsAdapter.getAll()) {
					sectionNames.add(section.name);
				}
		        SingleChoiceDialogFragment dlg = new SingleChoiceDialogFragment(getActivity(), 0);
				dlg.setTitle("Select new section");
				dlg.setCancelable(true);
				dlg.setChoices(sectionNames);
				dlg.setSelected(sectionsAdapter.getPositionByID(table.section_id));
				dlg.setPositiveText("OK");
				dlg.setNegativeText("CANCEL");
				dlg.setListener(new SingleChoiceDialogFragment.OnItemSelectedListener() {
					@Override
					public void OnItemSelected(int key, String selectedText, int position) {
						Section newSection = sectionsAdapter.getItem(position);
						Section oldSection = sectionsAdapter.getItemByID(table.section_id);
						
						List<Table> combinedTables = getCombinedTables(table);
						
						for(Table curTable : combinedTables) {
							reassignTable(curTable, oldSection, newSection);
						}

						sectionsAdapter.notifyDataSetChanged();
						tablesAdapter.notifyDataSetChanged();
						((HostessActivity)getActivity()).updateDetails(Mode.TABLE_SINGLE);			
					}
				});
				dlg.show(getFragmentManager(), "reassign");
			}
		});

		if(getCombinedTables(table).size()>1) {
			btn_combine.setText("Split");
		} else {
			btn_combine.setText("Combine");
		}
		btn_combine.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				List<Table> combinedTables = getCombinedTables(table);
				FlurryAgent.logEvent(FlurryEvents.VISIT_COMMENT.name());
				if(combinedTables.size()==1) {
					FlurryAgent.logEvent(FlurryEvents.TABLE_COMBINE.name());
					((HostessActivity)getActivity()).updateDetails(Mode.COMBINE_TABLES);
				} else {
					FlurryAgent.logEvent(FlurryEvents.TABLE_SPLIT.name());
					DiningTableFragment.this.splitTables(table);
					((HostessActivity)getActivity()).updateDetails(Mode.SECTION_LIST);			
				}
			}
		});

		btn_move.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				FlurryAgent.logEvent(FlurryEvents.VISIT_MOVE.name());
				((HostessActivity)getActivity()).updateDetails(Mode.PATRON_MOVE);
			}
		});
		
		btn_resize.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
		 		final int currentSize;
				if(table.seated_visit!=null) {
					currentSize = table.seated_visit.party_size;
				} else {
					currentSize = table.seats;
				}
				IntegerInputDialog dlg = IntegerInputDialog.newInstance("Select new Capacity", new Listener() {
					@Override
					public void onValueSelected(int value) {
						FlurryAgent.logEvent(FlurryEvents.VISIT_RESIZE.name());
						if(table.seated_visit!=null) {
							table.seated_visit.party_size = value;
						}
						((HostessActivity)getActivity()).seatThrottle.addParty(value-currentSize);
						
						TableFactory.getInstance().update(table);
						tablesAdapter.notifyDataSetChanged();
						sectionsAdapter.notifyDataSetChanged();
						((HostessActivity)getActivity()).updateDetails(Mode.TABLE_SINGLE);			
					}
				});
				dlg.show(getFragmentManager(), "resize");
			}
		});

		if(table.seated_visit==null) {
			btn_print.setVisibility(View.GONE);
		}
		btn_print.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				FlurryAgent.logEvent(FlurryEvents.VISIT_PRINT.name());
				((HostessActivity)getActivity()).printDineInReceipt(table, table.seated_visit.visit_id);
			}
		});

		btn_open.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				List<Table> combinedTables = getCombinedTables(table);
				FlurryAgent.logEvent(FlurryEvents.TABLE_OPEN.name());
				clearTable(table);
				
				for(Table curTable : combinedTables) {
					curTable.setState(Table.Status.OPEN);
				}
				
				TableFactory.getInstance().update(table);
				tablesAdapter.notifyDataSetChanged();
				sectionsAdapter.notifyDataSetChanged();
				((HostessActivity)getActivity()).updateDetails(Mode.TABLE_SINGLE);			
				//TODO call UpdateTableStatusRequest
			}
		});

		btn_close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				List<Table> combinedTables = getCombinedTables(table);
				FlurryAgent.logEvent(FlurryEvents.TABLE_CLOSE.name());
				clearTable(table);
				
				for(Table curTable : combinedTables) {
					curTable.setState(Table.Status.CLOSED);
				}
				
				TableFactory.getInstance().update(table);
				tablesAdapter.notifyDataSetChanged();
				sectionsAdapter.notifyDataSetChanged();
				((HostessActivity)getActivity()).updateDetails(Mode.TABLE_SINGLE);			
				//TODO call UpdateTableStatusRequest
			}
		});

		btn_finishcombine.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				List<Table> combinedTables = getCombinedTables(table);
				Section newSection = sectionsAdapter.getItemByID(table.section_id);
				
				Collection<Table> originalCombinedTables = TableFactory.getInstance().bulkRead("group_uuid='" + table.group_id + "'").values();
				for(Table originalTable : originalCombinedTables) {
					boolean found = false;
					for(Table curTable : combinedTables) {
						if(originalTable.getId().equals(curTable.getId())) {
							found = true;
							break;
						}
					}
					if(!found) {
						Table adapterTable = tablesAdapter.getItemByID(originalTable.getId());
						
						adapterTable.group_id = adapterTable.getId();
						TableFactory.getInstance().update(adapterTable);
					}
				}
				
				for(Table curTable : combinedTables) {
					Section oldSection = sectionsAdapter.getItemByID(curTable.section_id);
					curTable.seated_visit = table.seated_visit;
					reassignTable(curTable, oldSection, newSection);
					TableFactory.getInstance().update(curTable);
				}
				
				TableFactory.getInstance().update(table);
				tablesAdapter.notifyDataSetChanged();
				sectionsAdapter.notifyDataSetChanged();
				((HostessActivity)getActivity()).updateDetails(Mode.TABLE_SINGLE);			
				//TODO call UpdateTableStatusRequest
			}
		});
		
		btn_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				((HostessActivity)getActivity()).updateDetails(Mode.TABLE_SINGLE);			
			}
		});
	}
	
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
		
		((HostessActivity)getActivity()).deleteSeatedVisit(table.seated_visit);
		
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

	private void reassignTable(Table table, Section oldSection, Section newSection) {
		
		if(oldSection==newSection) {
			return;
		}
		if(oldSection!=null) {
			oldSection.removeTable(table);
		}
		if(newSection!=null) {
			newSection.addTable(table);
			SectionPlanFactory.getInstance().reassignTable(activeAreaID, newSection, table);
		}
		
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

}
