package dk.nodes.controllers.feedback.webservice;

import android.graphics.Bitmap;

import dk.nodes.tasks.NAsyncClassic;
import dk.nodes.webservice.models.NApiAsyncListener;

public class NFeedbackAsync extends NAsyncClassic {

	private Bitmap bitmapInput;
	private String packageName;
	private String name;
	private String message;
	private String version;
	private String device;
	public NFeedbackAsync(Bitmap bitmapInput, String packageName, String name, String message, String version, String device, NApiAsyncListener mNApiAsyncListener) {
		super(mNApiAsyncListener);
		
		this.bitmapInput = bitmapInput;
		this.packageName = packageName;
		this.version = version;
		this.name = name;
		this.message = message;
		this.device = device;
	}
	
	@Override
	protected Integer doInBackground(String... params) {
		return NFeedBackWebservice.getInstance().postUploadFeedback(version, device, name, message, bitmapInput, packageName);
	}
}

