package com.example.myapp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;
import com.jakebarnby.filemanager.R;

/**
 * Sets up and displays of the all the files in the current directory and sets
 * up listeners for actions upon list items
 * 
 * @author Jake
 * 
 */

public class DisplayDirectoryActivity extends Activity implements
		NoticeDialogFragment.NoticeDialogListener {

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
		String currentPath = (String) getIntent().getCharSequenceExtra(
				"currentPath");
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

	/**
	 * Populates the currentFileList with all the files/folders in the current
	 * directory
	 */
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

	/**
	 * Creates and attaches a multi choice listener to the listview so multiple
	 * items can be selected
	 */
	public void setupListViewMulti() {
		// Find the listview
		lv = (AbsListView) findViewById(R.id.gridView);

		// Set listview to multiple selection mode
		lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		lv.setMultiChoiceModeListener(new MultiChoiceModeListenerImpl(this));
	}

	/**
	 * Creates and attaches an adapter to the listview so the data may be
	 * manipulated
	 */
	public void setupAdapter() {
		// Create a new adapter with the context, row layout, textview id tag
		// and the current files
		mAdapter = new SelectionAdapter(getBaseContext(),
				R.layout.row_list_item, R.id.file, currentFileList);
		// Set the new adapter to the ListView
		lv.setAdapter(mAdapter);
	}

	/**
	 * Creates and attaches an onclicklistener to the listview to handle when
	 * files/folders are clicked
	 */
	private void setupListClick() {
		// Create onClickListener to allow action of clicking listview items
		lv.setOnItemClickListener(new OnClickListener(this));
	}

	/**
	 * Override back button to move up one level in the filesystem on press
	 */
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

	/**
	 * TO DO: MOVE ELSEWHERE
	 */
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (itemsMoving) {
			if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
					&& event.getAction() == KeyEvent.ACTION_UP) {
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

	/**
	 * TO DO: MOVE ELSEWHERE
	 */
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
		Toast.makeText(getBaseContext(), "Delete successful",
				Toast.LENGTH_SHORT).show();
		mode.finish(); // Action picked, so close the CAB
	}

	/**
	 * TO DO: MOVE ELSEWHERE
	 */
	@Override
	public void onDialogNegativeClick(DialogFragment dialog) {
		mode.finish();

	}


	public static boolean getCut() {
		return cut;
	}

	public String getPath() {
		return path;
	}

	public void appendPath(String path) {
		this.path += path;
	}
	
	public ArrayList<String> getCurrentFileList() {
		return currentFileList;
	}

	public void setCurrentFileList(ArrayList<String> currentFileList) {
		this.currentFileList = currentFileList;
	}

	public List<String> getSelectedPaths() {
		return selectedPaths;
	}

	public void setSelectedPaths(List<String> selectedPaths) {
		this.selectedPaths = selectedPaths;
	}

	public ArrayList<File> getFilesMoving() {
		return filesMoving;
	}

	public void setFilesMoving(ArrayList<File> filesMoving) {
		this.filesMoving = filesMoving;
	}

	public boolean isItemsMoving() {
		return itemsMoving;
	}

	public void setItemsMoving(boolean itemsMoving) {
		this.itemsMoving = itemsMoving;
	}

	public int getNumMoving() {
		return numMoving;
	}

	public void setNumMoving(int numMoving) {
		this.numMoving = numMoving;
	}

	public AbsListView getLv() {
		return lv;
	}

	public void setLv(AbsListView lv) {
		this.lv = lv;
	}

	public SelectionAdapter getmAdapter() {
		return mAdapter;
	}

	public void setmAdapter(SelectionAdapter mAdapter) {
		this.mAdapter = mAdapter;
	}

	public ActionMode getMode() {
		return mode;
	}

	public void setMode(ActionMode mode) {
		this.mode = mode;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setCut(boolean cut) {
		this.cut = cut;
	}
}
