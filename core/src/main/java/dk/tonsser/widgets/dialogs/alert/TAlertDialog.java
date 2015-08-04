package dk.tonsser.widgets.dialogs.alert;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.tonsser.core.R;

import dk.tonsser.controllers.font.FontContainer;
import dk.tonsser.utils.TLog;
import dk.tonsser.widgets.dialogs.TDialog;
import dk.tonsser.widgets.dialogs.alert.util.TAlertDialogOptions;
import dk.tonsser.widgets.dialogs.alert.util.TAlertListener;

public class TAlertDialog extends TDialog {

    private String TAG = TAlertDialogOptions.class.getName();

    private TAlertListener mTAlertListener;
    private TAlertDialogOptions mTAlertDialogOptions;

    private TextView headerTv;
    private TextView messageTv;
    private Button leftBtn;
    private Button rightBtn;
    private FrameLayout topSeperatorFl;
    private FrameLayout betweenBtnsSeperatorFl;

    private String queueTag;

    public TAlertDialog(Context context, TAlertDialogOptions mTAlertDialogOptions, TAlertListener mTAlertListener) {
        super(context, R.style.Theme_AppCompat_Dialog);

        this.mTAlertDialogOptions = mTAlertDialogOptions;
        this.mTAlertListener = mTAlertListener;

        applyNAlertDialogOptions();
    }

    public void applyNAlertDialogOptions() {
        if (mTAlertDialogOptions == null) {
            TLog.e(TAG + " applyNAlertDialogOptions", "TAlertDialogOptions is null");
            return;
        }

        setContentView(mTAlertDialogOptions.getContentViewResource());
        initResources();
        applyDialogSettings();
    }

    private void applyDialogSettings() {
        setCancelable(mTAlertDialogOptions.isCancelable());
        setCanceledOnTouchOutside(mTAlertDialogOptions.isCancelOnTouchOutside());
        AUTO_DISMISS_ON_FOCUS_LOST = mTAlertDialogOptions.isAutoDismisWhenFocusLost();
    }

    private void initResources() {
        //Header
        headerTv = (TextView) findViewById(R.id.dialog_nalert_header_tv);
        if (headerTv != null) {
            if (mTAlertDialogOptions.getHeader() != null) {
                headerTv.setVisibility(View.VISIBLE);
                headerTv.setText(mTAlertDialogOptions.getHeader());
                FontContainer.setFont(mTAlertDialogOptions.getHeaderFont(), headerTv);
            } else
                headerTv.setVisibility(View.GONE);
        }

        //Message
        messageTv = (TextView) findViewById(R.id.dialog_nalert_message_tv);
        if (messageTv != null) {
            if (mTAlertDialogOptions.getMessage() != null) {
                messageTv.setVisibility(View.VISIBLE);
                messageTv.setText(mTAlertDialogOptions.getMessage());
                FontContainer.setFont(mTAlertDialogOptions.getMessageFont(), messageTv);
            } else
                messageTv.setVisibility(View.GONE);
        }

        //Left btn
        leftBtn = (Button) findViewById(R.id.dialog_nalert_left_btn);
        if (leftBtn != null) {
            if (mTAlertDialogOptions.getLeftBtn() != null) {
                leftBtn.setVisibility(View.VISIBLE);
                leftBtn.setText(mTAlertDialogOptions.getLeftBtn());
                FontContainer.setFont(mTAlertDialogOptions.getBtnFont(), leftBtn);
            } else
                leftBtn.setVisibility(View.GONE);

            leftBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (mTAlertDialogOptions.isDismissOnBtnClick())
                        dismiss();

                    if (mTAlertListener != null)
                        mTAlertListener.onLeftClicked();
                }
            });
        }

        //Right btn
        rightBtn = (Button) findViewById(R.id.dialog_nalert_right_btn);
        if (rightBtn != null) {
            if (mTAlertDialogOptions.getRightBtn() != null) {
                rightBtn.setVisibility(View.VISIBLE);
                rightBtn.setText(mTAlertDialogOptions.getRightBtn());
                FontContainer.setFont(mTAlertDialogOptions.getBtnFont(), rightBtn);
            } else
                rightBtn.setVisibility(View.GONE);

            rightBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (mTAlertDialogOptions.isDismissOnBtnClick())
                        dismiss();

                    if (mTAlertListener != null)
                        mTAlertListener.onRightClicked();
                }
            });
        }

        //Seperators
        topSeperatorFl = (FrameLayout) findViewById(R.id.dialog_nalert_top_btn_seperator_fl);
        betweenBtnsSeperatorFl = (FrameLayout) findViewById(R.id.dialog_nalert_btn_seperator_fl);

        if ((leftBtn == null || leftBtn.getVisibility() == View.GONE) && (rightBtn == null || rightBtn.getVisibility() == View.GONE)) {
            if (topSeperatorFl != null)
                topSeperatorFl.setVisibility(View.GONE);
            if (betweenBtnsSeperatorFl != null)
                betweenBtnsSeperatorFl.setVisibility(View.GONE);
        } else {
            if (topSeperatorFl != null)
                topSeperatorFl.setVisibility(View.VISIBLE);
            if (betweenBtnsSeperatorFl != null)
                betweenBtnsSeperatorFl.setVisibility(View.VISIBLE);
        }
    }

    public TAlertDialog setOptions(TAlertDialogOptions mTAlertDialogOptions) {
        this.mTAlertDialogOptions = mTAlertDialogOptions;
        return this;
    }

    public TAlertDialog setQueueTagAsMessage() {
        queueTag = mTAlertDialogOptions.getMessage();

        return this;
    }

    @Override
    public String getQueueTag() {
        if (queueTag != null)
            return queueTag;
        else if (mTAlertDialogOptions == null)
            return null;
        else
            return mTAlertDialogOptions.getHeader() + mTAlertDialogOptions.getMessage();
    }


}