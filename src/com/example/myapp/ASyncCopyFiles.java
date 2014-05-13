package com.example.myapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

public class ASyncCopyFiles extends AsyncTask<ArrayList<File>, Integer, Boolean> {

	private ProgressDialog pd;
	private String path;

	public ASyncCopyFiles(ProgressDialog pd, String path) {
		this.pd = pd;
		this.path = path;
	}


	protected Boolean doInBackground(ArrayList<File>... params) {
		
		ArrayList<File> sourceFiles = new ArrayList<File>();
		sourceFiles.addAll(params[0]);
		try {
			for (int i = 0; i < sourceFiles.size(); i++) {
				
				File destination = new File(path + "/", sourceFiles.get(i).getName());
				Log.d("current drectory", destination.getAbsolutePath());
				
				
				copyDirectory(sourceFiles.get(i), destination);
				publishProgress((int) ((i / (float) sourceFiles.size() * 100)));
			}
		} catch (IOException e) {
			Log.e("check", "Could not write file " + e.getMessage());
			return false;
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
		pd.setMessage("Copy complete!");
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                pd.dismiss();
            }}, 2000);
	}

	protected void onProgressUpdate(Integer... progress) {
		pd.setProgress(progress[0]);
		Log.d("progress", String.valueOf(progress[0]));
	}

	public void copyDirectory(File sourceLocation, File targetLocation) throws IOException {
		if (sourceLocation.isDirectory() && sourceLocation.exists()) {
			if (!targetLocation.exists()) {
				targetLocation.mkdir();
			}

			String[] children = sourceLocation.list();
			for (int i = 0; i < children.length; i++) {
				copyDirectory(new File(sourceLocation, children[i]), new File(
						targetLocation, children[i]));
			}
		} else {

			InputStream in = new FileInputStream(sourceLocation);
			OutputStream out = new FileOutputStream(targetLocation);

			// Copy the bits from instream to outstream
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			in = null;
			out.flush();
			out.close();
			out = null;
		}
	}
}
