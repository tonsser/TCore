package dk.nodes.controllers.camera;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import dk.nodes.controllers.NScreenParameters;
import dk.nodes.utils.NToast;

@Deprecated
public class NCameraView extends SurfaceView implements SurfaceHolder.Callback {
	private static final String TAG = NCameraView.class.getSimpleName();

	private SurfaceHolder mHolder;
	private NCameraController nCameraController;
	private Camera camera;
	private boolean firstLayout = true;
	private Context mContext;

	/** Used as a re-constructor after pausing the fragment/activity which will destroy the SurfaceView */
	public NCameraView(Context context, NCameraController nCameraController) {
		super(context);	
		this.mContext = context;
		this.nCameraController = nCameraController;
		makecamera(mContext);
	}

	/** Used as a general constructor */
	public NCameraView(Context context) {
		super(context);	
		this.mContext = context;
		makecamera(mContext);
	}

	public NCameraView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		makecamera(mContext);
	}

	public NCameraView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		makecamera(mContext);
	}

	/** Not used */
	public void resume(Context context) {
		//NLog.i(TAG, "resume()");
	}

	@SuppressWarnings("deprecation")
	public void makecamera(Context context) {
		//NLog.i(TAG, "makecamera()");
		if(nCameraController == null) {		
			nCameraController = new NCameraController(context, 0, getWidth(), getHeight());
		}
		camera = nCameraController.getCamera();

		// Install a SurfaceHolder.Callback so we get notified when the underlying surface is created and destroyed.
		getHolder().removeCallback(this);

		mHolder = getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); // deprecated setting, but required on Android versions prior to 3.0

	}

	public NCameraController getCameraController() {
		return nCameraController;
	}

	@Override
	protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld){
		//NLog.i(TAG, "onSizeChanged() " +  xOld + " x " + yOld + " changed to " +  xNew + " x " + yNew);
		super.onSizeChanged(xNew, yNew, xOld, yOld);
		
	}

	private void myOnLayout(boolean changed, int width, int height) {
		//NLog.i(TAG, "myOnLayout() - changed to " +  width + " x " + height);
		if(changed) {
			if(firstLayout) { // one-shot
				nCameraController.setVisibleSize(width, height);
				firstLayout = false;
			}
			
			NScreenParameters.setScreenParameters(mContext, false, false, false);

			int previewHeight, previewWidth;

			previewWidth = nCameraController.getWidth();
			previewHeight = nCameraController.getHeight();
			
			// if its a phone in "landscape" or a tablet in "portrait" switch
			if(NScreenParameters.screenRatio > 1.0f) {
				previewWidth = nCameraController.getHeight();
				previewHeight = nCameraController.getWidth();
			}			
			
			if(previewWidth == 0 || previewHeight == 0) { // no reason to layout if no preview is active
				return; 
			}
			// Center the SurfaceView within the parent, cropping the remainder
			int xmin = Math.round((float) (nCameraController.getVisibleWidth() - previewWidth) / 2f);
			int	xmax = Math.round((float) (nCameraController.getVisibleWidth() + previewWidth) / 2f);
			int	ymin = Math.round((float) (nCameraController.getVisibleHeight() - previewHeight) / 2f);
			int	ymax = Math.round((float) (nCameraController.getVisibleHeight() + previewHeight) / 2f);
			
			//NLog.d(TAG, "View size: " + (xmax-xmin) + " x " + (ymax-ymin));
			layout(xmin, ymin, xmax, ymax);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		//NLog.i(TAG, "surfaceChanged() " +  width + " x " + height);
		if(mHolder.getSurface() == null) { // preview surface does not exist
			return;
		}

		this.getHolder().addCallback(this);

		try {
			camera.stopPreview(); // stop preview before making changes
		} catch (Exception e) {
			// ignore: tried to stop a non-existent preview
		}

		try {
			nCameraController.changeSize(width, height);
			myOnLayout(true, width, height);
			camera.setPreviewDisplay(holder);
			camera.startPreview(); // start preview with new settings

		} catch (Exception e) {
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		//NLog.i(TAG, "surfaceCreated()");
		// The Surface has been created, now tell the camera where to draw the preview.
		try {
			firstLayout = true;
			camera.setPreviewDisplay(holder);
		} catch (Exception e) {
			camera = null;
			nCameraController = new NCameraController(mContext, 0, getWidth(), getHeight());
			camera = nCameraController.getCamera();

		} finally {
			if(camera != null)
				camera.startPreview();
			else
				NToast.executeShort(getContext(), "Can't connect to camera");
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		//NLog.i(TAG, "surfaceDestroyed()");
		try {
			this.getHolder().removeCallback(this);
			camera.stopPreview(); // stop preview before making changes
		} catch (Exception e) {
			// ignore: tried to stop a non-existent preview
		}

		nCameraController.releaseCamera();
		nCameraController = null;
	}

}