package dk.nodes.controllers.feedback.webservice;

import android.graphics.Bitmap;

import org.apache.http.entity.mime.content.ByteArrayBody;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import dk.nodes.utils.NLog;
import dk.nodes.webservice.NQueryBuilder;
import dk.nodes.webservice.NWebserviceConstants;
import dk.nodes.webservice.NWebserviceController;
import dk.nodes.webservice.models.NMultiPartEntityPair;
import dk.nodes.webservice.models.NResponse;
import dk.nodes.webservice.parser.NJSONObject;

public class NFeedBackWebservice extends NWebserviceController{
	private static NFeedBackWebservice instance;
	private String TAG = NFeedBackWebservice.class.getName();

	public NFeedBackWebservice(){
		super();
		AUTO_LOG_RESPONSES = true;
		AUTO_LOG_REQUESTS = true;
	}

	public int postUploadFeedback(String version, String device, String name, String message, Bitmap bitmapInput, String packageName){
		NResponse response = null;
		try {  	
			NJSONObject queryJSON = new NJSONObject();
			queryJSON.put("bundle", packageName);
			
			String feedbackUrl = "http://mobile.like.st/api/feedback" + NQueryBuilder.jsonToQuery(queryJSON);
			
			NJSONObject postJSON = new NJSONObject();
			postJSON.put("device", "android " +device);
			postJSON.put("version", version);
			postJSON.put("name", name);
			postJSON.put("message", message);
			ArrayList<NMultiPartEntityPair> mNMultiPartEntityPairList = new ArrayList<NMultiPartEntityPair>();
			
			//With image
			if(bitmapInput != null){
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				bitmapInput.compress(Bitmap.CompressFormat.JPEG, 75, stream);
				byte[] byteArray = stream.toByteArray();

				ByteArrayBody bab = new ByteArrayBody(byteArray, System.currentTimeMillis()+".jpg");

				mNMultiPartEntityPairList.add(new NMultiPartEntityPair("data[encoded_image]", bab));
				response = curlHttpPostMultiPart(feedbackUrl, postJSON, mNMultiPartEntityPairList);
			}
			else{ // No image
				response = curlHttpPost(feedbackUrl, postJSON);
			}
		}
		catch(Exception e){
			NLog.e(e);	
			return NWebserviceConstants.API_CONNECTION_ERROR;
		}
		try{
			return response.getResponseCode();
		} 
		catch (Exception e) {
			NLog.e(e);	
			return NWebserviceConstants.API_RANDOM_ERROR;
		}	
	}

	//Not used at the moment
	public boolean getIsFeedbackActive(String bundle){
		NResponse response;
		try{
			NJSONObject bundleJson = new NJSONObject();
			bundleJson.put("bundle", bundle);
			String url = "http://mobile.like.st/api/project/feedback" + NQueryBuilder.jsonToQuery(bundleJson);
			response = curlHttpGet(url);
		}
		catch(Exception e){
			NLog.e(TAG + " getIsFeedbackActive connection", e);
			return false;
		}
		
		try{
			return response.getResponseJson().getNJSONObject("data").getBoolean("feedback");
		}
		catch(Exception e){
			NLog.e(TAG + " getIsFeedbackActive parse", e);
			return false;
		}
	}

	public static NFeedBackWebservice getInstance() {
		if(instance == null)
			instance = new NFeedBackWebservice();

		return instance;
	}
}
