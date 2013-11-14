package com.example.myapp;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity_actions, menu);
        
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	//Search button was clicked in action bar
    	case R.id.action_search:
    		//openSearch();
    	}
    	return false;
    }
    
    
    //Display contents of the external sdcard
    public void openExternal(View view) {
    	Intent intent = new Intent(this, DisplayDirectoryActivity.class);
    	//Need a method for external card, rather than hard coding
    	intent.putExtra("currentPath", "/storage/extSdCard");
    	startActivity(intent);
    }
    
    //Display contents of internal sdcard
    public void openInternal(View view) {
    	Intent intent = new Intent(this, DisplayDirectoryActivity.class);
    	intent.putExtra("currentPath", Environment.getExternalStorageDirectory().getPath());
    	startActivity(intent);
    }
    
    //Search using the given term
    public void openSearch() {
    	
    }
}
    

