package com.example.myapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    public void showToast(View view) {
    	Context context = getApplicationContext();
    	CharSequence text = "Get fucked, this does nothing.";
    	int duration = Toast.LENGTH_LONG;
    	
    	Toast toast = Toast.makeText(context, text, duration);
    	toast.show();    	
    }
    
    public void showExternal(View view) {
    	Intent intent = new Intent(this, DisplayExternalActivity.class);
    	intent.putExtra("root", "/");
    	startActivity(intent);
    	
    }
    
}
