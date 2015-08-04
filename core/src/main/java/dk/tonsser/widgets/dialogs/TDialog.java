package dk.tonsser.widgets.dialogs;

import android.app.Dialog;
import android.content.Context;

import dk.tonsser.utils.TLog;

/**
 * This dialog is preventing most stupid crashes and will auto dismiss if view looses focus.
 *
 * @author Casper Rasmussen - 2012
 */
public abstract class TDialog extends Dialog {
    public boolean AUTO_DISMISS_ON_FOCUS_LOST = true;

    /**
     * Remember to check if context is null
     *
     * @param context
     * @param theme
     */
    public TDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (AUTO_DISMISS_ON_FOCUS_LOST && !hasFocus) {
            dismiss();
        }
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    public void cancel() {
        try {
            super.cancel();
        } catch (Exception e) {
            TLog.e("TDialog cancel", e);
        }
    }

    @Override
    public void dismiss() {
        try {
            super.dismiss();
        } catch (Exception e) {
            TLog.e("TDialog dismiss", e);
        }
    }

    @Override
    public void show() {
        try {
            super.show();
        } catch (Exception e) {
            TLog.e("TDialog show", e);
        }
    }

    @Override
    public void onDetachedFromWindow() {
        try {
            super.onDetachedFromWindow();
        } catch (Exception e) {
            TLog.e("TDialog onDetachedFromWindow", e);
        }
    }

}
