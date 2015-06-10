package dk.nodes.controllers.feedback;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import dk.nodes.base.NBaseActivity;
import dk.nodes.controllers.NStringController;
import dk.nodes.controllers.feedback.util.NFeedbackOptions;
import dk.nodes.controllers.feedback.webservice.NFeedbackAsync;
import dk.nodes.controllers.font.NFontContainer;
import dk.nodes.ncore.R;
import dk.nodes.utils.NBuild;
import dk.nodes.utils.NLog;
import dk.nodes.utils.NToast;
import dk.nodes.webservice.models.NApiAsyncListener;

public class NFeedbackActivity extends NBaseActivity{
	private String TAG = NFeedbackActivity.class.getName();

	private Button addImageBtn;
	private int SELECT_PICTURE_REQUEST_ID = 133712312; // just something random
	private Bitmap mBitmap;
	private Button uploadFeedbackBtn;
	private EditText nameEt;
	private EditText messageEt;
	private TextView headerTv;
	private NFeedbackOptions mNFeedbackOptions;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		SHAKE_FEEDBACK_ENABLED = false;

		mNFeedbackOptions = NFeedback.getInstance().getOptions();
		setContentView(mNFeedbackOptions.getFeedbackContentViewResource());

		initResources();
	}

	public void onResume(){
		super.onResume();
		setImageUploadBtn();
	}

	private void initResources() {
		headerTv = (TextView) findViewById(R.id.activity_feedback_header_tv);
		headerTv.setText(mNFeedbackOptions.getFeedbackHeader());

		nameEt = (EditText) findViewById(R.id.activity_feedback_name_et);
		nameEt.setHint(mNFeedbackOptions.getFeedbackNameHint());

		messageEt = (EditText) findViewById(R.id.activity_feedback_feedback_et);
		messageEt.setHint(mNFeedbackOptions.getFeedbackFeedbackHint());

		//Add image
		addImageBtn = (Button) findViewById(R.id.activity_feedback_image_btn);
		addImageBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				openGallary();
			}
		});

		//Upload btn
		uploadFeedbackBtn = (Button) findViewById(R.id.activity_feedback_send_btn);
		uploadFeedbackBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(checkIfNameAndMessageIsFilled()){
					uploadFeedback();
				}else{
					NToast.execute(mActivity, mNFeedbackOptions.getFeedbackAlertValidationNameAndFeedbackReq());
				}
			}
		});

		NFontContainer.setFont(mNFeedbackOptions.getFeedbackHederFont(), headerTv);
		NFontContainer.setFont(mNFeedbackOptions.getFeedbackEditTextFont(), nameEt, messageEt);
		NFontContainer.setFont(mNFeedbackOptions.getFeedbackBtnsFont(), addImageBtn, uploadFeedbackBtn);
	}

	protected void uploadFeedback() {
		final ProgressDialog progressDialog = ProgressDialog.show(mActivity, "Loading...", null, true, true);
		new NFeedbackAsync(mBitmap, NBuild.getPackageName(mActivity), nameEt.getText().toString(), 
				messageEt.getText().toString(),NBuild.getVersionName(this), android.os.Build.MODEL,
				new NApiAsyncListener() {

			@Override
			public void onSuccess(int code) {
				NToast.execute(mActivity, mNFeedbackOptions.getFeedbackFeedbackSubmitted());
				finish();
			}

			@Override
			public void onError(int code) {
				if(code == 406)
					NLog.e(TAG+" Upload error"," The bundle is not used in any nodescamp projects, please add it!");
				NToast.executeShort(mActivity, mNFeedbackOptions.getFeedbackErrorWithUpload());
			}

			@Override
			public void onConnectionError(int code) {
				NToast.executeShort(mActivity, mNFeedbackOptions.getFeedbackErrorWithConnection());				
			}

			@Override
			public void onAlways() {
				if(progressDialog != null) {
					progressDialog.dismiss();
				}
			}
		}).execute();
	}

	protected boolean checkIfNameAndMessageIsFilled() {
		if(NStringController.hasValue(nameEt.getText().toString()) && NStringController.hasValue(messageEt.getText().toString()))
			return true;
		else
			return false;
	}

	public void openGallary(){
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, mNFeedbackOptions.getFeedbackSelectScreenshot()), SELECT_PICTURE_REQUEST_ID);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == SELECT_PICTURE_REQUEST_ID) {
				try {
					Uri selectedImageUri = data.getData();
					mBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
					if(mBitmap != null){
						NToast.executeShort(mActivity, mNFeedbackOptions.getFeedbackScreenshotAdded());
					}
					setImageUploadBtn();
				}
				catch(Exception e){
					NLog.e(TAG  + " onActivityResult", e);
				}
			}
		} else if (resultCode == RESULT_CANCELED) {

		}
	}

	private void setImageUploadBtn(){
		if(mBitmap != null)
			addImageBtn.setText(mNFeedbackOptions.getFeedbackReplaceScreenshotBtn());
		else
			addImageBtn.setText(mNFeedbackOptions.getFeedbackAddScreenshotBtn());
	}
}
