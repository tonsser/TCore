package dk.nodes.customize;

import android.widget.ProgressBar;

public class NProgressBarCustomizeController {

    /**
     * Use this method to Color your progressBar
     *
     * @param mProgressBar
     * @param mColor
     * @author Casper Rasmussen
     */
    public static void setColorOfProgressBar(ProgressBar mProgressBar, int mColor) {
        try {
            mProgressBar.getIndeterminateDrawable()
                    .setColorFilter(mColor, android.graphics.PorterDuff.Mode.MULTIPLY);
        } catch (NullPointerException e) {
            /*On SDK 22 (Lollipop 5.1), a weird crash can happen.
				Caused by: java.lang.NullPointerException: Attempt to write to field 'java.util.ArrayList android.animation.AnimatorSet$Node.nodeDependents' on a null object reference
		        at android.animation.AnimatorSet.clone(AnimatorSet.java:699)
			*/
            e.printStackTrace();
        }
    }

    public static void setColorOfHorizontalProgressBar(ProgressBar mProgressBar, int mColor) {
        try {
            mProgressBar.getProgressDrawable()
                    .setColorFilter(mColor, android.graphics.PorterDuff.Mode.SRC_IN);
        } catch (NullPointerException e) {
            /*On SDK 22 (Lollipop 5.1), a weird crash can happen.
				Caused by: java.lang.NullPointerException: Attempt to write to field 'java.util.ArrayList android.animation.AnimatorSet$Node.nodeDependents' on a null object reference
		        at android.animation.AnimatorSet.clone(AnimatorSet.java:699)
			*/
            e.printStackTrace();
        }
    }
}
