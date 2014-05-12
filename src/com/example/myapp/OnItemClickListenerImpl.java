package com.example.myapp;

import java.io.File;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import com.jakebarnby.filemanager.R;

public class OnItemClickListenerImpl implements OnItemClickListener {

	DisplayDirectoryActivity d;

	public OnItemClickListenerImpl(DisplayDirectoryActivity d) {
		super();
		this.d = d;
	}

	public void onItemClick(AdapterView<?> parentAdapter, View view,
			int position, long id) {
		// View is a text view so it can be cast
		TextView clickedItem = (TextView) view.findViewById(R.id.file);

		// Update path and listview to new directory based on what was
		// clicked
		d.appendPath("/" + clickedItem.getText());

		// Create a new file from the updated path and check if it is a
		// directory or file
		File currentFile = new File(d.getPath());

		if (currentFile.isDirectory()) {
			d.populateFiles();
			d.getmAdapter().notifyDataSetChanged();
		}

		else if (currentFile.isFile()) {
			// Create new intent and set it's action to ACTION_VIEW
			Intent intent = new Intent();
			intent.setAction(android.content.Intent.ACTION_VIEW);

			// Give intent the filename and extension
			intent.setDataAndType(Uri.fromFile(currentFile), getFileExt(currentFile));

			// Set path back to parent of file opened
			File currentDirectory = new File(d.getPath());
			d.setPath(currentDirectory.getParent());

			// Start the new activity, launching the file with defualt
			// application
			d.startActivity(intent);
		}

	}
	
	/**
	 * Get the file extension of the selected file
	 * @param currentFile the file currently selected
	 * @return the extension of the selected file
	 */
	public String getFileExt(File currentFile) {
		MimeTypeMap mime = MimeTypeMap.getSingleton();
		String ext = currentFile.getName().substring(currentFile.getName().indexOf(".") + 1);
		String type = mime.getMimeTypeFromExtension(ext);
		return type;
	}
}
