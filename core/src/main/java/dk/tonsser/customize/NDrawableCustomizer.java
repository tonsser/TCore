package dk.tonsser.customize;
/**
 * @author Thomas
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

import dk.tonsser.utils.NLog;

public class NDrawableCustomizer {

	/**
	 * Use this method to color the icon of a drawable
	 * @param context
	 * @param colorID
	 * @param icon
	 */
	public static void setIconColorOnDrawable(Context context, int colorID, Drawable...icon) {
		for (Drawable drawable : icon){
			if(drawable!=null)
				drawable.setColorFilter(context.getResources().getColor(colorID), android.graphics.PorterDuff.Mode.SRC_ATOP);
			else
				NLog.w("NDrawableCustomizeController setIconColorOnDrawable", "A drawable was null");
		}
	}
	/**
	 * Use this method to color the background of a drawable
	 * @param context
	 * @param backgroundColorID
	 * @param icons
	 */
	public static void setIconBackgroundOnDrawable(Context context, int backgroundColorID, Drawable...icons) {
		for(Drawable drawable : icons){
			if(drawable!=null)
				drawable.setColorFilter(context.getResources().getColor(backgroundColorID), android.graphics.PorterDuff.Mode.DST_ATOP);
			else
				NLog.w("NDrawableCustomizeController setIconBackgroundOnDrawable","A drawable was null");
		}
	}
	
	/**
	 * Use this method to color icon fill of a drawable
	 * @param context
	 * @param backgroundColorID
	 * @param icons
	 */
	public static void setIconFillOnDrawable(Context context, int backgroundColorID, Drawable...icons) {
		for(Drawable drawable : icons){
			if(drawable!=null)
				drawable.setColorFilter(context.getResources().getColor(backgroundColorID), android.graphics.PorterDuff.Mode.SRC);
			else
				NLog.w("NDrawableCustomizeController setIconFillOnDrawable","A drawable was null");
		}
	}
	
	/**
	 * Makes a bitmap greyscale
	 * @param bmpOriginal
	 * @return
	 */
	public static Bitmap toGrayscale(Bitmap bmpOriginal)
	{        
	    int width, height;
	    height = bmpOriginal.getHeight();
	    width = bmpOriginal.getWidth();    

	    Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
	    Canvas c = new Canvas(bmpGrayscale);
	    Paint paint = new Paint();
	    ColorMatrix cm = new ColorMatrix();
	    cm.setSaturation(0);
	    ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
	    paint.setColorFilter(f);
	    c.drawBitmap(bmpOriginal, 0, 0, paint);
	    return bmpGrayscale;
	}
}
