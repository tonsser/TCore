package dk.nodes.widgets.dialogs;
/**
 * @author Casper Rasmussen - 2012
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.text.Spanned;

import dk.nodes.controllers.dialogqueuing.NDialogQueueInterface;
import dk.nodes.controllers.dialogqueuing.NDialogQueueListener;
import dk.nodes.utils.NLog;

public class NPickSpannedDialog1 implements NDialogQueueInterface {
	private Activity mActivity;
	private Spanned dialogText = null;
	private String okText ="Ok";
	private boolean cancelable = false;
	private AlertDialog alert;
	private OnNPickSpannedDialogListener1 mOnNPickDialogListener1;
	private NDialogQueueListener mQueueListener;
	private String mQueueTag;
	/**
	 * Remember to call show() afterwards
	 * @param mActivity
	 * @param title
	 * @param yesText
	 * @param noText
	 * @param cancelable
	 * @param mOnNPickDialogListener1
	 */
	public NPickSpannedDialog1(Activity mActivity, Spanned dialogText, String okText, boolean cancelable,OnNPickSpannedDialogListener1 mOnNPickDialogListener1){
		this.mActivity = mActivity;
		this.dialogText = dialogText;
		this.okText = okText;
		this.cancelable = cancelable;
		this.mOnNPickDialogListener1 = mOnNPickDialogListener1;
		setDialog();
	}

	private void setDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
		builder.setMessage(dialogText).setCancelable(cancelable)
		.setPositiveButton(okText,
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				mOnNPickDialogListener1.onOk();
				mOnNPickDialogListener1 = null;
				dialog.cancel();
			}
		});
		builder.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				if(mOnNPickDialogListener1!=null)
					mOnNPickDialogListener1.onOk();
				
				if( mQueueListener != null )
					mQueueListener.onGone();
			}
		});

		alert = builder.create();

	}
	/**
	 * Will show the dialog. Will NLog.e if builder is null
	 */
	public void show(){
		if(alert!=null)
			alert.show();
		else
			NLog.e("NQuitDialog show", "alert is null");
	}
	/**
	 * Will cancel the dialog. Will NLog.e if builder is null
	 */
	public void cancel(){
		if(alert!=null) {
			alert.cancel();
		}
			
		else
			NLog.e("NQuitDialog cancel", "alert is null");
	}
	
	public void setQueueListener( NDialogQueueListener mQueueListener ) {
		this.mQueueListener = mQueueListener;
	}

	public interface OnNPickSpannedDialogListener1{
		public void onOk();
	}

	@Override
	public String getQueueTag() {
		return mQueueTag;
	}
	
	public NPickSpannedDialog1 setQueueTag(String mQueueTag){
		this.mQueueTag = mQueueTag;
		return this;
	}
}
