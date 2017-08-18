package com.shaddyhollow.freedom.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

import com.shaddyhollow.quicktable.models.Identifiable;

public abstract class BaseDataAdapter<T extends Identifiable> extends BaseAdapter implements DataAdapter<T> {
	private int currentSelection = -1;
	protected LayoutInflater layoutInflater = null;
	private ArrayList<T> dataset = new ArrayList<T>();
	private HashMap<UUID, T> indexedDataset = new HashMap<UUID, T>();
	
	public BaseDataAdapter(Context context) {
		this.layoutInflater =  LayoutInflater.from(context);
	}
	
	@Override
	public void add(T element) {
		this.dataset.add(element);
		this.indexedDataset.put(element.getId(), element);
	}

	@Override
	public void clear() {
		this.dataset.clear();
		this.indexedDataset.clear();
	}
	
	@Override
	public void addAll(Collection<T> pDataset) {
		for(T element : pDataset) {
			add(element);
		}
	}

	@Override
	public List<T> getAll() {
		return dataset;
	}
	
	@Override
	public void remove(int position) {
		if(position>-1 && position<dataset.size()) {
			T element = getAll().remove(position);
			indexedDataset.remove(element.getId());
		}
		clearSelectionPosition();
	}
	
	public void setSelectionByID(UUID element) {
		int len = dataset.size();
		for(int i=0;i<len;i++) {
			if(dataset.get(i).getId().equals(element)) {
				currentSelection = i;
				break;
			}
		}
	}
	
	public boolean isSelected(UUID id) {
		T identifiableObject = getCurrentSelection();
		if(identifiableObject==null) {
			return false;
		}
		return (identifiableObject.getId().equals(id));
	}
	
	public int getCurrentSelectionPosition() {
		return currentSelection;
	}
	
	public T getCurrentSelection() {
		
		if(currentSelection!=-1 && currentSelection<getCount()) {
			return getItem(currentSelection);
		}
		if(currentSelection>=getCount()) {
			return getItem(getCount()-1);
		}
		return null;
	}
	
	public void setSelectionPosition(int position) {
		currentSelection = position;
	}
	
	public void clearSelectionPosition() {
		currentSelection = -1;
	}
	
	@Override
	public int getCount() {
		return dataset.size();
	}

	@Override
	public T getItem(int position) {
		return dataset.get(position);
	}

	@Override
	public T getItemByID(UUID ID) {
		return indexedDataset.get(ID);
	}
	
	public int getPositionByID(UUID ID) {
		int len = dataset.size();
		for(int i=0;i<len;i++) {
			T element = getItem(i);
			if(element.getId()==ID) {
				return i;
			}
		}
		return 0;
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}

}
