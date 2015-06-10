package dk.nodes.controllers.web;

import android.text.TextPaint;
import android.text.style.URLSpan;

public class NURLSpanNoUnderline extends URLSpan {
	public NURLSpanNoUnderline(String url) {
		super(url);
	}
	@Override
	public void updateDrawState(TextPaint ds) {
		super.updateDrawState(ds);
		ds.setUnderlineText(false);
	}
}
