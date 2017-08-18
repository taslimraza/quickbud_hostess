package com.shaddyhollow.freedom.hostess;

import java.util.UUID;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.shaddyhollow.quickbud.R;
import com.shaddyhollow.freedom.dinendashhostess.requests.RemoveQueuedVisitRemoteRequest.PatronRemovedCallback;
import com.shaddyhollow.freedom.dinendashhostess.requests.UpdateQueuedVisitRequest;
import com.shaddyhollow.freedom.hostess.dialogs.AddWalkinDialogFragment;
import com.shaddyhollow.freedom.hostess.dialogs.TextInputDialog;
import com.shaddyhollow.freedom.hostess.dialogs.TextInputDialog.TextInputListener;
import com.shaddyhollow.quickbud.FlurryEvents;
import com.shaddyhollow.quicktable.models.QueuedVisit;
import com.shaddyhollow.quicktable.models.QueuedVisitRequest;
import com.shaddyhollow.robospice.BaseListener;
import com.shaddyhollow.util.DateUtils;

public class DiningPatronFragment extends Fragment implements PatronRemovedCallback {
	private QueuedPatronsAdapter patronsAdapter = null;
	private Integer locationID;
	private Integer tenantId;
	Mode mode = null;

	public static DiningPatronFragment newInstance(Mode mode, QueuedPatronsAdapter patronsAdapter, Integer locationID, Integer tenantId) {
		DiningPatronFragment fragment = new DiningPatronFragment();
		fragment.mode = mode;
		fragment.patronsAdapter = patronsAdapter;
		fragment.locationID = locationID;
		fragment.tenantId = tenantId;
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.patron_detail, container, false);
		updatePatronView(view);
		return view;
	}
	
	private AddWalkinDialogFragment.Listener walkinEdited = new AddWalkinDialogFragment.Listener() {

		@Override
		public void onWalkinAdded(QueuedVisitRequest queuedVisitRequest, QueuedVisit patron) {
			DiningPatronFragment.this.updateQueue(queuedVisitRequest, patron.getId());
		}
	};
	
	public void updateQueue(QueuedVisitRequest queuedVisitRequest, UUID patronID) {
		UpdateQueuedVisitRequest request = new UpdateQueuedVisitRequest(((HostessActivity)getActivity()).patronsLoader, queuedVisitRequest, patronID);
		request.execute(((HostessActivity)getActivity()).contentManager, new UpdateQueuedVisitRequestListener() );
	}

	private class UpdateQueuedVisitRequestListener extends BaseListener<QueuedVisit> {
		@Override
		public void onFailure(SpiceException e) {
			Log.i("Walk-in", "Walk in failed to update!");
			patronsAdapter.notifyDataSetChanged();
			updatePatronView(null);
		}

		@Override
		public void onRequestSuccess(QueuedVisit queuedVisit) {
			Log.i("Walk-in", "Walk in updated!");
			patronsAdapter.setSelection(queuedVisit);
			patronsAdapter.notifyDataSetChanged();
			updatePatronView(null);
		}
	}


	private void updatePatronView(View patronView) {
		if(patronView==null) {
			patronView = getView();
		}
		if(patronView==null) {
			return;
		}
		
		TextView customerName = (TextView)patronView.findViewById(R.id.patron_name);
		TextView party = (TextView)patronView.findViewById(R.id.patron_party);
		TextView timein = (TextView)patronView.findViewById(R.id.patron_timein);
		TextView currentwait = (TextView)patronView.findViewById(R.id.patron_currentwait);
		TextView status = (TextView)patronView.findViewById(R.id.patron_status);
		TextView needs = (TextView)patronView.findViewById(R.id.patron_needs);
		TextView needs_label = (TextView)patronView.findViewById(R.id.label_patron_needs);
		TextView comments = (TextView)patronView.findViewById(R.id.patron_comments);
		TextView comments_label = (TextView)patronView.findViewById(R.id.label_patron_comments);
		Button editButton = (Button)patronView.findViewById(R.id.btnpatron_edit);
		Button pageButton = (Button)patronView.findViewById(R.id.btnpatron_page);
		Button textButton = (Button)patronView.findViewById(R.id.btnpatron_text);
		Button printButton = (Button)patronView.findViewById(R.id.btnpatron_print);
		Button seatButton = (Button)patronView.findViewById(R.id.btnpatron_seat);
		Button removeButton = (Button)patronView.findViewById(R.id.btnpatron_remove);
		Button cancelButton = (Button)patronView.findViewById(R.id.btnpatron_cancel);

		final QueuedVisit patron = patronsAdapter.getSelection();

		int childcount = ((ViewGroup)patronView).getChildCount();
		for(int i=0;i<childcount;i++) {
			View curView = ((ViewGroup)patronView).getChildAt(i);
			curView.setVisibility(patron==null ? View.INVISIBLE : View.VISIBLE);
		}
		
		if(patron==null) {
			return;
		}
		
		customerName.setText(patron.getName());
		party.setText(String.valueOf(patron.getParty_size()));
		timein.setText(DateUtils.getTime(patron.getCreated_at()));
		currentwait.setText(patron.getLow_wait_time() + " - " + patron.getHigh_wait_time() + " min");
		status.setText(patron.getStatus());
		StringBuffer needsString = new StringBuffer("");
		if(patron.isWheel_chair_access()) {
			needsString.append("wheelchair access");
		}
		if(patron.getBooster_seats()>0) {
			if(needsString.length()>0) {
				needsString.append(", ");
			}
			needsString.append("booster seat");
		}
		if(patron.getHigh_chairs()>0) {
			if(needsString.length()>0) {
				needsString.append(", ");
			}
			needsString.append("high chair");
		}
		needs.setText(needsString.toString());
		if(needsString.length()>0) {
			needs_label.setVisibility(View.VISIBLE);
			needs.setVisibility(View.VISIBLE);
		} else {
			needs_label.setVisibility(View.GONE);
			needs.setVisibility(View.GONE);
		}
		
		if(patron.getSpecialRequests()!=null && patron.getSpecialRequests().length()>0) {
			comments.setText(patron.getSpecialRequests());
			comments.setVisibility(View.VISIBLE);
			comments_label.setVisibility(View.VISIBLE);
		} else {
			comments.setVisibility(View.GONE);
			comments_label.setVisibility(View.GONE);
		}
		
		
		// update page button
		if (patron.getPhone_number() != null && !patron.getPhone_number().isEmpty()) {
			pageButton.setAlpha(1.0f);
			pageButton.setEnabled(true);
			
			textButton.setAlpha(1.0f);
			textButton.setEnabled(true);
		} else {
			pageButton.setAlpha(0.5f);
			pageButton.setEnabled(false);

			textButton.setAlpha(0.5f);
			textButton.setEnabled(false);
		}

		// update print button
		if(patron.isOrder_in()) {
			printButton.setAlpha(1.0f);
			printButton.setEnabled(true);
		} else {
			printButton.setAlpha(0.5f);
			printButton.setEnabled(false);
		}

		if(mode==Mode.PATRON_SEATING) {
			editButton.setVisibility(View.GONE);
			pageButton.setVisibility(View.GONE);
			textButton.setVisibility(View.GONE);
			printButton.setVisibility(View.GONE);
			seatButton.setVisibility(View.GONE);
			removeButton.setVisibility(View.GONE);
			cancelButton.setVisibility(View.VISIBLE);

			cancelButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View view) {
					((HostessActivity)getActivity()).updateDetails(Mode.SECTION_LIST);
				}
			});
		} else {
			editButton.setVisibility(View.VISIBLE);
			pageButton.setVisibility(View.VISIBLE);
			textButton.setVisibility(View.VISIBLE);
			printButton.setVisibility(View.VISIBLE);
			seatButton.setVisibility(View.VISIBLE);
			removeButton.setVisibility(View.VISIBLE);
			cancelButton.setVisibility(View.GONE);

			//TODO figure out if its possible to print dine in tickets without a table
			// we currently do not support printing tickets without a table
//			printButton.setVisibility(View.GONE);
			printButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					FlurryAgent.logEvent(FlurryEvents.PATRON_PRINT.name());
					((HostessActivity)getActivity()).printDineInReceipt(null, patron.getVisit_id());			
				}
			});
			
			editButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
		        	FlurryAgent.logEvent(FlurryEvents.PATRON_EDIT.name());
					AddWalkinDialogFragment newFragment = AddWalkinDialogFragment.newInstance(walkinEdited, tenantId, locationID, patron);
				    newFragment.show(getFragmentManager(), "dialog");
				}
			});
			
			pageButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
		        	FlurryAgent.logEvent(FlurryEvents.PATRON_PAGE.name());
					((HostessActivity)getActivity()).pagePatron(patron);			
				}
			});

			textButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					TextInputDialog dlg = TextInputDialog.newInstance("Send Text to " + patron.getName(), "", new TextInputListener() {
						@Override
						public void onValueSelected(String value) {
				        	FlurryAgent.logEvent(FlurryEvents.PATRON_TEXT.name());
							((HostessActivity)getActivity()).textPatron(patron, value);			
						}
					});
					dlg.show(getFragmentManager(), "comment");
				}
			});

			seatButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
		        	FlurryAgent.logEvent(FlurryEvents.PATRON_SEAT.name());
					((HostessActivity)getActivity()).prepPatronForSeating(patron);			
				}
			});
			
			removeButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
				    new AlertDialog.Builder(getActivity())
			        .setIcon(android.R.drawable.ic_dialog_alert)
			        .setTitle("Remove Patron")
			        .setMessage("Are you sure you want to remove " + patron.getName() + "?")
			        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				        @Override
				        public void onClick(DialogInterface dialog, int which) {
				        	FlurryAgent.logEvent(FlurryEvents.PATRON_REMOVE.name());
					     	((HostessActivity)getActivity()).removePatron(patron, DiningPatronFragment.this);
							((HostessActivity)getActivity()).updateDetails(Mode.SECTION_LIST);
							DiningPatronFragment.this.patronsAdapter.notifyDataSetChanged();
				        }
				    })
				    .setNegativeButton("No", null)
				    .show();

				}
			});
		}
		
	}
	
	@Override
	public void patronRemoved(UUID queuedVisitPatron) {
	    getActivity().runOnUiThread(new Runnable() {
	        @Override
	        public void run() {
	    		patronsAdapter.notifyDataSetChanged();
	        }
	      });
	}

}
