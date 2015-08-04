package dk.tonsser.utils.math;

import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;
import android.widget.ListView;

import dk.tonsser.models.RectD;

/**
 * @author Casper Rasmussen 2012
 */

public class NMath {	

			
	/**
	 * Check if the coordinate set is within the view
	 * @param v
	 * @param x
	 * @param y
	 * @return boolean
	 */
	public static boolean isViewContains(View mView,float x, float y) {
		return x > 0 && y > 0 && x < mView.getWidth() && y < mView.getHeight();
	}
	/**
	 * @author Christian
	 * Use this method to generate a rect where top left corner is actually top left
	 * @param in
	 * @return Rect
	 */
	public static Rect fixRectangle(Rect in) {
		int l, t, r, b;
		if(in.left <= in.right) {
			l = in.left; r = in.right;
		}
		else {
			l = in.right; r = in.left;
		}
		if(in.top <= in.bottom) {
			t = in.top; b = in.bottom;
		}
		else {
			t = in.bottom; b = in.top;
		}
		return new Rect(l, t, r, b);
	}
	
	/**
	 * @author CR
	 * Use this method to generate a RectF where top left corner is actually top left
	 * @param in
	 * @return RectF
	 */
	public static RectF fixRectangleF(RectF in) {
		float l, t, r, b;
		if(in.left <= in.right) {
			l = in.left; r = in.right;
		}
		else {
			l = in.right; r = in.left;
		}
		if(in.top <= in.bottom) {
			t = in.top; b = in.bottom;
		}
		else {
			t = in.bottom; b = in.top;
		}
		return new RectF(l, t, r, b);
	}
	
	/**
	 * @author CR
	 * Use this method to generate a RectF where top left corner is actually top left
	 * @param in
	 * @return RectD
	 */
	public static RectD fixRectangleD(RectD in) {
		return new RectD(Math.min(in.left, in.right), Math.min(in.top, in.bottom), Math.max(in.left, in.right), Math
				.max(in.top, in.bottom));
	}
	
	/**
	 * nearest 100, value 101 = 200; 
	 * nearest 100, value 199 = 200; 
	 * nearest 10, value 101 = 110; 
	 * nearest 10, value 199 = 200; 
	 * If you need a decimal shortner use Decimal.format
	 * double d = 0.005;
	 * DecimalFormat df = new DecimalFormat("0.00");
	 * df.setRoundingMode(RoundingMode.HALF_UP);
	 * df.format(d); //0.01
	 * @param nearest
	 * @param value
	 * @return int
	 */
	public static int roundUpToNearest(int nearest,float value){
		return (int) ((Math.ceil(value / nearest))*nearest);
	}
	
	public static int getScrollYOfListView(ListView listView){
		View firstView = listView.getChildAt(0);
		if(firstView!=null){
			return -firstView.getTop() + listView.getFirstVisiblePosition() * firstView.getHeight();	
		}
		else
			return 0;
	}
}
