package dk.nodes.widgets.framelayout;

import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class NTouchableFrameLayout extends FrameLayout {

	private long lastTouchedTimeStamp;
	private int deltaMsToTriggerEvent = 0;
	private int deltaPixelsToTriggerActionMoveFired = 10; 
	private int lastTouchedX = 0;
	private int lastTouchedY = 0;
	private NTouchableFrameLayoutOnTouchListener mNTouchableFrameLayoutOnTouchListener;
	private NTouchableFrameLayoutOnMoveListener mNTouchableFrameLayoutOnMoveListener;
	private boolean interceptTouchEvents;
	private boolean actionMoveFired = false;

	public NTouchableFrameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public NTouchableFrameLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public NTouchableFrameLayout(Context context) {
		super(context);
	}

	public int getTouchTimeMs() {
		return deltaMsToTriggerEvent;
	}

	public void setTouchTimeMs(int touchTimeMs) {
		this.deltaMsToTriggerEvent = touchTimeMs;
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				actionMoveFired = false;
				lastTouchedX = (int)ev.getX();
				lastTouchedY = (int)ev.getY();
				lastTouchedTimeStamp = SystemClock.uptimeMillis();
				if(mNTouchableFrameLayoutOnTouchListener != null && !interceptTouchEvents)
					mNTouchableFrameLayoutOnTouchListener.onTouchDown();
				break;
			case MotionEvent.ACTION_MOVE:
				if(mNTouchableFrameLayoutOnMoveListener != null)
					mNTouchableFrameLayoutOnMoveListener.onMove(actionMoveFired);
				if(Math.abs(ev.getX() - lastTouchedX)> Math.abs(deltaPixelsToTriggerActionMoveFired)
						|| Math.abs(ev.getY() - lastTouchedY)> Math.abs(deltaPixelsToTriggerActionMoveFired))
					actionMoveFired = true;
				break;
			case MotionEvent.ACTION_UP:
				final long now = SystemClock.uptimeMillis();
				if (now - lastTouchedTimeStamp > deltaMsToTriggerEvent) {
					if(mNTouchableFrameLayoutOnTouchListener != null && !interceptTouchEvents)
						mNTouchableFrameLayoutOnTouchListener.onTouchCancel(actionMoveFired);
				}
				break;
		}
		if(interceptTouchEvents)
			return true;
		else
			return super.dispatchTouchEvent(ev);
	}

	public void setIntersepctTouchEvents(boolean interceptTouchEvents){
		this.interceptTouchEvents = interceptTouchEvents;
	}

	public void setOnTouchListener(NTouchableFrameLayoutOnTouchListener mNTouchableFrameLayoutOnTouchListener){
		this.mNTouchableFrameLayoutOnTouchListener = mNTouchableFrameLayoutOnTouchListener;
	}
	
	public void setOnMoveListener(NTouchableFrameLayoutOnMoveListener mNTouchableFrameLayoutOnMoveListener){
		this.mNTouchableFrameLayoutOnMoveListener = mNTouchableFrameLayoutOnMoveListener;
	}

	public interface NTouchableFrameLayoutOnTouchListener {
		public void onTouchCancel(boolean actionMoveFired);
		public void onTouchDown();
	}
	
	/**
	 * @author Thomas
	 * Useful if you want to know if user has actually moved the map, or just clicked it.
	 */
	public interface NTouchableFrameLayoutOnMoveListener {
		public void onMove(boolean firstMoveCallback);
	}
	
	public int getDeltaPixelsToTriggerActionMoveFired() {
		return deltaPixelsToTriggerActionMoveFired;
	}

	public void setDeltaPixelsToTriggerActionMoveFired(int deltaPixelsToTriggerMove) {
		this.deltaPixelsToTriggerActionMoveFired = deltaPixelsToTriggerMove;
	}

}