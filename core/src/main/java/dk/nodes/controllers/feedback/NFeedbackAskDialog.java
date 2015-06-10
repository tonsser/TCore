package dk.nodes.controllers.feedback;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import dk.nodes.controllers.feedback.util.NFeedbackOptions;
import dk.nodes.controllers.feedback.util.NFeedbackSharedPrefKey;
import dk.nodes.controllers.font.NFontContainer;
import dk.nodes.ncore.R;
import dk.nodes.widgets.dialogs.NDialog;

public class NFeedbackAskDialog extends NDialog{

	private TextView headerTv;
	private Button yesBtn;
	private Button noBtn;
	private CheckBox neverAgainCb;
	private Activity mActivity;
	private NFeedbackOptions mNFeedbackOptions;
	private OnNFeedbackAskDialogListener mOnNFeedbackListener;


	public NFeedbackAskDialog(Activity mActivity, NFeedbackOptions mNFeedbackOptions , final OnNFeedbackAskDialogListener mOnNFeedbackListener) {
		super(mActivity, R.style.Theme_AppCompat_Dialog);
		setContentView(mNFeedbackOptions.getPopupContentViewResource());
		
		this.mNFeedbackOptions = mNFeedbackOptions;
		this.mActivity = mActivity;
		this.mOnNFeedbackListener = mOnNFeedbackListener;
		
		init();
	}

	private void init(){
		headerTv = (TextView) findViewById(R.id.dialog_ask_for_feedback_header_tv);
		headerTv.setText(this.mNFeedbackOptions.getPopupTitle());

		yesBtn = (Button) findViewById(R.id.dialog_ask_for_feedback_yes_btn);
		yesBtn.setText(this.mNFeedbackOptions.getYes());

		noBtn = (Button) findViewById(R.id.dialog_ask_for_feedback_no_btn);
		noBtn.setText(this.mNFeedbackOptions.getNo());

		neverAgainCb = (CheckBox) findViewById(R.id.dialog_ask_for_feedback_never_again_cb);
		neverAgainCb.setText(this.mNFeedbackOptions.getPopupNeverAskAgain());

		NFontContainer.setFont(mNFeedbackOptions.getPopupHeaderFont(), headerTv);
		NFontContainer.setFont(mNFeedbackOptions.getPopupCheckboxFont(), neverAgainCb);
		NFontContainer.setFont(mNFeedbackOptions.getPopupBtnsFont(), yesBtn, noBtn);

		yesBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(mOnNFeedbackListener != null)
					mOnNFeedbackListener.onYes();

				saveNeverAgainState();
				dismiss();
			}
		});

		noBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				saveNeverAgainState();
				dismiss();
			}
		});
	}

	private void saveNeverAgainState(){
		if(neverAgainCb == null)
			return;

		SharedPreferences sharedPref = mActivity.getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putBoolean(NFeedbackSharedPrefKey.key, neverAgainCb.isChecked());
		editor.commit();
	}

	public interface OnNFeedbackAskDialogListener{
		public void onYes();
	}
}
