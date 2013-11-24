package com.example.myapp;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class DisplayDirectoryActivity extends Activity {

	List<Map<String, String>> currentFileList = new ArrayList<Map<String, String>>();
	String path;
	String query;

	ListView lv;
	SimpleAdapter simpleAdpt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.display_directory_activity);
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// Receive current directory path
		String currentPath = (String) getIntent().getCharSequenceExtra("currentPath");
		path = currentPath;

		// Add all directories on external SD card to List->Map for display in
		// ListView
		populateFiles();

		// Create ListView attached to ListView in xml definition
		lv = (ListView) findViewById(R.id.listView);

		// Create a new adapter with the files needed to be listed
		simpleAdpt = new SimpleAdapter(getBaseContext(), currentFileList,
				R.layout.list_view_adapter, new String[] { "file", "img" },
				new int[] { R.id.file, R.id.img });
		// Set the new adapter to the ListView
		lv.setAdapter(simpleAdpt);

		// Set up the ListView to accept clicking on items
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> parentAdapter, View view, int position, long id) {
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
					simpleAdpt.notifyDataSetChanged();
				}

				else if (currentFile.isFile()) {
					// Create new intent and set it
					Intent intent = new Intent();
					intent.setAction(android.content.Intent.ACTION_VIEW);

					// Get extension type of file
					MimeTypeMap mime = MimeTypeMap.getSingleton();
					String ext = currentFile.getName().substring(currentFile.getName().indexOf(".") + 1);
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_activity_actions, menu);
		return true;
	}

	public void populateFiles() {
		// Clear list for updating
		currentFileList.clear();
		// Create a file from the current path
		File current = new File(this.path);
		// Array of the files within the current directory
		String[] subFiles = current.list();

		// Add each file in the current directory to currentFileList
		for (int i = 0; i < subFiles.length; i++) {
				currentFileList.add(createEntry("file", subFiles[i]));
		}
	}

	// Create a map of key to filename for adding to the currentFileList
	private HashMap<String, String> createEntry(String type, String name) {
		HashMap<String, String> entry = new HashMap<String, String>();
		File current = new File(path, name);
		//If the current file is a directory, display icon as folder
		if (current.isFile()) {
			entry.put(type, name);
			entry.put("img", String.valueOf(R.drawable.file));
		}
		//If the current file is not a directory, display icon as a file
		else {
			entry.put(type, name);
			entry.put("img", String.valueOf(R.drawable.folder));
		}
			
		return entry;

	}

	// Override back button to move up one level in the filesystem on press
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (path.length() > 5) {
			if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
				File currentDirectory = new File(path);
				path = currentDirectory.getParent();
				populateFiles();
				simpleAdpt.notifyDataSetChanged();
				return true;
			} else
				finish();
		}

		return super.onKeyDown(keyCode, event);
	}
}
