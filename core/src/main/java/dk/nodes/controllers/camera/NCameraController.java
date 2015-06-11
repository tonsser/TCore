package dk.nodes.controllers.camera;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.net.Uri;
import android.os.Environment;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import dk.nodes.controllers.NScreenParameters;
import dk.nodes.utils.NLog;

@Deprecated
public class NCameraController {
	private static final String logtag = NCameraController.class.getSimpleName();
	private static final String TAG = NCameraController.class.getSimpleName();

	private static String fileStorageDir = "NCamera";

	public Camera myCamera;
	private Context mContext;
	private int cameraId;
	public boolean isLandscape = false;

	private int visibleWidth;
	private int visibleHeight;

	public NCameraController(Context context, int cameraId, int width, int height) {
		//NLog.i(TAG, "NCameraController()");
		mContext = context;

		Camera c = null;

		try {
			c = Camera.open(cameraId); // attempt to get a Camera instance
		} catch (Exception e) {
			NLog.e(logtag, "Camera is not available " + e.toString());
		}
		this.cameraId = cameraId;

		Camera.Parameters myParameters = null;
		try {
			myParameters = c.getParameters();
		} catch (Exception e) {
			// Desire X crash on Jan 17 2013
		}
		if(myParameters == null) {
			NLog.e(logtag, "Camera error");
		}
		else {
			for(String focus : myParameters.getSupportedFocusModes()) {
				if(focus.equalsIgnoreCase(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
					myParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
				}
			}
			for(String whiteBalance : myParameters.getSupportedWhiteBalance()) {
				if(whiteBalance.equalsIgnoreCase(Camera.Parameters.WHITE_BALANCE_AUTO)) {
					myParameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
				}
			}

			c.setParameters(myParameters);
		}
		myCamera = c;
		changeSize(width, height);

	}

	public NCameraController(Context context, int width, int height) {
		this(context, 0, width, height);
	}

	public Camera getCamera() {
		return myCamera;
	}

	public int getWidth() {
		try {
			return myCamera.getParameters().getPreviewSize().width;
		} catch (Exception e) {
			// Desire X crash on Jan 17 2013
			NLog.e(logtag, "Camera.getParameters() failed");
			return 0;
		}
	}

	public int getHeight() {
		try {
			return myCamera.getParameters().getPreviewSize().height;
		} catch (Exception e) {
			// Desire X crash on Jan 17 2013
			NLog.e(logtag, "Camera.getParameters() failed");
			return 0;
		}
	}

	public void changeSize(int width, int height) {
		//NLog.i(TAG, "changeSize() - size: " + width + " x " + height);
		if(width==0 || height==0) {
			NLog.w(logtag, "changeSize() - size is zero");
			return;
		}
		if(myCamera != null) { // need to stop preview before resize in pre-ICS 
			if(Integer.valueOf(android.os.Build.VERSION.SDK_INT) < android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
				myCamera.stopPreview();
				myCamera.setDisplayOrientation(getCameraDisplayOrientation());
				myCamera.startPreview();
			}
			else {
				myCamera.setDisplayOrientation(getCameraDisplayOrientation());
			}
		}

		Camera.Parameters myParameters = null;
		try {
			myParameters = myCamera.getParameters();
		} catch (Exception e) {
			// Desire X crash on Jan 17 2013
			NLog.e(logtag, "Camera.getParameters() failed");
		}

		if(myParameters == null) {
			NLog.e(logtag, "No parameters found");		
		}		
		else {
			NScreenParameters.setScreenParameters(mContext, false, false, false);

			if(!isLandscape) { // correct for landscape
				if(NScreenParameters.screenRatio > 1.0f) {
					//NLog.i(logtag, "Portrait");	

					int temp = height;
					height = width;
					width = temp; 
				}
			}

			//NLog.i(TAG, "changeSize() - corrected for landscaping: " + width + " x " + height + " - " + ((float)width/(float)height));

			Camera.Size preSize = chooseSize(myParameters.getSupportedPreviewSizes(), width, height);
			Camera.Size picSize = chooseSize(myParameters.getSupportedPictureSizes(), preSize.width, preSize.height);
			if (preSize != null) {
				myParameters.setPreviewSize(preSize.width, preSize.height);
				//NLog.i(logtag, "Camera preview size: " + preSize.width + " x " +  preSize.height);
			}
			if (picSize != null) {
				myParameters.setPictureSize(picSize.width, picSize.height);
				//NLog.i(logtag, "Camera picture size: " + picSize.width + " x " +  picSize.height);
			}

			myCamera.setParameters(myParameters);
		}
	}

	private Camera.Size chooseSize(List<Size> sizes, int prefWidth, int prefHeight) {
		//NLog.d(TAG, "chooseSize() - preferred: " + prefWidth + " x " + prefHeight);

		Camera.Size chosen = sizes.get(0);
		for (Size s : sizes) {
			if(s.width==prefWidth && s.height==prefHeight) { // perfect match
				chosen = s;
				break;
			}
			float relativeWidth = ((float)s.width) / prefWidth;
			float relativeHeight = ((float)s.height) / prefHeight;
			//NLog.i(TAG, "chooseSize() - relative size: " + relativeWidth + " x " + relativeHeight);

			if( (relativeWidth>=1.0f) && (relativeHeight>=1.0f) ) { // this is big enough
				if( (s.width<chosen.width) || (s.height<chosen.height)) { // smaller than current
					chosen = s;
					//					NLog.i(TAG, "chosen " + s.width + " x " + s.height);
				}
				else {
					//					NLog.e(TAG, "rejected " + s.width + " x " + s.height);
				}
			}
			else if((s.width>chosen.width) || (s.height>chosen.height)) { // if not big enough, choose the biggest
				chosen = s;
				//				NLog.w(TAG, "chosen " + s.width + " x " + s.height);
			}
		}
		NLog.d(TAG, "Chosen Size: " + chosen.width + " x " + chosen.height);
		return chosen;
	}

	/** Kept for reference */
	@Deprecated
	private Camera.Size chooseSizeOld(List<Size> sizes, int prefWidth, int prefHeight) {
		NLog.d(TAG, "chooseSize() - preferred: " + prefWidth + " x " + prefHeight);
		final float ff = (float) prefWidth / prefHeight; // preview form factor
		float bff = 0; // holder for the current best form factor and size
		Camera.Size chosen = sizes.get(0);
		for (Size s : sizes) {
			if(s.width==prefWidth && s.height==prefHeight) { // perfect match
				chosen = s;
				break;
			}
			float cff = (float) s.width / s.height;
			float relativeWidth = ((float)s.width) / prefWidth;
			float relativeHeight = ((float)s.height) / prefHeight;
			NLog.d(TAG, "chooseSize() - relative size: " + relativeWidth + " x " + relativeHeight);
			if ((Math.abs(ff - cff) <= Math.abs(ff - bff)) && (relativeWidth >= 1.0f) && (relativeHeight >= 1.0f) && (chosen.width != prefWidth)) {
				if ((s.width < chosen.width)) {
					bff = cff;
					chosen = s;
					NLog.d(TAG, "chooseSize() " + chosen.width + " x " + chosen.height);
				}
			}
		}

		return chosen;
	}

	public void setVisibleSize(int width, int height) {
		//NLog.i(TAG, "setVisibleSize() " + width + " x " + height);
		this.visibleWidth = width;
		this.visibleHeight = height;
	}

	public void releaseCamera() {
		NLog.i(TAG, "releaseCamera()");
		if(myCamera != null)
			myCamera.release();
	}

	private int getCameraDisplayOrientation() {
		//NLog.i(TAG, "getCameraDisplayOrientation()");
		Camera.CameraInfo info = new Camera.CameraInfo();
		Camera.getCameraInfo(cameraId, info);

		WindowManager lWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);

		Configuration cfg = mContext.getResources().getConfiguration();
		int rotation = lWindowManager.getDefaultDisplay().getRotation();

		if( (((rotation== Surface.ROTATION_0) || (rotation== Surface.ROTATION_180)) && (cfg.orientation== Configuration.ORIENTATION_LANDSCAPE))
				|| (((rotation == Surface.ROTATION_90) ||(rotation == Surface.ROTATION_270)) && (cfg.orientation == Configuration.ORIENTATION_PORTRAIT))) {
			isLandscape = true;
		} 
		else {
			isLandscape = false;
		}


		int degrees = 0;
		switch (rotation) {
		case Surface.ROTATION_0: {
			degrees = 0; 
			break;
		}
		case Surface.ROTATION_90: {
			degrees = 90;  
			break;
		}
		case Surface.ROTATION_180: {
			degrees = 180;  
			break;
		}
		case Surface.ROTATION_270: {
			degrees = 270;  
			break;
		}
		}

		int result;
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360;  // compensate the mirror
		} else {  // back-facing
			result = (info.orientation - degrees + 360) % 360;
		}		

