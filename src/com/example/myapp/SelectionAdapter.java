package com.example.myapp;

import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

final class SelectionAdapter extends ArrayAdapter<String> {

	private SparseBooleanArray mSelection = new SparseBooleanArray();

	public SelectionAdapter(Context context, int resource,
			int textViewResourceId, String[] objects) {
		super(context, resource, textViewResourceId, objects);
	}

	public void setNewSelection(int position, boolean value) {
		mSelection.put(position, value);
		notifyDataSetChanged();
	}

	public boolean isPositionChecked(int position) {
		Boolean result = mSelection.get(position);
		return result == null ? false : result;
	}

	public Set<Integer> getCurrentCheckedPosition() {
		Set<Integer> current = new HashSet<Integer>();
		for(int i=0; i < mSelection.size(); i++) {
			current.add(mSelection.keyAt(i));
		}
		return current;
	}

	public void removeSelection(int position) {
		mSelection.delete(position);
		notifyDataSetChanged();
	}

	public void clearSelection() {
		mSelection = new SparseBooleanArray();
		notifyDataSetChanged();
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = super.getView(position, convertView, parent);
		return v;
	}
}
