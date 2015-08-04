package dk.tonsser.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.provider.Telephony;
import android.text.Html;

import dk.tonsser.controllers.web.TagHandler;
import dk.tonsser.widgets.dialogs.alert.TBasicAlertDialog;

public class TAndroidIntents {

    private static String TAG = TAndroidIntents.class.getName();
    public static final int REQUEST_CODE_SMS = 123;

    /**
     * This method will open a Google Play intent, directing straight to the given app (packagename)
     *
     * @param mContext
     * @param String   packageName
     * @author Casper Rasmussen - 2012
     */
    public static void toMarket(Context mContext, String packageName) {
        try {
            mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        } catch (Exception e) {
            TLog.e("TUtils toMarket", e);
        }
    }

    /**
     * This method will open a Google Play intent, directing to the current app.
     *
     * @param mContext
     * @author Casper Rasmussen - 2102
     */
    public static void toMarket(Context mContext) {
        toMarket(mContext, TBuild.getPackageName(mContext));
    }

    /**
     * Use this method to open native browser and go to url
     *
     * @param mContext
     * @param url
     */
    public static void toBrowser(Context mContext, String url) {
        if (mContext == null || url == null) {
            TLog.d(TAG + " toBrowser", "Context or url was null");
            return;
        }
        try {
            if (!url.startsWith("http://") && !url.startsWith("https://"))
                url = "http://" + url;

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url)).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(browserIntent);
        } catch (Exception e) {
            TLog.e(TAG + " toBrowser url: " + url, e);
        }
    }

    public static void dialNumber(Activity mActivity, String phoneNumbe) {
        try {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + phoneNumbe));
            callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mActivity.startActivity(callIntent);
        } catch (Exception e) {
            TLog.e(TAG + " dialNumber", e);
        }
    }

    /**
     * Requrires dial permission
     *
     * @param mActivity
     * @param phoneNumbe
     */
    public static void callNumber(Activity mActivity, String phoneNumbe) {
        try {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + phoneNumbe));
            callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mActivity.startActivity(callIntent);
        } catch (Exception e) {
            TLog.e(TAG + " callNumber", e);
        }
    }

    /**
     * Requrires dial permission
     *
     * @param mActivity
     * @param phoneNumbe
     */
    public static void callNumberAfterConfirmation(final Activity mActivity, final String phoneNumbe,
                                                   String question, String no, String yes) {
        new TBasicAlertDialog(mActivity, null, question, no, yes, true, new TBasicAlertDialog.NBasicAlertDialogListener() {

            @Override
            public void onRightBtnClicked() {
                callNumber(mActivity, phoneNumbe);
            }

            @Override
            public void onLeftBtnClicked() {
                callNumber(mActivity, phoneNumbe);
            }
        }).show();
    }

    /**
     * This will open location settings
     *
     * @param mActivity
     */
    public static void toLocationSettings(final Context mContext) {
        try {
            mContext.startActivity(new Intent(
                    Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        } catch (Exception e) {
            TLog.e(TAG + " toLocationSettings", e);
        }
    }

    /**
     * This will open the wifi-settings
     *
     * @param mActivity
     */
    public static void toWIFISettings(final Context mContext) {
        try {
            mContext.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
        } catch (Exception e) {
            TLog.e(TAG + " toWIFISettings", e);
        }
    }

    /**
     * This method will open the choose with only mail apps, and pre-fill parameters
     *
     * @param mActivity
     * @param mailTo
     * @param subject
     * @param mail
     * @param chooseTitle
     */
    public static void sendMail(final Context mContext, String mailTo, String subject, String mail, String chooseTitle) {
        try {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO,
                    Uri.fromParts("mailto", mailTo, null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            emailIntent.putExtra(Intent.EXTRA_TEXT, mail);
            mContext.startActivity(Intent.createChooser(emailIntent, chooseTitle).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        } catch (Exception e) {
            TLog.e(TAG + " sendMail", e);
        }
    }

    /**
     * This method will open the choose with only mail apps, and pre-fill parameters
     * Will do HTML.fromHtmlt(mailAsHTML) as EXTRA_TEXT
     *
     * @param mActivity
     * @param mailTo
     * @param subject     as html
     * @param mail
     * @param chooseTitle
     */
    public static void sendMailWithHTML(final Context mContext, String mailTo, String subject, String mailAsHTML, String chooseTitle) {
        try {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO,
                    Uri.fromParts("mailto", mailTo, null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            emailIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(mailAsHTML, null, new TagHandler()));
            mContext.startActivity(Intent.createChooser(emailIntent, chooseTitle).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        } catch (Exception e) {
            TLog.e(TAG + " sendMail", e);
        }
    }

    /**
     * Use this to open Google Maps to show a route between two points
     *
     * @param mContext
     * @param fromLat
     * @param fromLng
     * @param toLat
     * @param toLng
     */
    public static void showRouteInGoogleMaps(final Context mContext, double fromLat, double fromLng, double toLat, double toLng) {
        try {
            mContext.startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?saddr=" + fromLat + "," + fromLng + "&daddr=" + toLat + "," + toLng + "&dirflg=w")));
        } catch (Exception e) {
            TLog.e(TAG + " onGetDirections", e);
        }
    }

    /**
     * Opens the installed sms app.
     *
     * @param activity
     */
    public static void sendSMS(final Activity activity) {
        sendSMS(activity, null, null, REQUEST_CODE_SMS);
    }

    /**
     * Opens the installed sms app.
     *
     * @param activity
     * @param message
     */
    public static void sendSMS(final Activity activity, String message) {
        sendSMS(activity, null, message, REQUEST_CODE_SMS);
    }

    /**
     * Opens the installed sms app.
     *
     * @param activity
     * @param phoneNumber
     * @param message
     */
    public static void sendSMS(final Activity activity, String phoneNumber, String message) {
        sendSMS(activity, phoneNumber, message, REQUEST_CODE_SMS);
    }

    /**
     * Opens the installed sms app.
     *
     * @param activity
     * @param message
     * @param requestCodeForResult For identification in onActivityResult().
     */
    public static void sendSMS(final Activity activity, String message, int requestCodeForResult) {
        sendSMS(activity, null, message, requestCodeForResult);
    }

    /**
     * Opens the installed sms app.
     *
     * @param activity
     * @param phoneNumber
     * @param message
     * @param requestCodeForResult For identification in onActivityResult().
     */
    @TargetApi(19)
    public static void sendSMS(final Activity activity, String phoneNumber, String message, int requestCodeForResult) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { /*At least KitKat*/
            String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(activity); /*Need to change the build to API 19*/

            Intent sendIntent;

            if (phoneNumber != null) {
                sendIntent = new Intent(Intent.ACTION_SENDTO);
                sendIntent.setType("text/plain");
                sendIntent.putExtra(Intent.EXTRA_TEXT, message);
                sendIntent.setData(Uri.parse("smsto:" + Uri.encode(phoneNumber)));//setData should be called last, otherwise it will crash.
            } else {
                sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.setType("text/plain");
                sendIntent.putExtra(Intent.EXTRA_TEXT, message);
            }

            if (defaultSmsPackageName != null) {/*Can be null in case that there is no default, then the user would be able to choose any app that support this intent.*/
                sendIntent.setPackage(defaultSmsPackageName);
            }
            activity.startActivityForResult(sendIntent, requestCodeForResult);
        } else { /*For early versions, do what worked for you before.*/
            Intent sendIntent = new Intent(Intent.ACTION_VIEW);

            if (phoneNumber != null)
                sendIntent.setData(Uri.parse("sms:" + Uri.encode(phoneNumber)));
            else
                sendIntent.setData(Uri.parse("sms:"));

            sendIntent.putExtra("sms_body", message);

            activity.startActivityForResult(sendIntent, requestCodeForResult);
        }
    }

}
