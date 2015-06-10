package dk.nodes.map.v2.controller;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.SparseArray;

import java.lang.ref.WeakReference;

public class NMarkerDrawerV2 {

	static SparseArray<WeakReference<Bitmap>> mMarkerCache = new SparseArray<WeakReference<Bitmap>>();
	
	private static final int HEIGHT = 50;
	private static final int WIDTH = 35;
	private static final float TEXT_SIZE = 20f;
	
	public static int GROUP_PIN_STROKE_COLOR = Color.BLACK;
	public static int GROUP_PIN_FILL_COLOR = Color.WHITE;
	
	Paint mPaint;
	
	public NMarkerDrawerV2() {
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setARGB(255, 0, 0, 0);
		mPaint.setTextSize( TEXT_SIZE );
	}
	
	public Bitmap drawGroupMarker( int number ) {
		if( mMarkerCache.get( number ) != null && mMarkerCache.get( number ).get() != null ) {
			return mMarkerCache.get( number ).get();
		}
		
		Bitmap.Config conf = Bitmap.Config.ARGB_8888;
		Bitmap bmp = Bitmap.createBitmap(WIDTH, HEIGHT, conf);
		Canvas canvas = new Canvas(bmp);
		
		int preColor = mPaint.getColor();
		
		// Stroke
		mPaint.setColor( GROUP_PIN_STROKE_COLOR );
		canvas.drawCircle( WIDTH/2, HEIGHT/3, WIDTH/2.2f, mPaint );
		
		RectF oval = new RectF();
		oval.set( 0, HEIGHT/2, WIDTH, HEIGHT+HEIGHT/2 );
		canvas.drawArc( oval, 240, 60, true, mPaint );
		
		// Fill
		mPaint.setColor( GROUP_PIN_FILL_COLOR );
		canvas.drawCircle( WIDTH/2, HEIGHT/3, (WIDTH/2.2f)-2, mPaint );
		
		oval = new RectF();
		oval.set( 0, (HEIGHT/2)-7, WIDTH, (HEIGHT+HEIGHT/2) );
		canvas.drawArc( oval, 240, 60, true, mPaint );
		
		// Text
		mPaint.setColor( preColor );
		canvas.drawText( ""+number, WIDTH/3.5f, HEIGHT/2.2f, mPaint );
		
		// Save it in the cache
		mMarkerCache.put( number, new WeakReference<Bitmap>( bmp ));
		
		return bmp;
	}
	
	public Bitmap drawGroupMarker( int number, Bitmap marker, GroupMarkerOptions options ) {
		if( mMarkerCache.get( number ) != null && mMarkerCache.get( number ).get() != null ) {
			return mMarkerCache.get( number ).get();
		}
		
		Bitmap.Config conf = Bitmap.Config.ARGB_8888;
		Bitmap bmp = Bitmap.createBitmap(options.width, options.height, conf);
		Canvas canvas = new Canvas(bmp);
		
		// Custom font with custom color
		if( options.typeface != null && options.color != 0 ) {
			// Save previous paint options
			Typeface preTypeface = mPaint.getTypeface();
			int preColor = mPaint.getColor();
			
			// Setup new stuff
			mPaint.setTypeface( options.typeface );
			mPaint.setColor( options.color );
			
			// Draw!
			canvas.drawText( ""+number, options.xOffset, options.yOffset, mPaint );
			
			// Restore
			mPaint.setTypeface( preTypeface );
			mPaint.setColor( preColor );
		
		// Custom font, standard color
		} else if( options.typeface != null && options.color == 0 ) {
			// Save previous paint options
			Typeface preTypeface = mPaint.getTypeface();

			// Setup new stuff
			mPaint.setTypeface( options.typeface );

			// Draw!
			canvas.drawText( ""+number, options.xOffset, options.yOffset, mPaint );

			// Restore
			mPaint.setTypeface( preTypeface );
			
		// Standard font, custom color
		} else if( options.typeface == null && options.color != 0 ) {
			
			// Save previous paint options
			int preColor = mPaint.getColor();

			// Setup new stuff
			mPaint.setColor( options.color );

			// Draw!
			canvas.drawText( ""+number, options.xOffset, options.yOffset, mPaint );

			// Restore
			mPaint.setColor( preColor );
		
		// Standard font
		} else {
			canvas.drawText( ""+number, options.xOffset, options.yOffset, mPaint );
		}
		
		// Save it in the cache
		mMarkerCache.put( number, new WeakReference<Bitmap>( bmp ));
		
		return bmp;
	}
	
	public class GroupMarkerOptions {
		public float xOffset = 10f;
		public float yOffset = 10f;
		public int width = NMarkerDrawerV2.WIDTH;
		public int height = NMarkerDrawerV2.HEIGHT;
		public Typeface typeface = null;
		public int color = 0;
	}
	
}
