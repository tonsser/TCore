package dk.nodes.widgets.dialogs.alert;

import android.app.Activity;

import dk.nodes.base.NBaseApplication;
import dk.nodes.widgets.dialogs.alert.util.NAlertDialogOptions;
import dk.nodes.widgets.dialogs.alert.util.NAlertListener;

public class NQuitCustomizableAlertDialog extends NAlertDialog{

	public NQuitCustomizableAlertDialog(final Activity mActivity,
			NAlertDialogOptions mNAlertDialogOptions) {
		super(mActivity, mNAlertDialogOptions, new NAlertListener() {

			@Override
			public void onRightClicked() {
				mActivity.finish();
				NBaseApplication.broadcastFinishAll(mActivity);		
			}

			@Override
			public void onLeftClicked() {
				//do nothing			
			}
		});
	}
}
