package dk.tonsser.features.canvasrotater.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import dk.tonsser.features.canvasrotater.lib.MoveGestureDetector;
import dk.tonsser.features.canvasrotater.lib.RotateGestureDetector;

@Deprecated
public class ImageMover extends View {
	
	public static float				SKEW_FACTOR			= 0.05f;
	public static float				PERSPECTIVE_FACTOR	= 0.05f;

	private Drawable mImage;
	private Camera mCamera;

	private ScaleGestureDetector mScaleDetector;
	private RotateGestureDetector	mRotateDetector;
	private MoveGestureDetector mMoveDetector;
	private float					mScaleFactor		= 1.f;
	private float					mCameraRotateX		= 0f;
	private float					mCameraRotateY		= 0f;

	public float					mRotationDegrees	= 1.f;
	public float					mFocusX				= 1.f;
	public float					mFocusY				= 1.f;
	
	public float					mSkewX				= 0f;
	public float					mSkewY				= 0f;

	public int						mViewHeight			= 0;
	public int						mViewWidth			= 0;

	/*
	 * https://github.com/Almeros/android-gesture-detectors
	 */
	
	public ImageMover( Context context ) {
		this( context, null, 0 );
	}

	public ImageMover( Context context, AttributeSet attrs ) {
		this( context, attrs, 0 );
	}

	public ImageMover( Context context, AttributeSet attrs, int defStyle ) {
		super( context, attrs, defStyle );
		mImage = getResources().getDrawable( 0 ); //set drawable here
		mImage.setBounds( 0, 0, mImage.getIntrinsicWidth(), mImage.getIntrinsicHeight() );

		mScaleDetector 	= new ScaleGestureDetector( context, new ScaleListener() );
		mRotateDetector = new RotateGestureDetector( context, new RotateListener() );
		mMoveDetector 	= new MoveGestureDetector( context, new MoveListener() );
		
		mCamera = new Camera();

	}

	public void setDrawable( Drawable imageDrawable ) {
		mImage = imageDrawable;
	}
	
	// Not tested
	// From -> http://stackoverflow.com/questions/2801116/converting-a-view-to-bitmap-without-displaying-it-in-android
	public Bitmap loadBitmapFromView() {
	    Bitmap b = Bitmap.createBitmap(mViewWidth, mViewHeight, Bitmap.Config.ARGB_8888);
	    Canvas c = new Canvas(b);
	    this.layout(0, 0, mViewWidth, mViewHeight);
	    this.draw(c);
	    return b;
	}

	public void increaseRotationX() {
		mCameraRotateX += 5f;
		
		invalidate();
	}
	
	public void decreaseRotationX() {
		mCameraRotateX -= 5f;
		
		invalidate();
	}
	
	public void increaseRotationY() {
		mCameraRotateY += 5f;
		
		invalidate();
	}
	
	public void decreaseRotationY() {
		mCameraRotateY -= 5f;
		
		invalidate();
	}
	
	public void setDisplay( int displayHeight, int displayWidth ) {
		mViewHeight = displayHeight;
		mViewWidth = displayWidth;
	}

	@Override
	protected void onSizeChanged( int xNew, int yNew, int xOld, int yOld ) {
		super.onSizeChanged( xNew, yNew, xOld, yOld );
		mViewHeight = yNew;
		mViewWidth = xNew;
	}

	@Override
	public boolean onTouchEvent( MotionEvent ev ) {
		/*
		// Buggy 
		if( mImage.copyBounds().contains( (int)ev.getX(), (int)ev.getY() ) ) {
			mScaleDetector.onTouchEvent( ev );
			mRotateDetector.onTouchEvent( ev );
			mMoveDetector.onTouchEvent( ev );

			invalidate();

			return true;
		}
		*/
		
		mScaleDetector.onTouchEvent( ev );
		mRotateDetector.onTouchEvent( ev );
		mMoveDetector.onTouchEvent( ev );

		invalidate();

		return true;
	}

	@SuppressLint("NewApi")
	@Override
	public void onDraw( Canvas canvas ) {
		super.onDraw( canvas );

		canvas.save();
		canvas.translate( mFocusX, mFocusY );
		canvas.scale( mScaleFactor, mScaleFactor );
		canvas.rotate( mRotationDegrees, mFocusX, mFocusY );
		//canvas.skew( mSkewX, mSkewY );
		
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB){
			Matrix canvasMatrix = canvas.getMatrix();
			mCamera.save();
			mCamera.rotateX( mCameraRotateX );
			mCamera.rotateY( mCameraRotateY );
			mCamera.getMatrix( canvasMatrix );
			canvasMatrix.preTranslate( -mViewWidth/2, -mViewHeight/2 );
			canvasMatrix.postTranslate( mViewWidth/2, mViewHeight/2 );
			canvas.concat( canvasMatrix );
			mCamera.restore();
		} 
		
		mImage.draw( canvas );
		canvas.restore();
	}

	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

		@Override
		public boolean onScale( ScaleGestureDetector detector ) {
			mScaleFactor *= detector.getScaleFactor(); // scale change since

			// Don't let the object get too small or too large.
			mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f));

			return true;
		}
	}

	private class RotateListener extends RotateGestureDetector.SimpleOnRotateGestureListener {

		@Override
		public boolean onRotate( RotateGestureDetector detector ) {
			mRotationDegrees -= detector.getRotationDegreesDelta();
			return true;
		}
	}

	private class MoveListener extends MoveGestureDetector.SimpleOnMoveGestureListener {

		private int minX = mImage.getIntrinsicWidth() / 2;
		private int minY = mImage.getIntrinsicHeight() / 2;
		
		@Override
		public boolean onMove( MoveGestureDetector detector ) {
			PointF d = detector.getFocusDelta();
			/*
			if( detector.getFocusX() + minX >= 0 && detector.getFocusX() - minX <= mViewWidth)
				mFocusX += d.x;
			
			if( detector.getFocusY() + minY >= 0 && detector.getFocusY() - minY <= mViewHeight )
				mFocusY += d.y;
			*/

			mFocusX += d.x;
			mFocusY += d.y;
			
			// mFocusX = detector.getFocusX();
			// mFocusY = detector.getFocusY();
			return true;
		}

	}
}
