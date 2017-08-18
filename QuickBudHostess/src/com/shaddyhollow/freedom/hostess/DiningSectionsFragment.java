package com.shaddyhollow.freedom.hostess;

import java.util.UUID;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;
import com.shaddyhollow.quickbud.R;
import com.shaddyhollow.quicktable.models.Section;

public class DiningSectionsFragment extends Fragment {
	private DiningSectionsAdapter sectionsAdapter = null;
	@SuppressWarnings("unused")
	private Integer locationID;
	Mode mode = null;
	
	// drag drop listview variables
    public int dragStartMode = DragSortController.ON_DOWN;
    public boolean removeEnabled = false;
    public boolean sortEnabled = true;
    public boolean dragEnabled = true;
    private DragSortListView mDslv;
    private DragSortController mController;
    
	public static DiningSectionsFragment newInstance(DiningSectionsAdapter sectionsAdapter, Integer locationID) {
		DiningSectionsFragment fragment = new DiningSectionsFragment();
		fragment.sectionsAdapter = sectionsAdapter;
		fragment.locationID = locationID;
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sectionslist, container, false);

        mDslv = (DragSortListView)view.findViewById(R.id.sectionlist);
        mController = buildController(mDslv);
        mDslv.setFloatViewManager(mController);
        mDslv.setOnTouchListener(mController);
        mDslv.setDragEnabled(dragEnabled);
        mDslv.setDropListener(onDrop);

        return view;
	}

	@Override
	public void onDestroyView() {
		mDslv.setDropListener(null);
		mDslv.setOnTouchListener(null);
		mDslv = null;
		sectionsAdapter = null;
		View sectionView = getActivity().findViewById(R.id.sectionlist);
		ListView sectionsList = (ListView)sectionView.findViewById(R.id.sectionlist);
		sectionsList.setAdapter(null);

		super.onDestroyView();
	}

	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updateSectionView();
	}
	
    private DragSortController buildController(DragSortListView dslv) {
        DragSortController controller = new DragSortController(dslv);
        controller.setDragHandleId(R.id.list_image);
        controller.setRemoveEnabled(removeEnabled);
        controller.setSortEnabled(sortEnabled);
        controller.setDragInitMode(dragStartMode);
        return controller;
    }
    
	private void updateSectionView() {
		View sectionView = getActivity().findViewById(R.id.sectionlist);
		ListView sectionsList = (ListView)sectionView.findViewById(R.id.sectionlist);
		sectionsList.setAdapter(sectionsAdapter);
		sectionsList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
				Section thisSection = sectionsAdapter.getItem(position);
				if(sectionsAdapter.getSelection()!=null && sectionsAdapter.getSelection().getId().equals(thisSection.getId())) {
					sectionsAdapter.clearSelectionPosition();
				} else {
					sectionsAdapter.setSelection(thisSection);
				}
				sectionsAdapter.notifyDataSetChanged();
			}
		});
	}

	public void moveSections(int from, int to) {
		sectionsAdapter.moveItem(from, to);
		sectionsAdapter.incrementNextSection();
		sectionsAdapter.notifyDataSetChanged();
	}
	
    private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
    	@Override
    	public void drop(int from, int to) {
    		moveSections(from, to);
    	}
    };

}
