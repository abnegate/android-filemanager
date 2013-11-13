package com.example.myapp;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class DisplayDirectoryActivity extends Activity {
	
	List<Map<String, String>> currentFileList = new ArrayList<Map<String,String>>();
	
	int pathCount;
	Stack<String> pathStack = new Stack<String>();
	
	ListView lv;
	SimpleAdapter simpleAdpt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_external);
		// Show the Up button in the action bar.
		setupActionBar();
		
		//Receive current directory path
		String currentPath = (String) getIntent().getCharSequenceExtra("currentPath");
		pathStack.push(currentPath);
		
		//Add all directories on external SD card to List->Map for display in ListView
		populateFiles();
		
		//Create ListView attached to ListView in xml definition
		lv = (ListView) findViewById(R.id.listView);
		
		//Create a new adapter with the files needed to be listed
		simpleAdpt = new SimpleAdapter(this, currentFileList, android.R.layout.simple_list_item_1, new String[] {"file"}, new int[] {android.R.id.text1});
		//Set the new adapter to the ListView
		lv.setAdapter(simpleAdpt);
		
		//Set up the ListView to accept clicking on items
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			
			public void onItemClick(AdapterView<?> parentAdapter, View view, int position, long id) {
				//View is a text view so it can be cast
				TextView clickedView = (TextView) view;
				
				//Update path to new directory based on click
				pathStack.push(pathStack.peek() + "/" + clickedView.getText());
				populateFiles();
				simpleAdpt.notifyDataSetChanged();
			}
		});
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}
	
	public void populateFiles() {
		//Clear list for updating
		currentFileList.clear();
		//Create a file from the current path
		File root = new File(pathStack.peek());
		//Array of the files within the current directory
		String[] subFiles = root.list();
	
		for (int i = 0; i < subFiles.length-1; i++) {
			currentFileList.add(createEntry("file", subFiles[i]));
		}
	}
	
	private HashMap<String, String> createEntry(String key, String name) {
		HashMap<String, String> entry = new HashMap<String, String>();
		entry.put(key, name);
		
		return entry;
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
		if (pathStack.size() > 1) {
			if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
				pathStack.pop();
				populateFiles();
				simpleAdpt.notifyDataSetChanged();
				return true;
			}
	    else finish();
	    }

	    return super.onKeyDown(keyCode, event);
	}
}

