package dk.nodes.controllers.versionsandcrashes.util;

import android.graphics.Typeface;

import dk.nodes.ncore.R;

public class NVersionOtions {

	//Fonts
	private Typeface headerFont;
	private Typeface messageFont;
	private Typeface btnFont;
	
	//Update Strings
	private String updateHeader = "New version";
	private String updateMessage = "Update available, want to download?";
	private String updateMessageForced = "Update available, you have to download.";
	private String updateGoToStoreBtn = "Play Store";
	private String updateNoBtn = "No";

	//New in version strings
	private String newInThisVersionHeader = "New in this version";
	private String newInThisVersionOkBtn = "Ok";
	
	//Resource
	private int contentViewResource = R.layout.dialog_nversion_light;
	
	//Selected state type
	private NVersionSelectedStateType selectedStateType = NVersionSelectedStateType.DARKER;
	
	//Alerts
	private NVersionAlertOptions mNVersionAlertOptions;
	
	public NVersionOtions(){
	}
	
	public void setStrings(String updateHeader, String updateMessage, String updateMessageForced, String updateGoToStoreBtn,
			String updateNoBtn, String newInThisVersionHeader) {
		this.updateHeader = updateHeader;
		this.updateMessage = updateMessage;
		this.updateMessageForced = updateMessageForced;
		this.updateGoToStoreBtn = updateGoToStoreBtn;
		this.updateNoBtn = updateNoBtn;
		this.newInThisVersionHeader = newInThisVersionHeader;
	}

	public void setFonts(Typeface headerFont, Typeface messageFont, Typeface btnFont){
		this.headerFont = headerFont;
		this.messageFont = messageFont;
		this.btnFont = btnFont;
	}
	
	public void setCustomTheme(int contentViewResource) {
		this.contentViewResource = contentViewResource;
		selectedStateType = NVersionSelectedStateType.NONE;
	}
	
	public void setLightTheme(){
		contentViewResource = R.layout.dialog_nversion_light;
		selectedStateType = NVersionSelectedStateType.DARKER;
	}
	
	public void setDarkTheme(){
		contentViewResource = R.layout.dialog_nversion_dark;
		selectedStateType = NVersionSelectedStateType.LIGHTER;
	}

	public void setSelectedStateType(NVersionSelectedStateType selectedStateType) {
		this.selectedStateType = selectedStateType;
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

	public String getUpdateHeader() {
		return updateHeader;
	}

	public String getUpdateMessage() {
		return updateMessage;
	}

	public String getUpdateMessageForced() {
		return updateMessageForced;
	}

	public String getUpdateGoToStoreBtn() {
		return updateGoToStoreBtn;
	}

	public String getUpdateNoBtn() {
		return updateNoBtn;
	}

	public String getNewInThisVersionHeader() {
		return newInThisVersionHeader;
	}

	public String getNewInThisVersionOkBtn() {
		return newInThisVersionOkBtn;
	}

	public int getContentViewResource() {
		return contentViewResource;
	}

	public NVersionSelectedStateType getSelectedStateType() {
		return selectedStateType;
	}

	public NVersionAlertOptions getNVersionAlertOptions() {
		return mNVersionAlertOptions;
	}

	public void setNVersionAlertOptions(NVersionAlertOptions mNVersionAlertOptions) {
		this.mNVersionAlertOptions = mNVersionAlertOptions;
	}
}