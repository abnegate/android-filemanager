package com.example.myapp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AbsListView;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class DisplayDirectoryActivity extends Activity implements MultiChoiceModeListener, NoticeDialogFragment.NoticeDialogListener {

	private ArrayList<String> currentFileList = new ArrayList<String>();
	private List<String> selectedPaths = new ArrayList<String>();
	private ArrayList<File> filesMoving = new ArrayList<File>();
	private String path;
	private boolean itemsMoving = false;
	private static boolean cut = false;
	private int numMoving = 0;

	private AbsListView lv;
	private SelectionAdapter mAdapter;
	private ActionMode mode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.display_directory_activity);
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// Receive current directory path
		String currentPath = (String) getIntent().getCharSequenceExtra("currentPath");
		path = currentPath;

		// Run initialiation methods
		populateFiles();
		setupListViewMulti();
		setupAdapter();
		setupListClick();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_activity_actions, menu);
		return true;
	}

	public void populateFiles() {
		// Clear current file list for updating
		currentFileList.clear();
		// Create a file from the current path
		File current = new File(path);
		// Array of the files within the current directory
		File[] subFiles = current.listFiles();
		// Add each filepath in the current directory to currentFileList
		for (int i = 0; i < subFiles.length; i++) {
			currentFileList.add(subFiles[i].getPath());
		}
	}

	public void setupListViewMulti() {
		// Find the listview
		lv = (AbsListView) findViewById(R.id.gridView);

		// Set listview to multiple selection mode
		lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		lv.setMultiChoiceModeListener(this);
	}

	public void setupAdapter() {
		// Create a new adapter with the context, row layout, textview id tag
		// and the current files
		mAdapter = new SelectionAdapter(getBaseContext(), R.layout.row_list_item, R.id.file, currentFileList);
		// Set the new adapter to the ListView
		lv.setAdapter(mAdapter);
	}

	private void setupListClick() {
		// Create onClickListener to allow action of clicking listview items
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> parentAdapter, View view, int position, long id) {
				// View is a text view so it can be cast
				TextView clickedItem = (TextView) view.findViewById(R.id.file);

				// Update path and listview to new directory based on what was
				// clicked
				path += "/" + clickedItem.getText();

				// Create a new file from the updated path and check if it is a
				// directory or file
				File currentFile = new File(path);

				if (currentFile.isDirectory()) {
					populateFiles();
					mAdapter.notifyDataSetChanged();
				}

				else if (currentFile.isFile()) {
					// Create new intent and set it's action to ACTION_VIEW
					Intent intent = new Intent();
					intent.setAction(android.content.Intent.ACTION_VIEW);

					// Get extension type of file
					MimeTypeMap mime = MimeTypeMap.getSingleton();
					String ext = currentFile.getName().substring(currentFile.getName().indexOf(".") + 1);
					String type = mime.getMimeTypeFromExtension(ext);

					// Give intent the filename and extension
					intent.setDataAndType(Uri.fromFile(currentFile), type);

					// Set path back to parent of file opened
					File currentDirectory = new File(path);
					path = currentDirectory.getParent();

					// Start the new activity, launching the file with defualt
					// application
					startActivity(intent);
				}
			}
		});

	}


	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		if (itemsMoving) {
			menu.clear();
			menu.add(Menu.NONE, R.id.context_cancel_paste, Menu.FIRST, "Cancel").setIcon(R.drawable.ic_action_cancel);
			menu.add(Menu.NONE, Menu.NONE, Menu.FIRST + 1, "Moving " + numMoving + " items");
			menu.add(Menu.NONE, R.id.context_accept_paste, Menu.FIRST + 2, "PASTE").setIcon(R.drawable.ic_action_paste);
			return true;
		} else {
			menu.removeItem(R.id.context_accept_paste);
			menu.removeItem(R.id.context_cancel_paste);
			return true;
		}
	}

	@Override
	public void onDestroyActionMode(ActionMode mode) {
		mAdapter.clearSelection();
		filesMoving.clear();
		selectedPaths.clear();
		itemsMoving = false;
		cut = false;
		numMoving = 0;
		mode.invalidate();
	}

	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		MenuInflater inflater = mode.getMenuInflater();
		inflater.inflate(R.menu.context, menu);
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		// Switch based on menu option clicked
		switch (item.getItemId()) {
		case R.id.context_delete:
			this.mode = mode;
			confirmDelete();
			return true;

		case R.id.context_copy:
			cut = false;
			itemsMoving = true;
			selectedPaths = mAdapter.getCurrentPaths();
			numMoving = selectedPaths.size();
			for (int i = 0; i < selectedPaths.size(); i++) {
				filesMoving.add(new File(selectedPaths.get(i)));
			}
			mode.setTitle("Preparing to copy..");
			mode.invalidate();
			mAdapter.clearSelection();
			return true;

		case R.id.context_cut:
			cut = true;
			itemsMoving = true;
			selectedPaths = mAdapter.getCurrentPaths();
			numMoving = selectedPaths.size();
			for (int i = 0; i < selectedPaths.size(); i++) {
				filesMoving.add(new File(selectedPaths.get(i)));
			}
			mode.setTitle("Preparing to cut..");
			mode.invalidate();
			mAdapter.clearSelection();
			return true;

		case R.id.context_accept_paste:
			MoveFiles copy = new MoveFiles(new ProgressDialog(this), path);
			Log.e("copy path given", path);
			copy.execute(filesMoving);	
			for (int i = 0; i < filesMoving.size(); i++)
				currentFileList.add(path + "/" + filesMoving.get(i).getName());
			selectedPaths.clear();
			mAdapter.notifyDataSetChanged();
			mode.finish(); // Action picked, so close the CAB
			return true;

		case R.id.context_cancel_paste:
			mode.finish();
			return true;
		default:
			return false;
		}

	}
	

	@Override
	public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
		if (!itemsMoving) {
			if (checked) {
				String path = mAdapter.getItem(position);
				numMoving++;
				mAdapter.setNewSelection(path, position, true);
				mode.setTitle(numMoving + " items selected");
			} else if (!checked) {
				numMoving--;
				mAdapter.removeSelection(position);
			}
			mode.setTitle(numMoving + " items selected");
			mode.invalidate();
		}
		if (itemsMoving) {
			String clickedItem = mAdapter.getItem(position);
			// Update path and listview to new directory based on what was
			// clicked
			path = clickedItem;
			populateFiles();
			mAdapter.notifyDataSetChanged();
			mode.invalidate();
		}
	}

	// Override back button to move up one level in the filesystem on press
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (path.length() > 15) {
			if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
				File currentDirectory = new File(path);
				path = currentDirectory.getParent();
				populateFiles();
				mAdapter.notifyDataSetChanged();
				return true;
			} else
				finish();
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (itemsMoving) {
			if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
				File currentDirectory = new File(path);
				path = currentDirectory.getParent();
				populateFiles();
				mAdapter.notifyDataSetChanged();
				return true;
			}
			return true; // consumes the back key event - ActionMode is not
							// finished
		}

		return super.dispatchKeyEvent(event);
	}

	@Override
	public void onDialogPositiveClick(DialogFragment dialog) {
		selectedPaths = mAdapter.getCurrentPaths();
		for (int i = 0; i < selectedPaths.size(); i++) {
			File f = new File(selectedPaths.get(i));
			MoveFiles.DeleteRecursive(f);
			mAdapter.remove(selectedPaths.get(i));
			mAdapter.notifyDataSetChanged();
		}
		selectedPaths.clear();
		Toast.makeText(getBaseContext(), "Delete successful", Toast.LENGTH_SHORT).show();
		mode.finish(); // Action picked, so close the CAB
	}

	@Override
	public void onDialogNegativeClick(DialogFragment dialog) {
		mode.finish();
		
	}
	public void confirmDelete() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new NoticeDialogFragment();
        dialog.show(getFragmentManager(), "NoticeDialogFragment");
    }
	
	public static boolean getCut() {
		return cut;
	}
}
