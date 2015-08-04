package dk.tonsser.widgets.dialogs.alert;

/**
 * @author Casper Rasmussen - 2012
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;

import dk.tonsser.utils.TLog;

public class TBasicAlertDialog {

    private static final String TAG = TBasicAlertDialog.class.getName();
    private Activity mActivity;
    private String title;
    private String message;
    private String leftText;
    private String rightText;
    private boolean cancelable = false;
    private AlertDialog alert;
    private NBasicAlertDialogListener mNBasicAlertDialogListener;
    private boolean isShowing = false;
    private String mQueueTag;

    /**
     * Remember to call show() afterwards
     *
     * @param mActivity
     * @param title
     * @param message
     * @param rightText
     * @param leftText
     * @param cancelable
     * @param mNBasicAlertDialogListener
     */
    public TBasicAlertDialog(Activity mActivity, String title, String message, String leftText, String rightText,
                             boolean cancelable, NBasicAlertDialogListener mNBasicAlertDialogListener) {
        if (mActivity == null) {
            TLog.e(TAG + " constructor", "Activity is null");
            return;
        }

        this.mActivity = mActivity;
        this.title = title;
        this.message = message;
        this.rightText = rightText;
        this.leftText = leftText;
        this.cancelable = cancelable;
        this.mNBasicAlertDialogListener = mNBasicAlertDialogListener;
        setDialog();
    }

    private void setDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

        if (title != null)
            builder.setTitle(title);
        if (message != null)
            builder.setMessage(message);

        builder.setCancelable(cancelable);

        if (leftText != null) {
            builder.setNegativeButton(leftText,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            try {
                                if (mNBasicAlertDialogListener != null)
                                    mNBasicAlertDialogListener.onLeftBtnClicked();
                                dialog.cancel();
                            } catch (Exception e) {
                                TLog.e(TAG + " onNegaviveButton OnClick", e);
                            }
                        }
                    });
        }

        if (rightText != null) {
            builder.setPositiveButton(rightText,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            try {
                                mNBasicAlertDialogListener.onRightBtnClicked();
                                mNBasicAlertDialogListener = null;
                                dialog.cancel();
                            } catch (Exception e) {
                                TLog.e(TAG + " onPositiveButton OnClick", e);
                            }
                        }
                    });
        }

        builder.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                isShowing = false;
            }
        });

        alert = builder.create();

        alert.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                isShowing = false;
            }
        });
    }

    /**
     * Will show the dialog. Will TLog.e if builder is null
     */
    public void show() {
        if (alert != null) {
            try {
                alert.show();
            } catch (Exception e) {
                TLog.e(TAG + " show", e);
            }
            isShowing = true;
        } else
            TLog.e(TAG + " show", "alert is null");
    }

    /**
     * Will cancel the dialog. Will TLog.e if builder is null
     */
    public void cancel() {
        if (alert != null) {
            alert.cancel();
            isShowing = false;
        } else
            TLog.e(TAG + " cancel", "alert is null");
    }

    public boolean isShowing() {
        return isShowing;
    }

    public void setText(String title, String yes, String no) {
        this.title = title;
        this.rightText = yes;
        this.leftText = no;
    }

    public TBasicAlertDialog setQueueTag(String mQueueTag) {
        this.mQueueTag = mQueueTag;
        return this;
    }

    public TBasicAlertDialog setQueueTagAsMessage() {
        this.mQueueTag = message;
        return this;
    }

    public interface NBasicAlertDialogListener {
        void onLeftBtnClicked();

        void onRightBtnClicked();
    }
}