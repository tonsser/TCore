package dk.tonsser.widgets.dialogs.alert;

import android.app.Activity;

import dk.tonsser.base.TBaseApplication;
import dk.tonsser.widgets.dialogs.alert.util.TAlertDialogOptions;
import dk.tonsser.widgets.dialogs.alert.util.TAlertListener;

public class TQuitCustomizableAlertDialog extends TAlertDialog {

    public TQuitCustomizableAlertDialog(final Activity mActivity,
                                        TAlertDialogOptions mTAlertDialogOptions) {
        super(mActivity, mTAlertDialogOptions, new TAlertListener() {

            @Override
            public void onRightClicked() {
                mActivity.finish();
                TBaseApplication.broadcastFinishAll(mActivity);
            }

            @Override
            public void onLeftClicked() {
                //do nothing
            }
        });
    }
}
