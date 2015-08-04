package dk.tonsser.controllers.rateme;
/**
 * @author Martin 2012, Modified by CR
 */

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import dk.tonsser.utils.NAndroidIntents;

public class NRateMeDialog extends Dialog {
    String infoText, appName, AskMeLaterText, noText, packageName;
    SharedPreferences.Editor editor;
    Context mContext;
    Dialog dialog;

    /**
     * @param context     Your application context.
     * @param message     The message to be shown within the dialog.
     * @param later       The message on the "remind me later" button.
     * @param noThanks    The message to be shown on the "dont remind me again" button.
     * @param editor      The SharedPreferences editor for saving choices according to user input.
     * @param packageName The apps package name for Google Play reference.
     * @param appName     The app name.
     */
    public NRateMeDialog(final Context mContext, String infoText, String yesText, String AskMeLaterText, String noText, final SharedPreferences.Editor editor, String packageName, String appName) {
        super(mContext);
        this.infoText = infoText;
        this.appName = appName;
        this.AskMeLaterText = AskMeLaterText;
        this.noText = noText;
        this.packageName = packageName;
        this.editor = editor;
        this.mContext = mContext;
        dialog = this;

        dialog.setTitle("Rate " + appName);

        LinearLayout ll = new LinearLayout(mContext);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setGravity(Gravity.CENTER_HORIZONTAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 10, 10, 10);

        TextView messageTv = new TextView(mContext);
        messageTv.setText(infoText);
        messageTv.setWidth(240);
        messageTv.setPadding(4, 0, 4, 10);
        ll.addView(messageTv);

        Button rateItButton = new Button(mContext);
        rateItButton.setText(yesText);
        rateItButton.setId(0);
        rateItButton.setMinimumWidth(300);
        rateItButton.setLayoutParams(params);
        rateItButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NAndroidIntents.toMarket(mContext);
                dialog.dismiss();
            }
        });
        ll.addView(rateItButton);

        Button noButton = new Button(mContext);
        noButton.setText(AskMeLaterText);
        noButton.setId(1);
        noButton.setMinimumWidth(300);
        noButton.setLayoutParams(params);
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        ll.addView(noButton);

        Button askLaterButton = new Button(mContext);
        askLaterButton.setText(noText);
        askLaterButton.setId(2);
        askLaterButton.setMinimumWidth(300);
        askLaterButton.setLayoutParams(params);
        askLaterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editor != null) {
                    editor.putBoolean("rateMeDontShowAgain", true);
                    editor.commit();
                }
                dialog.dismiss();
            }
        });
        ll.addView(askLaterButton);
        dialog.setContentView(ll);
    }
}
