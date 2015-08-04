package dk.tonsser.customize;

import android.content.Context;
import android.widget.ImageView;

public class NImageViewCustomizeController {

    /**
     * Use this method to change color of a given imageView, with a picture in. This works best with full black or white picture.
     *
     * @param context
     * @param mColor     - Color
     * @param imageviews
     */
    public static void setIconColorOnImageView(Context context, int mColor, ImageView... imageviews) {
        for (ImageView iv : imageviews) {
            iv.getDrawable().setColorFilter(mColor, android.graphics.PorterDuff.Mode.SRC_ATOP);
        }
    }
}
