package dk.nodes.controllers.versionsandcrashes;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import dk.nodes.controllers.font.NFontContainer;
import dk.nodes.controllers.versionsandcrashes.util.NVersionOtions;
import dk.nodes.controllers.versionsandcrashes.util.NVersionType;
import dk.nodes.ncore.R;
import dk.nodes.utils.NAndroidIntents;
import dk.nodes.widgets.dialogs.NDialog;

public class NVersionDialog extends NDialog{

	private NVersionOtions mNVersionOtions;
	private TextView headerTv;
	private NVersionType type;
	private String message;
	private TextView messageTv;
	private Button cancelBtn;
	private Button storeBtn;
	private Activity mActivity;
	private FrameLayout btnSeperatorFl;

	public NVersionDialog(Activity mActivity, NVersionOtions mNVersionOtions, NVersionType type, String message) {
		super(mActivity, R.style.Theme_AppCompat_Dialog);
		AUTO_DISMISS_ON_FOCUS_LOST = false;

		this.mActivity = mActivity;
		this.mNVersionOtions = mNVersionOtions;

		this.type = type;
		this.message = message;

		setContentView(mNVersionOtions.getContentViewResource());
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
		NFontContainer.setFont(mNVersionOtions.getHeaderFont(), headerTv);

		if(type == NVersionType.NEW_IN_VERSION){
			headerTv.setText(mNVersionOtions.getNewInThisVersionHeader());
		}
		else if(type == NVersionType.UPDATE || type == NVersionType.FORCE_UPDATE){
			headerTv.setText(mNVersionOtions.getUpdateHeader());
		}
		else if(type == NVersionType.ALERT){
			if(mNVersionOtions.getNVersionAlertOptions().getHeader() != null)
				headerTv.setText(mNVersionOtions.getNVersionAlertOptions().getHeader());
		}

		//Message
		messageTv = (TextView) findViewById(R.id.dialog_nversion_message_tv);
		NFontContainer.setFont(mNVersionOtions.getMessageFont(), messageTv);

		//NEW IN VERSION
		if(type == NVersionType.NEW_IN_VERSION && message != null){
			messageTv.setText(message);
		}
		//UPDATE
		else if(type == NVersionType.UPDATE){
			if(message != null)
				messageTv.setText(mNVersionOtions.getUpdateMessage() + "\n\n" + message);
			else
				messageTv.setText(mNVersionOtions.getUpdateMessage());
		}
		//FORCE UPDATE
		else if(type == NVersionType.FORCE_UPDATE){
			if(message != null)
				messageTv.setText(mNVersionOtions.getUpdateMessageForced()+ "\n\n" +message);
			else
				messageTv.setText(mNVersionOtions.getUpdateMessageForced());
		}
		else if(type == NVersionType.ALERT){
			if(mNVersionOtions.getNVersionAlertOptions().getMessage() != null)
				messageTv.setText(mNVersionOtions.getNVersionAlertOptions().getMessage());
		}
		
		//Cancel btn
		cancelBtn = (Button) findViewById(R.id.dialog_nversion_cancel_btn);
		NFontContainer.setFont(mNVersionOtions.getBtnFont(), cancelBtn);

		if(type == NVersionType.FORCE_UPDATE)
			cancelBtn.setVisibility(View.GONE);
		else
			cancelBtn.setVisibility(View.VISIBLE);

		if(type == NVersionType.NEW_IN_VERSION){
			cancelBtn.setText(mNVersionOtions.getNewInThisVersionOkBtn());
		}
		else if(type == NVersionType.ALERT){
			cancelBtn.setText(mNVersionOtions.getNewInThisVersionOkBtn()); // Just using this even though it should have its own!
		}
		else{
			cancelBtn.setText(mNVersionOtions.getUpdateNoBtn());
		}

		cancelBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

		//Store btn
		storeBtn = (Button) findViewById(R.id.dialog_nversion_store_btn);
		NFontContainer.setFont(mNVersionOtions.getBtnFont(), storeBtn);

		storeBtn.setText(mNVersionOtions.getUpdateGoToStoreBtn());

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
