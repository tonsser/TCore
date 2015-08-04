package dk.tonsser.utils;

import android.annotation.SuppressLint;
import android.content.Context;

public class NClipboard {
    private static String TAG = NClipboard.class.getName();

    /**
     * This method will copy the inputed String to clipboard, will check OS version and use pre api 11 and post api 11
     *
     * @param mContext
     * @param toBeCopied
     * @author Casper Rasmussen
     */
    @SuppressLint("NewApi")
    public static void copyStringToClipboard(Context mContext, String toBeCopied) {
        try {
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
                android.text.ClipboardManager clipboard = (android.text.ClipboardManager) mContext
                        .getSystemService(Context.CLIPBOARD_SERVICE);
                clipboard.setText(toBeCopied);
            } else {
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) mContext
                        .getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData
                        .newPlainText("Copied Text", toBeCopied);
                clipboard.setPrimaryClip(clip);
            }
        } catch (Exception e) {
            NLog.e(TAG + " copyToClipboard", e);
        }
    }
}
