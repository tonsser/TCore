package dk.nodes.widgets;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ViewFlipper;

public class NViewFlipper extends ViewFlipper {
/**
 * Use this ViewFlipper to prevent stupid crash reports
 * @author Casper Rasmussen 2012
 * @param context
 */

	public NViewFlipper(Context context) {
		super(context);
	}
	public NViewFlipper(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	@Override
	protected void onDetachedFromWindow() {
		try {
			super.onDetachedFromWindow();
		}
		catch (IllegalArgumentException e) {
			stopFlipping();
		}
	}
}

