package dk.nodes.controllers.feedback.util;

import android.graphics.Typeface;

import com.google.gson.Gson;

import dk.nodes.ncore.R;

public class NFeedbackOptions {

	//Fonts popup
	private Typeface popupHeaderFont;
	private Typeface popupCheckboxFont;
	private Typeface popupBtnsFont;
	
	//Fonts feedback
	private Typeface feedbackHederFont;
	private Typeface feedbackBtnsFont;
	private Typeface feedbackEditTextFont;
	
	//Util
	private String yes = "Yes";
	private String no = "No";
	
	//Popup
	private int popupContentViewResource = R.layout.dialog_nfeedback_light;
	private String popupHeader = "Want to give feedback?";
	private String popupNeverAskAgain = "Never ask again";

	//Feedback
	private int feedbackContentViewResource = R.layout.activity_nfeedback_light;
	private String feedbackHeader = "Developer feedback";
	private String feedbackNameHint = "Hint";
	private String feedbackFeedbackHint = "Feedback here...";
	private String feedbackAddScreenshotBtn = "Add screenshot";
	private String feedbackReplaceScreenshotBtn = "Replace screenshot";
	private String feedbackSendBtn = "Send";
	private String feedbackSelectScreenshot = "Select screenshot";
	private String feedbackScreenshotAdded = "Screenshot added";
	private String feedbackAlertValidationNameAndFeedbackReq = "Name and feedback is required to continue";
	private String feedbackErrorWithUpload = "Error with upload";
	private String feedbackErrorWithConnection = "Error with connection, try again";
	private String feedbackFeedbackSubmitted = "Feedback submitted";
	
	//Settings
	private NFeedbackSelectedStateType selectStateType = NFeedbackSelectedStateType.DARKER;
	
	public void setStrings(String yes, String no,
			String popupHeader, String popupNeverAskAgain, String feedbackHeader, String nameHint, String feedbackHint,
			String addScreenshotBtn, String replaceScreenshotBtn,String sendBtn, String selectScreenshot,
			String screenshotAdded, String alertValidationNameAndFeedbackReq,
			String errorWithUpload, String errorWithConnection,	String feedbackSubmitted) {
	
		this.yes = yes;
		this.no = no;
		this.popupHeader = popupHeader;
		this.popupNeverAskAgain = popupNeverAskAgain;
		this.feedbackHeader = feedbackHeader;
		this.feedbackNameHint = nameHint;
		this.feedbackFeedbackHint = feedbackHint;
		this.feedbackAddScreenshotBtn = addScreenshotBtn;
		this.feedbackReplaceScreenshotBtn = replaceScreenshotBtn;
		this.feedbackSendBtn = sendBtn;
		this.feedbackSelectScreenshot = selectScreenshot;
		this.feedbackScreenshotAdded = screenshotAdded;
		this.feedbackAlertValidationNameAndFeedbackReq = alertValidationNameAndFeedbackReq;
		this.feedbackErrorWithUpload = errorWithUpload;
		this.feedbackErrorWithConnection = errorWithConnection;
		this.feedbackFeedbackSubmitted = feedbackSubmitted;
	}
	
	public void setLightTheme(){
		popupContentViewResource = R.layout.dialog_nfeedback_light;
		feedbackContentViewResource = R.layout.activity_nfeedback_light;
		selectStateType = NFeedbackSelectedStateType.DARKER;
	}
	
	public void setDarkTheme(){
		popupContentViewResource = R.layout.dialog_nfeedback_dark;
		feedbackContentViewResource = R.layout.activity_nfeedback_dark;
		selectStateType = NFeedbackSelectedStateType.LIGHTER;
	}
	
	public void setCustomTheme(int popupContentViewResource, int feedbackContentViewResource){
		this.popupContentViewResource = popupContentViewResource;
		this.feedbackContentViewResource = feedbackContentViewResource;
		selectStateType = NFeedbackSelectedStateType.NONE;
	}

	public void setFont(Typeface headerFont, Typeface popupBtnsFont, Typeface popupCheckboxFont, Typeface feedbackHederFont,
			Typeface feedbackEditTextFont, Typeface feedbackBtnsFont){
		this.popupHeaderFont = headerFont;
		this.popupBtnsFont = popupBtnsFont;
		this.popupCheckboxFont = popupCheckboxFont;
		
		this.feedbackHederFont = feedbackHederFont;
		this.feedbackEditTextFont = feedbackEditTextFont;
		this.feedbackBtnsFont = feedbackBtnsFont;
	}

	public Typeface getPopupHeaderFont() {
		return popupHeaderFont;
	}

	public Typeface getPopupCheckboxFont() {
		return popupCheckboxFont;
	}

	public Typeface getPopupBtnsFont() {
		return popupBtnsFont;
	}

	public Typeface getFeedbackHederFont() {
		return feedbackHederFont;
	}

	public Typeface getFeedbackBtnsFont() {
		return feedbackBtnsFont;
	}

	public Typeface getFeedbackEditTextFont() {
		return feedbackEditTextFont;
	}

	public int getPopupContentViewResource() {
		return popupContentViewResource;
	}
	
	public String getJSON(){
		return new Gson().toJson(this);
	}

	public String getYes() {
		return yes;
	}

	public String getNo() {
		return no;
	}

	public String getPopupTitle() {
		return popupHeader;
	}

	public String getPopupNeverAskAgain() {
		return popupNeverAskAgain;
	}

	public int getFeedbackContentViewResource() {
		return feedbackContentViewResource;
	}

	public NFeedbackSelectedStateType getSelectStateType() {
		return selectStateType;
	}

	public void setSelectStateType(NFeedbackSelectedStateType selectStateType) {
		this.selectStateType = selectStateType;
	}

	public String getPopupHeader() {
		return popupHeader;
	}

	public String getFeedbackHeader() {
		return feedbackHeader;
	}

	public String getFeedbackNameHint() {
		return feedbackNameHint;
	}

	public String getFeedbackFeedbackHint() {
		return feedbackFeedbackHint;
	}

	public String getFeedbackAddScreenshotBtn() {
		return feedbackAddScreenshotBtn;
	}

	public String getFeedbackReplaceScreenshotBtn() {
		return feedbackReplaceScreenshotBtn;
	}

	public String getFeedbackSendBtn() {
		return feedbackSendBtn;
	}

	public String getFeedbackSelectScreenshot() {
		return feedbackSelectScreenshot;
	}

	public String getFeedbackScreenshotAdded() {
		return feedbackScreenshotAdded;
	}

	public String getFeedbackAlertValidationNameAndFeedbackReq() {
		return feedbackAlertValidationNameAndFeedbackReq;
	}

	public String getFeedbackErrorWithUpload() {
		return feedbackErrorWithUpload;
	}

	public String getFeedbackErrorWithConnection() {
		return feedbackErrorWithConnection;
	}

	public String getFeedbackFeedbackSubmitted() {
		return feedbackFeedbackSubmitted;
	}
}
