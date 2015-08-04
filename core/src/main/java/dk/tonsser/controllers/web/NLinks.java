package dk.tonsser.controllers.web;

import android.text.Spannable;
import android.text.style.URLSpan;
import android.widget.TextView;

import dk.tonsser.utils.NLog;

public class NLinks {
	private static String TAG = NLinks.class.getName();

	public static void stripUnderlines(TextView textView) {
        try {
			Spannable s = (Spannable) textView.getText();
			URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);
			for (URLSpan span : spans) {
				int start = s.getSpanStart(span);
				int end = s.getSpanEnd(span);
				s.removeSpan(span);
				span = new NURLSpanNoUnderline(span.getURL());
				s.setSpan(span, start, end, 0);
			}
			textView.setText(s);
		} catch (Exception e) {
			NLog.e(TAG + " stripUnderlines", "did you add the string link this: tv.setText(Html.fromHtml(html), TextView.BufferType.SPANNABLE" + e);
		}
    }
}
