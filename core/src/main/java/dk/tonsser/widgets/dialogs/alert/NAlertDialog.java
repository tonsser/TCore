package dk.tonsser.widgets.dialogs.alert;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.tonsser.core.R;

import dk.tonsser.controllers.font.NFontContainer;
import dk.tonsser.utils.NLog;
import dk.tonsser.widgets.dialogs.NDialog;
import dk.tonsser.widgets.dialogs.alert.util.NAlertDialogOptions;
import dk.tonsser.widgets.dialogs.alert.util.NAlertListener;

//

public class NAlertDialog extends NDialog {

	private String TAG = NAlertDialogOptions.class.getName();

	private NAlertListener mNAlertListener;
	private NAlertDialogOptions mNAlertDialogOptions;

	private TextView headerTv;
	private TextView messageTv;
	private Button leftBtn;
	private Button rightBtn;
	private FrameLayout topSeperatorFl;
	private FrameLayout betweenBtnsSeperatorFl;

	private String queueTag;

	public NAlertDialog(Context context, NAlertDialogOptions mNAlertDialogOptions, NAlertListener mNAlertListener) {
		super(context, R.style.Theme_AppCompat_Dialog);

		this.mNAlertDialogOptions = mNAlertDialogOptions;
		this.mNAlertListener = mNAlertListener;

		applyNAlertDialogOptions();
	}

	public void applyNAlertDialogOptions() {
		if(mNAlertDialogOptions == null){
			NLog.e(TAG + " applyNAlertDialogOptions", "NAlertDialogOptions is null");
			return;
		}

		setContentView(mNAlertDialogOptions.getContentViewResource());
		initResources();
		applyDialogSettings();	
	}

	private void applyDialogSettings() {
		setCancelable(mNAlertDialogOptions.isCancelable());
		setCanceledOnTouchOutside(mNAlertDialogOptions.isCancelOnTouchOutside());
		AUTO_DISMISS_ON_FOCUS_LOST = mNAlertDialogOptions.isAutoDismisWhenFocusLost();
	}

	private void initResources() {
		//Header
		headerTv = (TextView) findViewById(R.id.dialog_nalert_header_tv);
		if(headerTv != null){
			if(mNAlertDialogOptions.getHeader() != null){
				headerTv.setVisibility(View.VISIBLE);
				headerTv.setText(mNAlertDialogOptions.getHeader());
				NFontContainer.setFont(mNAlertDialogOptions.getHeaderFont(), headerTv);
			}
			else
				headerTv.setVisibility(View.GONE);
		}

		//Message
		messageTv = (TextView) findViewById(R.id.dialog_nalert_message_tv);
		if(messageTv != null){
			if(mNAlertDialogOptions.getMessage() != null){
				messageTv.setVisibility(View.VISIBLE);
				messageTv.setText(mNAlertDialogOptions.getMessage());
				NFontContainer.setFont(mNAlertDialogOptions.getMessageFont(), messageTv);
			}
			else
				messageTv.setVisibility(View.GONE);
		}

		//Left btn
		leftBtn = (Button) findViewById(R.id.dialog_nalert_left_btn);
		if(leftBtn != null){
			if(mNAlertDialogOptions.getLeftBtn() != null){
				leftBtn.setVisibility(View.VISIBLE);
				leftBtn.setText(mNAlertDialogOptions.getLeftBtn());
				NFontContainer.setFont(mNAlertDialogOptions.getBtnFont(), leftBtn);
			}
			else
				leftBtn.setVisibility(View.GONE);

			leftBtn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if(mNAlertDialogOptions.isDismissOnBtnClick())
						dismiss();

					if(mNAlertListener != null)
						mNAlertListener.onLeftClicked();
				}
			});
		}

		//Right btn
		rightBtn = (Button) findViewById(R.id.dialog_nalert_right_btn);
		if(rightBtn != null){
			if(mNAlertDialogOptions.getRightBtn() != null){
				rightBtn.setVisibility(View.VISIBLE);
				rightBtn.setText(mNAlertDialogOptions.getRightBtn());
				NFontContainer.setFont(mNAlertDialogOptions.getBtnFont(), rightBtn);
			}
			else
				rightBtn.setVisibility(View.GONE);

			rightBtn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if(mNAlertDialogOptions.isDismissOnBtnClick())
						dismiss();

					if(mNAlertListener != null)
						mNAlertListener.onRightClicked();
				}
			});
		}

		//Seperators
		topSeperatorFl = (FrameLayout) findViewById(R.id.dialog_nalert_top_btn_seperator_fl);
		betweenBtnsSeperatorFl = (FrameLayout) findViewById(R.id.dialog_nalert_btn_seperator_fl);

		if((leftBtn == null || leftBtn.getVisibility() == View.GONE) && (rightBtn == null || rightBtn.getVisibility() == View.GONE)){
			if(topSeperatorFl != null)
				topSeperatorFl.setVisibility(View.GONE);
			if(betweenBtnsSeperatorFl != null)
				betweenBtnsSeperatorFl.setVisibility(View.GONE);
		}
		else{
			if(topSeperatorFl != null)
				topSeperatorFl.setVisibility(View.VISIBLE);
			if(betweenBtnsSeperatorFl != null)
				betweenBtnsSeperatorFl.setVisibility(View.VISIBLE);
		}
	}

	public NAlertDialog setOptions(NAlertDialogOptions mNAlertDialogOptions){
		this.mNAlertDialogOptions = mNAlertDialogOptions;
		return this;
	}

	public NAlertDialog setQueueTagAsMessage(){
		queueTag = mNAlertDialogOptions.getMessage();

		return this;
	}

	@Override
	public String getQueueTag() {
		if(queueTag != null)
			return queueTag;
		else if(mNAlertDialogOptions == null)
			return null;
		else
			return mNAlertDialogOptions.getHeader()+mNAlertDialogOptions.getMessage();
	}


}