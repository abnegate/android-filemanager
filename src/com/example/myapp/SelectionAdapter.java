package com.example.myapp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.jakebarnby.filemanager.R;

final class SelectionAdapter extends ArrayAdapter<String> {

	private SparseBooleanArray mSelection = new SparseBooleanArray();
	private final Context context;
	private List<String> values;
	private List<String> paths = new ArrayList<String>();

	public SelectionAdapter(Context context, int resource,
			int textViewResourceId, ArrayList<String> values) {
		super(context, resource, textViewResourceId, values);
		this.context = context;
		this.values = values;
	}

	public void setNewSelection(String path, int position, boolean value) {
		paths.add(path);
		mSelection.put(position, value);
		notifyDataSetChanged();
	}
	

	public boolean isPositionChecked(int position) {
		Boolean result = mSelection.get(position);
		return result == null ? false : result;
	}

	public List<Integer> getCurrentCheckedPositions() {
		List<Integer> current = new ArrayList<Integer>();
		for (int i = 0; i < mSelection.size(); i++) {
			current.add(mSelection.keyAt(i));
		}
		return current;
	}
	
	public List<String> getCurrentPaths() {
		List<String> current = new ArrayList<String>();
		for (int i = 0; i < paths.size(); i++) {
			current.add(paths.get(i));
		}
		return current;
	}

	public void removeSelection(int position) {
		paths.remove(this.getItem(position));
		mSelection.delete(position);
		notifyDataSetChanged();
	}

	public void clearSelection() {
		paths = new ArrayList<String>();
		mSelection = new SparseBooleanArray();
		notifyDataSetChanged();
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.row_list_item, parent, false);
		TextView textView = (TextView) rowView.findViewById(R.id.file);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
		File current = new File(values.get(position));
		textView.setText(current.getName());
		// Change the icon for files and folders
		if (current.isDirectory()) {
			imageView.setImageResource(R.drawable.folder);
		} else {
			imageView.setImageResource(R.drawable.file);
		}
		if (isPositionChecked(position))
			rowView.setBackgroundColor(context.getResources().getColor(android.R.color.darker_gray));
		return rowView;
	}
}
