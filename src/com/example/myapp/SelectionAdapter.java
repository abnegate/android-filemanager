package com.example.myapp;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

final class SelectionAdapter extends ArrayAdapter<String> {

	private SparseBooleanArray mSelection = new SparseBooleanArray();
	private final Context context;
	private final String[] values;

	public SelectionAdapter(Context context, int resource, int textViewResourceId, String[] values) {
		super(context, resource, textViewResourceId, values);
		this.context = context;
		this.values = values;
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
		for (int i = 0; i < mSelection.size(); i++) {
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
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.row_list_item, parent, false);
		TextView textView = (TextView) rowView.findViewById(R.id.file);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
		textView.setText(values[position]);
		// Change the icon for Windows and iPhone
		String s = values[position];
		File f = new File(s);
		if (f.isDirectory()) {
			imageView.setImageResource(R.drawable.folder);
		} else {
			imageView.setImageResource(R.drawable.file);
		}

		return rowView;
	}
}
