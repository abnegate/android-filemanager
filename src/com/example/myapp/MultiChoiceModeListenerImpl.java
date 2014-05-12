package com.example.myapp;

import java.io.File;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView.MultiChoiceModeListener;
import com.jakebarnby.filemanager.R;

/**
 * Defines actions for multi selection mode
 * 
 * @author Jake
 * 
 */

public class MultiChoiceModeListenerImpl implements MultiChoiceModeListener {

	private DisplayDirectoryActivity d;

	public MultiChoiceModeListenerImpl(DisplayDirectoryActivity d) {
		super();
		this.d = d;
	}

	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		if (d.isItemsMoving()) {
			menu.clear();
			menu.add(Menu.NONE, R.id.context_cancel_paste, Menu.FIRST, "Cancel")
					.setIcon(R.drawable.ic_action_cancel);
			menu.add(Menu.NONE, Menu.NONE, Menu.FIRST + 1,
					"Moving " + d.getNumMoving() + " items");
			menu.add(Menu.NONE, R.id.context_accept_paste, Menu.FIRST + 2,
					"PASTE").setIcon(R.drawable.ic_action_paste);
			return true;
		} else {
			menu.removeItem(R.id.context_accept_paste);
			menu.removeItem(R.id.context_cancel_paste);
			return true;
		}
	}

	/**
	 * If multi selection is cancelled, forget all selections
	 */
	@Override
	public void onDestroyActionMode(ActionMode mode) {
		d.getmAdapter().clearSelection();
		d.getFilesMoving().clear();
		d.getSelectedPaths().clear();
		d.setItemsMoving(false);
		d.setCut(false);
		d.setNumMoving(0);
		mode.invalidate();
	}

	/**
	 * Begin multi select mode, inflate menu options
	 */
	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		MenuInflater inflater = mode.getMenuInflater();
		inflater.inflate(R.menu.context, menu);
		return true;
	}

	/**
	 * Switch based on menu option clicked
	 */
	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.context_delete:
			return contextDelete(mode);

		case R.id.context_copy:
			return contextCopy(mode);

		case R.id.context_cut:
			return contextCut(mode);

		case R.id.context_accept_paste:
			return contextAcceptPaste(mode);

		case R.id.context_cancel_paste:
			return contextCancelPaste(mode);

		default:
			return false;
		}

	}
	
	/**
	 * Actions for when item is either checked or unchecked
	 */
	@Override
	public void onItemCheckedStateChanged(ActionMode mode, int position,
			long id, boolean checked) {
		if (!d.isItemsMoving()) {
			if (checked) {
				String path = d.getmAdapter().getItem(position);
				d.setNumMoving(d.getNumMoving()+1);
				d.getmAdapter().setNewSelection(path, position, true);
				mode.setTitle(d.getNumMoving() + " items selected");
			} else if (!checked) {
				d.setNumMoving(d.getNumMoving()-1);
				d.getmAdapter().removeSelection(position);
			}
			mode.setTitle(d.getNumMoving() + " items selected");
			mode.invalidate();
		}
		if (d.isItemsMoving()) {
			String clickedItem = d.getmAdapter().getItem(position);
			// Update path and listview to new directory based on what was
			// clicked
			d.setPath(clickedItem);
			d.populateFiles();
			d.getmAdapter().notifyDataSetChanged();
			mode.invalidate();
		}
	}

	
	/**
	 * Delete option was selected
	 * @param mode The action mode in use
	 * @return If the action was successful
	 */
	public boolean contextDelete(ActionMode mode) {
		d.setMode(mode);
		confirmDelete();
		return true;
	}

	/**
	 * Copy option was selected
	 * @param mode The action mode in use
	 * @return If the action was successful
	 */
	public boolean contextCopy(ActionMode mode) {
		d.setCut(false);
		d.setItemsMoving(true);
		d.setSelectedPaths(d.getmAdapter().getCurrentPaths());
		d.setNumMoving(d.getSelectedPaths().size());
		for (int i = 0; i < d.getSelectedPaths().size(); i++) {
			d.getFilesMoving().add(new File(d.getSelectedPaths().get(i)));
		}
		mode.setTitle("Preparing to copy..");
		mode.invalidate();
		d.getmAdapter().clearSelection();
		return true;
	}

	/**
	 * Cut option was selected
	 * @param mode The action mode in use
	 * @return If the action was successful
	 */
	public boolean contextCut(ActionMode mode) {
		d.setCut(true);
		d.setItemsMoving(true);
		d.setSelectedPaths(d.getmAdapter().getCurrentPaths());
		d.setNumMoving(d.getSelectedPaths().size());
		for (int i = 0; i < d.getSelectedPaths().size(); i++) {
			d.getFilesMoving().add(new File(d.getSelectedPaths().get(i)));
		}
		mode.setTitle("Preparing to cut..");
		mode.invalidate();
		d.getmAdapter().clearSelection();
		return true;
	}

	/**
	 * Paste option was selected
	 * @param mode The action mode in use
	 * @return If the action was successful
	 */
	@SuppressWarnings("unchecked")
	public boolean contextAcceptPaste(ActionMode mode) {
		MoveFiles copy = new MoveFiles(new ProgressDialog(d), d.getPath());
		copy.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
				d.getFilesMoving());
		for (int i = 0; i < d.getFilesMoving().size(); i++)
			d.getCurrentFileList().add(
					d.getPath() + "/" + d.getFilesMoving().get(i).getName());
		d.getSelectedPaths().clear();
		d.getmAdapter().notifyDataSetChanged();
		mode.finish(); // Action picked, so close the CAB
		return true;
	}

	/**
	 * Paste option was cancelled
	 * @param mode The action mode in use
	 * @return If the action was successful
	 */
	public boolean contextCancelPaste(ActionMode mode) {
		mode.finish();
		return true;
	}

	/**
	 * Display dialog giving options to confirm or cancel delete
	 */
	public void confirmDelete() {
		// Create an instance of the dialog fragment and show it
		DialogFragment dialog = new NoticeDialogFragment();
		dialog.show(d.getFragmentManager(), "NoticeDialogFragment");
	}

}
