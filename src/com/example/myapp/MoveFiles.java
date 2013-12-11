package com.example.myapp;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class MoveFiles extends AsyncTask<File, Long, Boolean> {
	
	private Context mContext;
	private ProgressDialog pd;
	
	public MoveFiles(Context context) {
		mContext = context;
	}

	@Override
	protected Boolean doInBackground(File... files) {
		try {
			DisplayDirectoryActivity.copyDirectory(files[0], files[1]);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("static-access")
	protected void onPreExecute() {
		pd = new ProgressDialog(mContext).show(mContext, "Processing..", "Please wait.", true, false);
	}
	
	protected void onPostExecute(Boolean result) {
		pd.dismiss();
	}
	
	protected void onProgressUpdate(Long...progress){
		pd.setMessage("Transferred" + progress[0] + " bytes");
	}
}
