package com.example.myapp;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class DisplayExternalActivity extends Activity {
	
	List<Map<String, String>> directories = new ArrayList<Map<String,String>>();
	String base;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_external);
		// Show the Up button in the action bar.
		setupActionBar();
		
		//Receive current root
		String root = (String) getIntent().getCharSequenceExtra("root");
		this.base = root;
		
		//Add all directories on external SD card to List->Map for display in ListView
		populateFiles();
		
		//Create ListView attached to ListView in xml definition
		ListView lv = (ListView) findViewById(R.id.listView);
		
		//Create a new adapter with the files needed to be listed
		SimpleAdapter simpleAdpt = new SimpleAdapter(this, directories, android.R.layout.simple_list_item_1, new String[] {"directory"}, new int[] {android.R.id.text1});
		
		//Set the new adapter to the ListView
		lv.setAdapter(simpleAdpt);
		
		//Set up the ListView to accept clicking on items
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			
			public void onItemClick(AdapterView<?> parentAdapter, View view, int position, long id) {
				//View is a text view so it can be cast
				TextView clickedView = (TextView) view;
				
				
				Intent intent = new Intent(DisplayExternalActivity.this, DisplayExternalActivity.class);
				intent.putExtra("root",base + "/" + clickedView.getText());
				startActivity(intent);
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
		File root = new File(base);
		String[] subFiles = root.list();
	
		for (int i = 0; i < subFiles.length-1; i++) {
			directories.add(createEntry("directory", subFiles[i]));
		}
		
	}
	
	
	private HashMap<String, String> createEntry(String key, String name) {
		HashMap<String, String> entry = new HashMap<String, String>();
		entry.put(key, name);
		
		return entry;
		
	}
	




}
