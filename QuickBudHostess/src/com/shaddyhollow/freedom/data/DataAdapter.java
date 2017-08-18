package com.shaddyhollow.freedom.data;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import com.shaddyhollow.quicktable.models.Identifiable;

public interface DataAdapter<T extends Identifiable> {

	public abstract void add(T element);

	public abstract void clear();

	public abstract void addAll(Collection<T> pDataset);

	public abstract List<T> getAll();

	public abstract void remove(int position);

	public abstract int getCount();

	public abstract T getItem(int position);

	public abstract T getItemByID(UUID ID);

	public abstract long getItemId(int position);

}