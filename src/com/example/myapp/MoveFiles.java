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
import android.util.Log;

public class MoveFiles extends AsyncTask<ArrayList<File>, Integer, Boolean> {
	
	private ProgressDialog pd;
	private String path;
	
	public MoveFiles(ProgressDialog pd, String path) { 
		this.pd = pd;
		this.path = path;
	}

	@Override
	protected Boolean doInBackground(ArrayList<File>... params) {
		ArrayList<File> files = new ArrayList<File>();
		files.addAll(params[0]);
		try { 
			
			
			Log.d("count", String.valueOf(files.size()));
			for (int i = 0 ; i < files.size(); i++) {
				Log.d("files", files.get(i).getName());
			}
			
			
			for (int i = 0; i < files.size(); i++) {
				Log.d("path", path);
				File destination = new File (path + "/", files.get(i).getName());
				copyDirectory(files.get(i), destination);
				 publishProgress((int) ((i / (float) files.size() * 100)));
				}
		} 
		catch (IOException e) {
			e.printStackTrace();
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
	}
	
	protected void onProgressUpdate(Integer...progress){
		pd.setProgress(progress[0]);
		Log.d("progress", String.valueOf(progress[0]));
	}
	
	
	static void copyDirectory(File sourceLocation, File targetLocation) throws IOException {
		if (sourceLocation.isDirectory() && sourceLocation.exists()) {
			if (!targetLocation.exists()) {
				targetLocation.mkdir();
			}

			String[] children = sourceLocation.list();
			for (int i = 0; i < children.length; i++) {
				copyDirectory(new File(sourceLocation, children[i]), new File(targetLocation, children[i]));
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

	static void DeleteRecursive(File fileOrDirectory) {
		if (fileOrDirectory.isDirectory())
			for (File child : fileOrDirectory.listFiles())
				DeleteRecursive(child);

		fileOrDirectory.delete();
	}

}
