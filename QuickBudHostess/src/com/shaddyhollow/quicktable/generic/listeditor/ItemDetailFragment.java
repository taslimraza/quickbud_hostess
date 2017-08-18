package com.shaddyhollow.quicktable.generic.listeditor;

import android.app.Fragment;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.shaddyhollow.quickbud.R;
import com.shaddyhollow.freedom.data.BaseDataAdapter;
import com.shaddyhollow.quicktable.models.Identifiable;

/**
 * A fragment representing a single Identifiable detail screen. This fragment is
 * either contained in a {@link ItemListManagerActivity} in two-pane mode (on
 * tablets) or a {@link ItemDetailActivity} on handsets.
 */
public class ItemDetailFragment extends Fragment {
	private BaseDataAdapter<? extends Identifiable> adapter;
	private boolean hasDetails;
	private int detailLayout;
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public ItemDetailFragment() {
	}
	
	public static ItemDetailFragment newInstance(int detailLayout, boolean hasDetails) {
		ItemDetailFragment fragment = new ItemDetailFragment();
		fragment.hasDetails = hasDetails;
		fragment.detailLayout = detailLayout;
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(detailLayout, container, false);
		Button editDetails = (Button)rootView.findViewById(R.id.btnDetails);
		editDetails.setVisibility(hasDetails ? View.VISIBLE : View.GONE);
		
		TextView nametype = (TextView)rootView.findViewById(R.id.item_name_title);
		nametype.setText(((ItemListManagerActivity)getActivity()).getItemType() + " Name:");
		refreshView(rootView);
		return rootView;
	}
	
	public void refreshView(View rootView) {
		Identifiable mItem = adapter.getCurrentSelection();

		if (mItem != null) {
			((TextView) rootView.findViewById(R.id.item_name)).setText(mItem.getName());
		} else {
			rootView.setVisibility(View.INVISIBLE);
		}
	}
	
   private final DataSetObserver observer = new DataSetObserver() {

        @Override
        public void onChanged() {
        	if(getView()!=null) {
        		refreshView(getView());
        	}
        }

        @Override
        public void onInvalidated() {
        	if(getView()!=null) {
        		refreshView(getView());
        	}
        }
    };


	public void setAdapter(BaseDataAdapter<? extends Identifiable> adapter) {
        if (this.adapter != null) {
            this.adapter.unregisterDataSetObserver(observer);
        }
		this.adapter = adapter;
        if (this.adapter != null) {
            this.adapter.registerDataSetObserver(observer);
        }

	}
}
