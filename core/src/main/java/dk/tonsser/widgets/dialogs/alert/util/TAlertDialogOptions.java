package dk.tonsser.widgets.dialogs.alert.util;

import android.graphics.Typeface;

import com.tonsser.core.R;


public class TAlertDialogOptions {

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
    private int contentViewResource = R.layout.dialog_alert_light;

    //Selected state type
    private TAlertDialogSelectedStateType selectedStateType = TAlertDialogSelectedStateType.DARKER;

    //Settings
    private boolean autoDismisWhenFocusLost = true;
    private boolean cancelable = true;
    private boolean cancelOnTouchOutside = true;
    private boolean dismissOnBtnClick = true;


    public boolean isAutoDismisWhenFocusLost() {
        return autoDismisWhenFocusLost;
    }


    public TAlertDialogOptions setAutoDismisWhenFocusLost(boolean autoDismisWhenFocusLost) {
        this.autoDismisWhenFocusLost = autoDismisWhenFocusLost;
        return this;
    }


    public boolean isCancelable() {
        return cancelable;
    }


    public TAlertDialogOptions setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
        return this;
    }


    public boolean isCancelOnTouchOutside() {
        return cancelOnTouchOutside;
    }


    public TAlertDialogOptions setCancelOnTouchOutside(boolean cancelOnTouchOutside) {
        this.cancelOnTouchOutside = cancelOnTouchOutside;
        return this;
    }


    public TAlertDialogOptions setStrings(String header, String message, String leftBtn, String rightBtn) {
        this.header = header;
        this.message = message;
        this.leftBtn = leftBtn;
        this.rightBtn = rightBtn;

        return this;
    }


    public TAlertDialogOptions setFonts(Typeface headerFont, Typeface messageFont, Typeface btnFont) {
        this.headerFont = headerFont;
        this.messageFont = messageFont;
        this.btnFont = btnFont;

        return this;
    }

    public TAlertDialogOptions setCustomTheme(int contentViewResource) {
        this.contentViewResource = contentViewResource;
        selectedStateType = TAlertDialogSelectedStateType.NONE;

        return this;
    }

    public TAlertDialogOptions setLightTheme() {
        contentViewResource = R.layout.dialog_alert_light;
        selectedStateType = TAlertDialogSelectedStateType.DARKER;

        return this;
    }

    public TAlertDialogOptions setDarkTheme() {
        contentViewResource = R.layout.dialog_alert_dark;
        selectedStateType = TAlertDialogSelectedStateType.LIGHTER;

        return this;
    }

    public TAlertDialogOptions setSelectedStateType(TAlertDialogSelectedStateType selectedStateType) {
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

    public TAlertDialogSelectedStateType getSelectedStateType() {
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


    public TAlertDialogOptions setDismissOnBtnClick(boolean dismissOnBtnClick) {
        this.dismissOnBtnClick = dismissOnBtnClick;

        return this;
    }
}