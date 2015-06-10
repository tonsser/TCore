package dk.nodes.widgets.dialogs.alert.util;

import android.graphics.Typeface;

import dk.nodes.ncore.R;

public class NAlertDialogOptions {

	//Fonts
	private Typeface headerFont;
	private Typeface messageFont;
	private Typeface btnFont;

	//Strings
	private String header;
	private String message;
	private String leftBtn;
	private String rightBtn;
	
	//Resource
	private int contentViewResource = R.layout.dialog_nalert_light;
	
	//Selected state type
	private NAlertDialogSelectedStateType selectedStateType = NAlertDialogSelectedStateType.DARKER;
		
	//Settings
	private boolean autoDismisWhenFocusLost = true;
	private boolean cancelable = true;
	private boolean cancelOnTouchOutside = true;
	private boolean dismissOnBtnClick = true;
	
	
	public boolean isAutoDismisWhenFocusLost() {
		return autoDismisWhenFocusLost;
	}


	public NAlertDialogOptions setAutoDismisWhenFocusLost(boolean autoDismisWhenFocusLost) {
		this.autoDismisWhenFocusLost = autoDismisWhenFocusLost;
		return this;
	}


	public boolean isCancelable() {
		return cancelable;
	}


	public NAlertDialogOptions setCancelable(boolean cancelable) {
		this.cancelable = cancelable;
		return this;
	}


	public boolean isCancelOnTouchOutside() {
		return cancelOnTouchOutside;
	}


	public NAlertDialogOptions setCancelOnTouchOutside(boolean cancelOnTouchOutside) {
		this.cancelOnTouchOutside = cancelOnTouchOutside;
		return this;
	}


	public NAlertDialogOptions setStrings(String header, String message, String leftBtn, String rightBtn){
		this.header = header;
		this.message = message;
		this.leftBtn = leftBtn;
		this.rightBtn = rightBtn;
		
		return this;
	}
	

	public NAlertDialogOptions setFonts(Typeface headerFont, Typeface messageFont, Typeface btnFont){
		this.headerFont = headerFont;
		this.messageFont = messageFont;
		this.btnFont = btnFont;
		
		return this;
	}
	
	public NAlertDialogOptions setCustomTheme(int contentViewResource) {
		this.contentViewResource = contentViewResource;
		selectedStateType = NAlertDialogSelectedStateType.NONE;
		
		return this;
	}
	
	public NAlertDialogOptions setLightTheme(){
		contentViewResource = R.layout.dialog_nalert_light;
		selectedStateType = NAlertDialogSelectedStateType.DARKER;
		
		return this;
	}
	
	public NAlertDialogOptions setDarkTheme(){
		contentViewResource = R.layout.dialog_nalert_dark;
		selectedStateType = NAlertDialogSelectedStateType.LIGHTER;
		
		return this;
	}

	public NAlertDialogOptions setSelectedStateType(NAlertDialogSelectedStateType selectedStateType) {
		this.selectedStateType = selectedStateType;
		
		return this;
	}
	
	public Typeface getHeaderFont() {
		return headerFont;
	}

	public Typeface getMessageFont() {
		return messageFont;
	}

	public Typeface getBtnFont() {
		return btnFont;
	}

	public int getContentViewResource() {
		return contentViewResource;
	}

	public NAlertDialogSelectedStateType getSelectedStateType() {
		return selectedStateType;
	}

	public String getHeader() {
		return header;
	}

	public String getMessage() {
		return message;
	}

	public String getLeftBtn() {
		return leftBtn;
	}

	public String getRightBtn() {
		return rightBtn;
	}


	public boolean isDismissOnBtnClick() {
		return dismissOnBtnClick;
	}


	public NAlertDialogOptions setDismissOnBtnClick(boolean dismissOnBtnClick) {
		this.dismissOnBtnClick = dismissOnBtnClick;
		
		return this;
	}
}