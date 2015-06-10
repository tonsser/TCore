package dk.nodes.controllers;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.text.style.UnderlineSpan;

import dk.nodes.utils.NLog;

public class NSpannableHelper {

	private static String TAG = NSpannableHelper.class.getName();

	public static Spannable createIconWithText(Context mContext, String text, int iconResId){
		if(mContext == null){
			NLog.e(TAG  + " createIconWithText", "Context was null returning null");
			return null;
		}
		Spannable mSpannable = new SpannableString("   " + text);
		mSpannable.setSpan(new ImageSpan(mContext, iconResId,
				ImageSpan.ALIGN_BASELINE), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		return mSpannable;
	}

	public static Spannable createUnderline(String str){
		SpannableString contentUnderline = new SpannableString(str);
		contentUnderline.setSpan(new UnderlineSpan(), 0,
				contentUnderline.length(), 0);

		return contentUnderline;
	}
}