		//NLog.d(TAG, "getCameraDisplayOrientation() - landscape: " + isLandscape);

		return result;
	}


	public void takePhoto(final OnCameraListener mOnCameraListener){
		if(myCamera == null) {
			return;
		}
		if(!Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
			Toast.makeText(mContext, "Can't save photo. Media not mounted", Toast.LENGTH_LONG).show();
			return;
		}

		final File pictureFile = getOutputPhotoFile();
		if (pictureFile == null){
			NLog.e(logtag, "Error creating media file, check storage permissions");
			return;
		}

		myCamera.takePicture(null, null, new PictureCallback() {
			@Override
			public void onPictureTaken(byte[] data, Camera camera) {
				try {
					FileOutputStream fos = new FileOutputStream(pictureFile);
					fos.write(data);
					fos.close();
					scaleAndCrop(Uri.fromFile(pictureFile),mOnCameraListener);

					if(myCamera != null) {
						myCamera.startPreview();
					}
				} catch (FileNotFoundException e) {
					NLog.e(logtag, "FNFex: " + e.getMessage());
				} catch (IOException e) {
					NLog.e(logtag, "IOEx: " + e.getMessage());
				}
			}
		});

		Uri uri = Uri.fromFile(pictureFile);
		Intent scanFileIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
		mContext.sendBroadcast(scanFileIntent);
	}

	public static File getOutputPhotoFile(){
		File mediaStorageDir;
		if(fileStorageDir==null) { // fallback
			mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "NCamera");
		}
		else { 
			mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), fileStorageDir);
		}

		// Create the storage directory if it does not exist
		if (! mediaStorageDir.exists()){
			if (! mediaStorageDir.mkdirs()){
				NLog.e(logtag , "failed to create directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_"+ timeStamp+".jpg");
		return mediaFile;
	}

	private void scaleAndCrop(Uri imageUri, OnCameraListener mOnCameraListener) throws IOException {
		int preferredImgWidth = visibleWidth;
		int preferredImgHeigth = visibleHeight;
		// find the size of the original
		BitmapFactory.Options factoryOptions = new BitmapFactory.Options();
		factoryOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imageUri.getPath(), factoryOptions);
		int imageWidth = factoryOptions.outWidth;
		int imageHeight = factoryOptions.outHeight;
		int scaleFactor = Math.min(imageWidth / preferredImgWidth, imageHeight / preferredImgHeigth);

		// import it with scaled size
		factoryOptions.inJustDecodeBounds = false;
		factoryOptions.inSampleSize = scaleFactor; // may be forced to a power-of-two on some phones - faster, but higher heap requirements 
		factoryOptions.inPurgeable = true;
		Bitmap bitmapIn = BitmapFactory.decodeFile(imageUri.getPath(), factoryOptions);

		int h = bitmapIn.getHeight();
		int w = bitmapIn.getWidth();

		Matrix m = new Matrix();
		m.postRotate(getCameraDisplayOrientation());
		bitmapIn = Bitmap.createBitmap(bitmapIn, 0, 0, w, h, m, true);

		Bitmap bitmapOut;
		if(h==preferredImgHeigth && w==preferredImgWidth) {
			// perfect!
			bitmapOut = bitmapIn;
		}
		else {
			// scaling
			int hNew = bitmapIn.getHeight();
			int wNew = bitmapIn.getWidth();
			float aspectNew = (float) wNew / hNew;

			float preferredAspect = (float) preferredImgWidth / preferredImgHeigth;
			if(aspectNew > preferredAspect) { // relatively too wide 				
				wNew = (int)(preferredImgHeigth * aspectNew);
				hNew = preferredImgHeigth;
			}
			else { // relatively too tall
				wNew = preferredImgWidth;
				hNew = (int)(preferredImgWidth / aspectNew);
			}

			Bitmap bitmap = Bitmap.createScaledBitmap(bitmapIn, wNew, hNew, true);
			bitmapIn.recycle();
			bitmapIn = null;

			int offsetX = (wNew - preferredImgWidth) / 2;
			int offsetY = (hNew - preferredImgHeigth) / 2;

			if(offsetX==0 && offsetY==0) {
				bitmapOut = bitmap;
			}
			else { // if the bitmap is larger - cropping is required
				bitmapOut = Bitmap.createBitmap(bitmap, offsetX, offsetY, preferredImgWidth, preferredImgHeigth);
				bitmap.recycle();
				bitmap = null;
			}
		} 

		File file = getOutputPhotoFile();
		FileOutputStream out = new FileOutputStream(file);
		bitmapOut.compress(Bitmap.CompressFormat.JPEG, 50, out);
		out.close();

		Uri uri = Uri.fromFile(file);
		Intent scanFileIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
		mContext.sendBroadcast(scanFileIntent);
		if(mOnCameraListener != null)
			mOnCameraListener.onPhotoTaken(uri);
	}

	public int getVisibleWidth() {
		return visibleWidth;
	}

	public int getVisibleHeight() {
		return visibleHeight;
	}

	public interface OnCameraListener{
		public void onPhotoTaken(Uri uri);
	}

	/** Set the folder in which to save the photos. Defaults to "NCamera" */
	public static void setFileStorageDir(String fileStorageDir) {
		NCameraController.fileStorageDir = fileStorageDir;
	}
}
