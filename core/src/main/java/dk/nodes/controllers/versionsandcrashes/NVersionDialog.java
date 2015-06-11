package dk.nodes.controllers.versionsandcrashes;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import dk.nodes.controllers.font.NFontContainer;
import dk.nodes.controllers.versionsandcrashes.util.NVersionOptions;
import dk.nodes.controllers.versionsandcrashes.util.NVersionType;
import dk.nodes.ncore.R;
import dk.nodes.utils.NAndroidIntents;
import dk.nodes.widgets.dialogs.NDialog;

public class NVersionDialog extends NDialog{

	private NVersionOptions mNVersionOptions;
	private TextView headerTv;
	private NVersionType type;
	private String message;
	private TextView messageTv;
	private Button cancelBtn;
	private Button storeBtn;
	private Activity mActivity;
	private FrameLayout btnSeperatorFl;

	public NVersionDialog(Activity mActivity, NVersionOptions mNVersionOptions, NVersionType type, String message) {
		super(mActivity, R.style.Theme_AppCompat_Dialog);
		AUTO_DISMISS_ON_FOCUS_LOST = false;

		this.mActivity = mActivity;
		this.mNVersionOptions = mNVersionOptions;

		this.type = type;
		this.message = message;

		setContentView(mNVersionOptions.getContentViewResource());
		initResources();

		if(type == NVersionType.FORCE_UPDATE){
			setCancelable(false);
			setCanceledOnTouchOutside(false);
		}
		else{
			setCancelable(true);
			setCanceledOnTouchOutside(true);
		}
	}

	private void initResources() {
		//Title
		headerTv = (TextView) findViewById(R.id.dialog_nversion_header_tv);
		NFontContainer.setFont(mNVersionOptions.getHeaderFont(), headerTv);

		if(type == NVersionType.NEW_IN_VERSION){
			headerTv.setText(mNVersionOptions.getNewInThisVersionHeader());
		}
		else if(type == NVersionType.UPDATE || type == NVersionType.FORCE_UPDATE){
			headerTv.setText(mNVersionOptions.getUpdateHeader());
		}
		else if(type == NVersionType.ALERT){
			if(mNVersionOptions.getNVersionAlertOptions().getHeader() != null)
				headerTv.setText(mNVersionOptions.getNVersionAlertOptions().getHeader());
		}

		//Message
		messageTv = (TextView) findViewById(R.id.dialog_nversion_message_tv);
		NFontContainer.setFont(mNVersionOptions.getMessageFont(), messageTv);

		//NEW IN VERSION
		if(type == NVersionType.NEW_IN_VERSION && message != null){
			messageTv.setText(message);
		}
		//UPDATE
		else if(type == NVersionType.UPDATE){
			if(message != null)
				messageTv.setText(mNVersionOptions.getUpdateMessage() + "\n\n" + message);
			else
				messageTv.setText(mNVersionOptions.getUpdateMessage());
		}
		//FORCE UPDATE
		else if(type == NVersionType.FORCE_UPDATE){
			if(message != null)
				messageTv.setText(mNVersionOptions.getUpdateMessageForced()+ "\n\n" +message);
			else
				messageTv.setText(mNVersionOptions.getUpdateMessageForced());
		}
		else if(type == NVersionType.ALERT){
			if(mNVersionOptions.getNVersionAlertOptions().getMessage() != null)
				messageTv.setText(mNVersionOptions.getNVersionAlertOptions().getMessage());
		}
		
		//Cancel btn
		cancelBtn = (Button) findViewById(R.id.dialog_nversion_cancel_btn);
		NFontContainer.setFont(mNVersionOptions.getBtnFont(), cancelBtn);

		if(type == NVersionType.FORCE_UPDATE)
			cancelBtn.setVisibility(View.GONE);
		else
			cancelBtn.setVisibility(View.VISIBLE);

		if(type == NVersionType.NEW_IN_VERSION){
			cancelBtn.setText(mNVersionOptions.getNewInThisVersionOkBtn());
		}
		else if(type == NVersionType.ALERT){
			cancelBtn.setText(mNVersionOptions.getNewInThisVersionOkBtn()); // Just using this even though it should have its own!
		}
		else{
			cancelBtn.setText(mNVersionOptions.getUpdateNoBtn());
		}

		cancelBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

		//Store btn
		storeBtn = (Button) findViewById(R.id.dialog_nversion_store_btn);
		NFontContainer.setFont(mNVersionOptions.getBtnFont(), storeBtn);

		storeBtn.setText(mNVersionOptions.getUpdateGoToStoreBtn());

		if(type == NVersionType.NEW_IN_VERSION || type == NVersionType.ALERT)
			storeBtn.setVisibility(View.GONE);
		else
			storeBtn.setVisibility(View.VISIBLE);

		storeBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				NAndroidIntents.toMarket(mActivity);

				if(type == NVersionType.UPDATE)
					dismiss();
			}
		});

		//Sperator
		btnSeperatorFl = (FrameLayout) findViewById(R.id.dialog_nversion_btn_seperator_fl);
		if(btnSeperatorFl != null){
			if(cancelBtn.getVisibility() == View.VISIBLE && storeBtn.getVisibility() == View.VISIBLE)
				btnSeperatorFl.setVisibility(View.VISIBLE);
			else
				btnSeperatorFl.setVisibility(View.GONE);
		}
	}
}
