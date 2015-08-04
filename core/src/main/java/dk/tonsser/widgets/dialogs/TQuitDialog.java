package dk.tonsser.widgets.dialogs;

import android.app.Activity;

import dk.tonsser.base.TBaseApplication;
import dk.tonsser.widgets.dialogs.alert.TBasicAlertDialog;

/**
 * @author Casper Rasmussen
 */
public class TQuitDialog extends TBasicAlertDialog {

    /**
     * Remember to call show() afterwards
     *
     * @param mActivity
     * @param title
     * @param yesText
     * @param noText
     * @param cancelable
     * @param mOnNPickDialogListener2
     */
    public TQuitDialog(Activity mActivity, String title, String noText, String yesText, boolean cancelable, NBasicAlertDialogListener mNNativeAlertDialogListener) {
        super(mActivity, null, title, noText, yesText, cancelable, mNNativeAlertDialogListener);
    }


    /**
     * Will show a quit dialog, and if yes is clicked broadcast to all NActivities to finish
     *
     * @param mActivity
     * @param title
     * @param yesText
     * @param noText
     */
    public TQuitDialog(final Activity mActivity, String title, String noText, String yesText) {
        super(mActivity, null, title, noText, yesText, true, new NBasicAlertDialogListener() {

            @Override
            public void onRightBtnClicked() {
                mActivity.finish();
                TBaseApplication.broadcastFinishAll(mActivity);
            }

            @Override
            public void onLeftBtnClicked() {
            }
        });
    }

    /**
     * Default English
     *
     * @param mActivity
     */
    public TQuitDialog(final Activity mActivity) {
        super(mActivity, null, "Want to quit application?", "No", "Yes", true, new NBasicAlertDialogListener() {

            @Override
            public void onLeftBtnClicked() {
            }

            @Override
            public void onRightBtnClicked() {
                mActivity.finish();
                TBaseApplication.broadcastFinishAll(mActivity);
            }
        });
    }
}
