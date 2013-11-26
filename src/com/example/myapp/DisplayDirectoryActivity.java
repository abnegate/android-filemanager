package com.example.myapp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class DisplayDirectoryActivity extends Activity implements MultiChoiceModeListener {

	ArrayList<String> currentFileList = new ArrayList<String>();
	List<View> selected = new ArrayList<View>();
	String path;
	String query;

	ListView lv;
	SelectionAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.display_directory_activity);
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// Receive current directory path
		String currentPath = (String) getIntent().getCharSequenceExtra("currentPath");
		path = currentPath;

		// Add all directories on external SD card to List->Map for display in listView
		populateFiles();
		setupListViewMulti();
		setupAdapter();
		setupListClick();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_activity_actions, menu);
		return true;
	}
	
	public void populateFiles() {
		// Clear list for updating
		currentFileList.clear();
		// Create a file from the current path
		File current = new File(path);
		// Array of the files within the current directory
		File[] subFiles = current.listFiles();
		// Add each file in the current directory to currentFileList
		for (int i = 0; i < subFiles.length; i++) {
			currentFileList.add(subFiles[i].getPath());
		}
	}
	
	public void setupListViewMulti() {
	// Create ListView attached to ListView in xml definition
	lv = (ListView) findViewById(R.id.listView);

	// Set listview to multiple selection mode
	lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

	lv.setMultiChoiceModeListener(this);
	}
		
	
	public void setupAdapter() {
		// Create a new adapter with the files needed to be listed
		mAdapter = new SelectionAdapter(getBaseContext(), R.layout.row_list_item, R.id.file, currentFileList);
		// Set the new adapter to the ListView
		lv.setAdapter(mAdapter);
	}

	private void setupListClick() {
		// Set up the ListView to accept clicking on items
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> parentAdapter, View view,
					int position, long id) {
				// View is a text view so it can be cast
				TextView clickedItem = (TextView) view.findViewById(R.id.file);

				// Update path and listview to new directory based on what was
				// clicked
				path += "/" + clickedItem.getText();

				File currentFile = new File(path);
				// Create a new file from the updated path and check if it is a
				// directory or file
				if (currentFile.isDirectory()) {
					populateFiles();
					mAdapter.notifyDataSetChanged();
					}

				else if (currentFile.isFile()) {
					// Create new intent and set it
					Intent intent = new Intent();
					intent.setAction(android.content.Intent.ACTION_VIEW);

					// Get extension type of file
					MimeTypeMap mime = MimeTypeMap.getSingleton();
					String ext = currentFile.getName().substring(
							currentFile.getName().indexOf(".") + 1);
					String type = mime.getMimeTypeFromExtension(ext);

					// Give intent the filename and extension
					intent.setDataAndType(Uri.fromFile(currentFile), type);

					// Set path back to parent of file opened
					File currentDirectory = new File(path);
					path = currentDirectory.getParent();

					// Start the new activity, launching the file with defualt
					// application
					startActivity(intent);
				}
			}
		});

	}
	
	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onDestroyActionMode(ActionMode mode) {
		mAdapter.clearSelection();
	}

	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		MenuInflater inflater = mode.getMenuInflater();
		inflater.inflate(R.menu.context, menu);
		return true;
	}

	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.context_delete:
			mode.finish(); // Action picked, so close the CAB
			return true;
		default:
			return false;

		}
	}

	@Override
	public void onItemCheckedStateChanged(ActionMode mode,int position, long id, boolean checked) {
		if (checked){
			mAdapter.setNewSelection(position, true);;
		}
	}
	
	// Override back button to move up one level in the filesystem on press
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (path.length() > 5) {
			if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
				File currentDirectory = new File(path);
				path = currentDirectory.getParent();
				populateFiles();
				mAdapter.notifyDataSetChanged();
				return true;
			} else
				finish();
		}

		return super.onKeyDown(keyCode, event);
	}
	
	
}
