package dk.nodes.widgets.buttons;
/**
 * @author Thomas Nielsen 2013
 */

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

import dk.nodes.utils.math.NMath;

public class NImageButton extends ImageButton {

	private ImageButton mImageButton;
	private boolean isClicked;

	public NImageButton(Context context, AttributeSet attrs,int defStyle) {
		super(context, attrs, defStyle);
		mImageButton = this;
	}

	public NImageButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		mImageButton = this;
	}

	public NImageButton(Context context) {
		super(context);
		mImageButton = this;
	}

	public void setEnabledAlpha(final float alpha){
		final int alphaCalulacted = (int) (255*alpha);
		Drawable d = mImageButton.getDrawable();
		if(d != null){
			d.setAlpha(alphaCalulacted);  
			mImageButton.setImageDrawable(d);
		}
	}


	/**
	 * This method will setOnTouchListener and change background color, depending on the 2 input colors
	 * If you need OnTouchListener for anything else then just execute setSelectedStateColor(MotionEvent, int, int) in onTouch, instead of setting this
	 * @param colorNormalState
	 * @param colorSelectedState
	 */
	public void setSelectedStateColor(final int colorNormalState, final int colorSelectedState){
		this.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				setSelectedStateColor(event,colorNormalState,colorSelectedState);
				return false;
			}
		});		
	}

	/**
	 * This method will apply and remove selected states depending on MotionEvents, will change color of the background drawable
	 * @param event
	 * @param colorNormalState
	 * @param colorSelectedState
	 */
	public void setSelectedStateColor(MotionEvent event, final int colorNormalState, final int colorSelectedState){
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			mImageButton.setBackgroundColor(colorSelectedState);
			isClicked=true;
		}

		else if(isClicked&&(event.getAction() == MotionEvent.ACTION_MOVE) && NMath.isViewContains(mImageButton,event.getX(), event.getY())){
			mImageButton.setBackgroundColor(colorSelectedState);
		}
		else{
			isClicked = false;
			mImageButton.setBackgroundColor(colorNormalState);
		}
	}

	/**
	 * This method will setOnTouchListener and change aplha value of button background drawable, the alpha value is decided in input 0f-1f
	 * If you need OnTouchListener for anything else then just execute setSelectedStateAplhe(MotionEvent event,final int alpha) in onTouch, instead of setting this
	 * @param alpha
	 */
	public void setSelectedStateAlpha(final float alpha){
		this.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				setSelectedStateAplhe(event,alpha);
				return false;
			}
		});		
	}

	/**
	 * This method will apply and remove selected states depending on MotionEvents, will change alpha-value of the background drawable
	 * @param event
	 * @param AlphaOfBlack
	 */
	public void setSelectedStateAplhe(MotionEvent event, float alpha){
		final int alphaCalulacted = (int) (255*alpha);
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			Drawable d = mImageButton.getDrawable();
			if(d != null){
				d.setAlpha(alphaCalulacted);  
				mImageButton.setImageDrawable(d);
			}
			isClicked=true;
		}

		else if(isClicked&&(event.getAction() == MotionEvent.ACTION_MOVE) && NMath.isViewContains(mImageButton,event.getX(), event.getY())){
			Drawable d = mImageButton.getDrawable();
			if(d != null){
				d.setAlpha(alphaCalulacted);
				mImageButton.setImageDrawable(d);
			}
		}
		else{
			isClicked = false;
			Drawable d = mImageButton.getDrawable();
			if(d != null){
				d.setAlpha(255); 
				mImageButton.setImageDrawable(d);
			}
		}	
	}

	/**
	 * This method will setOnTouchListener and apply a dark colorFilter on top of the button, the alpha value is decided in input
	 * If you need OnTouchListener for anything else then just execute setSelectedStateDarkTop(MotionEvent event,final int AlphaOfBlack) in onTouch, instead of setting this
	 * @param AlphaOfBlack (0-1f)
	 */
	public void setSelectedStateDarkTop(final float AlphaOfBlack){
		setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				setSelectedStateDarkTop(event,AlphaOfBlack);
				return false;
			}
		});		
	}

	/**
	 * This method will apply and remove selected states depending on MotionEvents, will apply a apply a dark colorFilter on top of the background drawable
	 * @param event
	 * @param AlphaOfBlack
	 */
	public void setSelectedStateDarkTop(MotionEvent event,final float AlphaOfBlack){
		final int alphaCalulacted = (int) (255*AlphaOfBlack);
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			Drawable d = mImageButton.getDrawable();
			d.setColorFilter(Color.argb(alphaCalulacted, 0, 0, 0), android.graphics.PorterDuff.Mode.SRC_ATOP);
			mImageButton.setImageDrawable(d);  
			isClicked=true;
		}

		else if(isClicked&&(event.getAction() == MotionEvent.ACTION_MOVE) && NMath.isViewContains(mImageButton,event.getX(), event.getY())){
			Drawable d = mImageButton.getDrawable();
			d.setColorFilter(Color.argb(alphaCalulacted, 0, 0, 0), android.graphics.PorterDuff.Mode.SRC_ATOP);
			mImageButton.setImageDrawable(d);  
		}
		else{
			isClicked = false;
			Drawable d = mImageButton.getDrawable();
			d.setColorFilter(null);
			mImageButton.setImageDrawable(d);  
		}
	}


	/**
	 * This method will setOnTouchListener and use the 2 input drawables as states for normal and selected
	 * if you need the touchListener, then just execute setSelectedStateDrawables(MotionEvent, Drawable, Drawable) onTouch()
	 * @param normalState
	 * @param selectedState
	 */
	public void setSelectedStateDrawables(final Drawable normalState, final Drawable selectedState){
		this.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				setSelectedStateDrawables(event,normalState,selectedState);
				return false;
			}
		});		
	}
	/**
	 * This method will apply and remove selected states depending on MotionEvents, and the given drawables
	 * @param event
	 * @param normalState
	 * @param selectedState
	 */
	public void setSelectedStateDrawables(MotionEvent event, final Drawable normalState, final Drawable selectedState){
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			mImageButton.setImageDrawable(selectedState);  
			isClicked=true;
		}

		else if(isClicked&&(event.getAction() == MotionEvent.ACTION_MOVE) && NMath.isViewContains(mImageButton,event.getX(), event.getY())){
			mImageButton.setImageDrawable(selectedState);  
		}
		else{
			isClicked = false;
			mImageButton.setImageDrawable(normalState);  
		}
	}

	/**
	 * Will set setOnTouchListener(null) and thereby remove all selected states
	 */
	public void clearSelectedState(){
		setOnTouchListener(null);
	}

}
