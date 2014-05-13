package com.example.myapp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

public class ASyncDeleteFiles extends AsyncTask<List<String>, Integer, Boolean> {

	private ProgressDialog pd;

	public ASyncDeleteFiles(ProgressDialog pd) {
		this.pd = pd;
	}

	protected Boolean doInBackground(List<String>... params) {

		List<String> sourceFiles = new ArrayList<String>();
		sourceFiles.addAll(params[0]);
		for (int i = 0; i < sourceFiles.size(); i++) {
			deleteRecursive(new File(sourceFiles.get(i)));
			publishProgress((int) ((i / (float) sourceFiles.size() * 100)));
		}
		return true;
	}

	protected void onPreExecute() {
		pd.setMessage("Please wait..");
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setMax(100);
		pd.setIndeterminate(false);
		pd.setCancelable(true);
		pd.show();
	}

	protected void onPostExecute(Boolean result) {
		pd.setProgress(100);
		pd.setMessage("Delete successful!!");
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			public void run() {
				pd.dismiss();
			}
		}, 2000);
	}

	protected void onProgressUpdate(Integer... progress) {
		pd.setProgress(progress[0]);
		Log.d("progress", String.valueOf(progress[0]));
	}

	public void deleteRecursive(File fileOrDirectory) {
		if (fileOrDirectory.isDirectory())
			for (File child : fileOrDirectory.listFiles())
				deleteRecursive(child);

		fileOrDirectory.delete();
	}
}
