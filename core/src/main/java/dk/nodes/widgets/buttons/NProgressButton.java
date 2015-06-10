package dk.nodes.widgets.buttons;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

public class NProgressButton extends FrameLayout {

	private Button mNButton;
	private ProgressBar mProgressBar;
	private String storedButtonText;


	public NProgressButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public NProgressButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public NProgressButton(Context context) {
		super(context);
		init(context);
	}

	private void init(Context mContext){
		mNButton = new Button(mContext);
		mProgressBar = new ProgressBar(mContext,null,android.R.attr.progressBarStyleSmall);
		mProgressBar.setVisibility(View.GONE);
		FrameLayout.LayoutParams flLpForSpinner = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		flLpForSpinner.gravity = Gravity.CENTER;
		FrameLayout.LayoutParams flLpForNButton = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		
		addView(mNButton,flLpForNButton);
		addView(mProgressBar,flLpForSpinner);
	}
	
	public void setProgressBar(ProgressBar mProgressBar){
		this.mProgressBar = mProgressBar;
	}
	
	public Button getNButton(){
		return mNButton;
	}
	
	public ProgressBar getProgressBar(){
		return mProgressBar;
	}
	
	public String startProgressAndRemoveButtonText(){
		mProgressBar.setVisibility(View.VISIBLE);
		storedButtonText = (String) mNButton.getText();
		mNButton.setText("");
		mNButton.setEnabled(false);
		
		return storedButtonText;
	}
	
	public void stopProgressAndSetButtonTextToStored(){
		mProgressBar.setVisibility(View.GONE);
		mNButton.setEnabled(true);
		mNButton.setText(storedButtonText);
	}
	
	public void stopProgressAndSetButtonToText(String newText){
		mProgressBar.setVisibility(View.GONE);
		mNButton.setEnabled(true);
		mNButton.setText(newText);
	}
	
	public String getStoredButtonText(){
		return storedButtonText;
	}
}
