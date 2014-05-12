package com.example.myapp;

import java.io.File;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.Toast;

public class DialogOnClickListenerImpl implements OnClickListener, NoticeDialogFragment.NoticeDialogListener {

	private DisplayDirectoryActivity d;
	
	public DialogOnClickListenerImpl(DisplayDirectoryActivity d) {
		this.d = d;
	}

	/**
	 * Determines whether delete was confirmed or cancelled
	 */
	@Override
	public void onClick(DialogInterface arg0, int arg1) {
		if (arg1 == DialogInterface.BUTTON_POSITIVE) {
			onDialogPositiveClick((DialogFragment) arg0);
		} else {
			onDialogNegativeClick((DialogFragment) arg0);
		}

	}

	/**
	 * Delete was confirmed by clicking confirm on the dialog fragment
	 */
	public void onDialogPositiveClick(DialogFragment dialog) {
		d.setSelectedPaths(d.getmAdapter().getCurrentPaths());
		for (int i = 0; i < d.getSelectedPaths().size(); i++) {
			File f = new File(d.getSelectedPaths().get(i));
			MoveFiles.DeleteRecursive(f);
			d.getmAdapter().remove(d.getSelectedPaths().get(i));
			d.getmAdapter().notifyDataSetChanged();
		}
		d.getSelectedPaths().clear();
		Toast.makeText(d.getBaseContext(), "Delete successful",
				Toast.LENGTH_SHORT).show();
		d.getMode().finish(); // Action picked, so close the CAB
	}
	
	/**
	 * Delete cancelled by selecting cancel on the dialog fragment
	 */
	public void onDialogNegativeClick(DialogFragment dialog) {
		d.getMode().finish();

	}
}
