/**
 * @author Casper Rasmussen 2012
 */
package dk.tonsser.widgets.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;

import java.util.List;

import dk.tonsser.controllers.dialogqueuing.NDialogQueueInterface;
import dk.tonsser.controllers.dialogqueuing.NDialogQueueListener;
import dk.tonsser.utils.NLog;

public class NListPickerDialog implements NDialogQueueInterface {
	private boolean isShowing = false;
	private AlertDialog alert;
	private OnNListPickerDialogShowDismissListener mOnNListPickerDialogShowDismissListener;
	private NDialogQueueListener mQueueListener;
	private String mQueueTag;
	/**
	 * Use this constructor if your list is an ArrayList<String>, remember to call show()
	 * @param mActivity
	 * @param title
	 * @param list
	 * @param mNListPick
	 */
	public NListPickerDialog(Activity mActivity, String title, List<String> list,final DialogInterface.OnClickListener mNListPick ){
		String[] array = new String[list.size()];
		for(int i = 0 ; i < list.size() ; i++) {
			array[i] = list.get(i);
		}
		setDialog(mActivity, title, array, mNListPick);
	}

	/**
	 * Use this constructor if your list is an String[], remember to call show()
	 * @param mActivity
	 * @param title
	 * @param array
	 * @param mNListPick
	 */
	public NListPickerDialog(Activity mActivity, String title, String[] array,final DialogInterface.OnClickListener mNListPick ){
		setDialog(mActivity, title, array, mNListPick);
	}

	/**
	 * Use this method to set a listener on show/hide
	 * @param mOnNListPickerDialogShowDismissListener
	 */
	public void setOnShowListener(OnNListPickerDialogShowDismissListener mOnNListPickerDialogShowDismissListener){
		this.mOnNListPickerDialogShowDismissListener = mOnNListPickerDialogShowDismissListener;
	}

	private void setDialog(Activity mActivity, String title, String[] array,final DialogInterface.OnClickListener mNListPick){
		AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
		builder.setTitle(title);
		builder.setItems(array, new OnClickListener() {
			
			@Override
			public void onClick( DialogInterface dialog, int which ) {
				// TODO Auto-generated method stub
				mNListPick.onClick( dialog, which );
			}
		});
		builder.setOnCancelListener(new OnCancelListener(){
			@Override
			public void onCancel(DialogInterface dialog) {
				isShowing = false;
				if(mOnNListPickerDialogShowDismissListener!=null)
					mOnNListPickerDialogShowDismissListener.onDismiss();
				
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
		if(alert!=null){
			alert.show();
			isShowing =	true;
			if(mOnNListPickerDialogShowDismissListener!=null)
				mOnNListPickerDialogShowDismissListener.onShow();
		}
		else
			NLog.e("NQuitDialog show", "alert is null");
	}
	/**
	 * Will cancel the dialog. Will NLog.e if builder is null
	 */
	public void cancel(){
		if(alert!=null){
			alert.cancel();
			isShowing =	false;
			if(mOnNListPickerDialogShowDismissListener!=null)
				mOnNListPickerDialogShowDismissListener.onDismiss();
		}
		else
			NLog.e("NQuitDialog cancel", "alert is null");
	}

	public boolean isShowing() {
		return isShowing;
	}
	
	public void setQueueListener( NDialogQueueListener mQueueListener ) {
		this.mQueueListener = mQueueListener;
	}
	
	public interface OnNListPickerDialogShowDismissListener{
		void onShow();
		void onDismiss();
	}

	@Override
	public String getQueueTag() {
		return mQueueTag;
	}
	
	public void setQueueTag(String mQueueTag){
		this.mQueueTag = mQueueTag;
	}
}