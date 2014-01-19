package com.example.myapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

public class MoveFiles extends AsyncTask<ArrayList<File>, Integer, Boolean> {
	
	private ProgressDialog pd;
	private File destination;
	
	public MoveFiles(ProgressDialog pd, File dest) { 
		this.pd = pd;
		this.destination = dest;
	}

	@Override
	protected Boolean doInBackground(ArrayList<File>... files) {
		int count = files[0].size();
		try { 
			for (int i = 0; i < count; i++) {
				copyDirectory(files[0].get(i), destination);
				publishProgress((int) (i / (float) count) * 100);
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return null;
	}
	
	protected void onPreExecute() {
		pd.setMessage("Please wait..");
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setMax(100);
		pd.show();
	}
	
	protected void onPostExecute(Boolean result) {
		pd.dismiss();
	}
	
	protected void onProgressUpdate(Integer...progress){
		pd.setMessage("Copying");
		pd.setProgress(progress[0]);
		pd.show();
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
		if (DisplayDirectoryActivity.getCut()) {
			DeleteRecursive(sourceLocation);
		}
	}

	static void DeleteRecursive(File fileOrDirectory) {
		if (fileOrDirectory.isDirectory())
			for (File child : fileOrDirectory.listFiles())
				DeleteRecursive(child);

		fileOrDirectory.delete();
	}

}
