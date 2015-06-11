package dk.nodes.webservice;
/**
 * @author Casper Rasmussen 2012
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import dk.nodes.controllers.NRetryController;
import dk.nodes.controllers.NStringController;
import dk.nodes.filehandler.NFileHandler;
import dk.nodes.utils.NLog;
import dk.nodes.utils.NUtils;
import dk.nodes.webservice.cookies.NWebserviceCookieContainer;
import dk.nodes.webservice.cookies.NWebserviceCookieController;
import dk.nodes.webservice.models.NMultiPartEntityBase64Pair;
import dk.nodes.webservice.models.NMultiPartEntityPair;
import dk.nodes.webservice.models.NProgressMultipartEntity;
import dk.nodes.webservice.models.NProgressMultipartEntity.NProgressListener;
import dk.nodes.webservice.models.NResponse;
import dk.nodes.webservice.parser.NJSONArray;
import dk.nodes.webservice.parser.NJSONObject;

// use Retrofit!
@Deprecated
public class NWebserviceController {
	public boolean ACCEPT_JSON = false;
	public boolean ACCEPT_GZIP = false;
	public boolean AUTO_LOG_HEADERS = false;
	public boolean AUTO_LOG_ALL_CURLS = true;
	public boolean AUTO_LOG_AS_CURL = false;
	public boolean AUTO_LOG_RESPONSES = true;
	public boolean AUTO_LOG_REQUESTS = true;
	public boolean AUTO_LOG_RESPONSE_TIME = false;
	public boolean useRetry = true;
	public boolean useRetryOn500Errors = false;


	public static void initilizeCookieFilehandler(Context mContext){
		cookieFilehandler = new NFileHandler(mContext, "cookies.dat");
	}

	private DefaultHttpClient httpClientForeground;
	private DefaultHttpClient httpClientBackground;
	private BasicHttpParams httpParameters;
	private static NWebserviceCookieContainer cookieData = new NWebserviceCookieContainer();
	private ArrayList<BasicHeader> myHeaderArrayList = new ArrayList<BasicHeader>();
	private String login;
	private String password;
	private static NFileHandler cookieFilehandler;

	public static void loadCookieContainerFromFile(){
		if(cookieFilehandler==null)
			NLog.e("loadCookieContainerFromFile","cookieFilehandler is null, did you run initilizeCookieFilehandler?");
		else{
			NWebserviceCookieContainer newCookieData = (NWebserviceCookieContainer) cookieFilehandler.loadData();
			if(newCookieData!=null)
				cookieData = newCookieData;
			else
                NLog.e("loadCookieContainerFromFile","loaded data was null, did'nt override original");
		}
	}
	public static void saveCookieContainerToFile(){
		if(cookieFilehandler==null)
            NLog.e("saveCookieContainerToFile","cookieFilehandler is null, did you run initilizeCookieFilehandler?");
		else
			cookieFilehandler.saveData(cookieData);		
	}

	private NRetryController mNRetryController = new NRetryController();


	public NWebserviceController(){
		init();
	}

	protected void init() {
		httpParameters = new BasicHttpParams();
		ConnManagerParams.setMaxTotalConnections(httpParameters, 25);
		HttpConnectionParams.setConnectionTimeout(httpParameters, NWebserviceConstants.TIME_OUT);
		HttpConnectionParams.setSoTimeout(httpParameters, NWebserviceConstants.TIME_OUT);
		HttpConnectionParams.setSocketBufferSize(httpParameters, 8192);
		httpClientForeground= new DefaultHttpClient(httpParameters);
		httpClientBackground= new DefaultHttpClient(httpParameters);
		System.setProperty("http.keepAlive", "true");

		if(NWebserviceConstants.COOKIES_CONTROLL_ACTIVE){
			CookieStore cookieStore = loadCookies();
			applyCookies(httpClientForeground, cookieStore);
			applyCookies(httpClientBackground, cookieStore);
		}
	}
	private void applyCookies(DefaultHttpClient httpClient,CookieStore store){
		httpClient.setCookieStore(store);
	}

	/**
	 * This method will apply UsernamePasswordCredentials with this.login and this.password if there are != null
	 * @param mHttpRequest
	 * @throws Exception
	 */
	private void applyUsernamePasswordCredentialsIfSet(HttpRequest mHttpRequest) throws Exception {
		if(login!=null && password !=null)
			setUsernamePasswordCredentials(mHttpRequest,login,password);

		if(login!=null && password == null)
			NLog.e("NWebServiceController applyUsernamePasswordCredentialsIfSet","Login was set, but password was null");

		if(login==null && password != null)
			NLog.e("NWebServiceController applyUsernamePasswordCredentialsIfSet","Password was set, but login was null");
	}



	/**
	 * Use this method to clear login and password for UsernamePasswordCredentials. When they are null, they will not get used.
	 */
	public void clearLoginValues(){
		this.login = null;
		this.password = null;
	}

	/**
	 * Use this method to clear current headers
	 */
	public void clearMyHeaderArrayList(){
		this.myHeaderArrayList.clear();
	}

	/**
	 * Use this method to curl a http-delete request on HTTP_DEFAULT_THREAD
	 * @param url
	 * @return NResponse
	 * @throws Exception
	 */
	public NResponse curlHttpDelete(String url) throws Exception {
		return curlHttpDelete(url,NWebserviceConstants.HTTP_DEFAULT_THREAD);
	}

	/**
	 * Use this method to curl http-delete request with selection of thread
	 * @param url
	 * @param httpThread (NWebserviceConstants.HTTP_DEFAULT_THREAD/HTTP_FOREGROUND_THREAD/HTTP_BACKGROUND_THREAD/HTTP_SAFE_THREAD
	 * @return NResponse
	 * @throws Exception
	 */
	public NResponse curlHttpDelete(String url, int httpThread) throws Exception {
		long ms = 0;
		if(AUTO_LOG_RESPONSE_TIME)
			ms = System.currentTimeMillis();
		if(AUTO_LOG_ALL_CURLS && AUTO_LOG_REQUESTS)
			NLog.d("Requesting curlHttpDelete httpThread "+NWebserviceConstants.getHttpThreadAsString(httpThread),"Url: "+url);

		HttpResponse response = executeHttpDelete(getHTTPDelete(url),httpThread);
		int code = response.getStatusLine().getStatusCode();			
		NJSONObject json = responseToJSON(response);

		if(AUTO_LOG_ALL_CURLS && AUTO_LOG_RESPONSES)
			NLog.d("Returning curlHttpDelete "+ "statusCode: " + code, json.toString(4));

		if(AUTO_LOG_RESPONSE_TIME)
			NLog.d("Response time",url+" took "+(System.currentTimeMillis()-ms)+" ms");
		return new NResponse(json, code);
	}

	/**
	 * Use this method to curl a http-get request, in default thread
	 * @param url
	 * @return NResponse
	 * @throws Exception
	 */
	public NResponse curlHttpGet(String url) throws Exception {
		return curlHttpGet(url,NWebserviceConstants.HTTP_DEFAULT_THREAD);
	}

	/**
	 * Use this method to curl a http-get request with your selection of thread
	 * @param url
	 * @param httpThread (NWebserviceConstants.HTTP_DEFAULT_THREAD/HTTP_FOREGROUND_THREAD/HTTP_BACKGROUND_THREAD/HTTP_SAFE_THREAD
	 * @return NResponse
	 * @throws Exception
	 */
	public NResponse curlHttpGet(String url,int httpThread) throws Exception {
		long ms = 0;
		if(AUTO_LOG_RESPONSE_TIME)
			ms = System.currentTimeMillis();

		if(AUTO_LOG_ALL_CURLS && AUTO_LOG_REQUESTS)
			NLog.d("Requesting curlHttpGet httpThread "+NWebserviceConstants.getHttpThreadAsString(httpThread),"Url: "+url);

		HttpGet mHttpGet = getHTTPGet(url);
		HttpResponse response = executeHttpGet(mHttpGet,httpThread,null);
		int code = response.getStatusLine().getStatusCode();						
		NJSONObject json = responseToJSON(response);

		if(AUTO_LOG_ALL_CURLS && AUTO_LOG_RESPONSES)
			NLog.d("Returning curlHttpGet "+ "statusCode: "+code, json.toString(4));

		if(AUTO_LOG_RESPONSE_TIME)
			NLog.d("Response time",url+" took "+(System.currentTimeMillis()-ms)+" ms");

		return new NResponse(json, code);
	}

	/**
	 * Use this method to curl a http-get request with last-modified and selection of thread
	 * @param url
	 * @param httpThread
	 * @param lastModified
	 * @return NResponse
	 * @throws Exception
	 */
	public NResponse curlHttpGet(String url, int httpThread, String lastModified) throws Exception {
		long ms = 0;
		if(AUTO_LOG_RESPONSE_TIME)
			ms = System.currentTimeMillis();

		if(AUTO_LOG_ALL_CURLS && AUTO_LOG_REQUESTS)
			NLog.d("Requesting curlHttpGet httpThread: " + NWebserviceConstants.getHttpThreadAsString(httpThread)+" lastModified: "+lastModified,"Url: "+url);

		HttpResponse response = executeHttpGet(getHTTPGet(url), httpThread, lastModified);
		int code = response.getStatusLine().getStatusCode();		
		
		NJSONObject json = responseToJSON(response);

		if(AUTO_LOG_ALL_CURLS && AUTO_LOG_RESPONSES)
			NLog.d("Returning curlHttpGet "+ "statusCode: "+code, json.toString(4));

		if(AUTO_LOG_RESPONSE_TIME)
			NLog.d("Response time",url+" took "+(System.currentTimeMillis()-ms)+" ms");

		return new NResponse(json, code);
	}

	/**
	 * Use this method to curl a http-get request with last-modified string
	 * @param url
	 * @param lastModified
	 * @return NResponse
	 * @throws Exception
	 */
	public NResponse curlHttpGet(String url,String lastModified) throws Exception {
		return curlHttpGet(url,NWebserviceConstants.HTTP_DEFAULT_THREAD,lastModified);
	}

	/**
	 * Use this method to curl a http-post request without input
	 * @param url
	 * @return NResponse
	 * @throws Exception
	 */
	public NResponse curlHttpPatch(String url) throws Exception {
		return curlHttpPatch(url, null, NWebserviceConstants.HTTP_DEFAULT_THREAD);
	}

	/**
	 * Use this method to curl a http-post request without input, but selection of thread
	 * @param url
	 * @param httpThread (NWebserviceConstants.HTTP_DEFAULT_THREAD/HTTP_FOREGROUND_THREAD/HTTP_BACKGROUND_THREAD/HTTP_SAFE_THREAD
	 * @return NResponse
	 * @throws Exception
	 */
	public NResponse curlHttpPatch(String url, int httpThread) throws Exception {
		return curlHttpPatch(url, null, httpThread);
	}

	/**
	 * Use this method to curl a http-post request with input
	 * @param url
	 * @param jsonPatch
	 * @return NResponse
	 * @throws Exception
	 */
	public NResponse curlHttpPatch(String url, NJSONObject jsonPatch) throws Exception {
		return curlHttpPatch(url, jsonPatch, NWebserviceConstants.HTTP_DEFAULT_THREAD);
	}

	/**
	 * Use this method to curl a http-post request with input and selection of thread
	 * @param url
	 * @param jsonPatch
	 * @param httpThread (NWebserviceConstants.HTTP_DEFAULT_THREAD/HTTP_FOREGROUND_THREAD/HTTP_BACKGROUND_THREAD/HTTP_SAFE_THREAD
	 * @return NResponse
	 * @throws Exception
	 */
	public NResponse curlHttpPatch(String url, NJSONObject jsonPatch, int httpThread) throws Exception {
		long ms = 0;
		if(AUTO_LOG_RESPONSE_TIME) {
			ms = System.currentTimeMillis();
		}

		if(AUTO_LOG_ALL_CURLS && AUTO_LOG_REQUESTS) {
			if(jsonPatch!=null) {
				NLog.d("Requesting curlHttpPost httpThread " + NWebserviceConstants.getHttpThreadAsString(httpThread), "Input: " + jsonPatch.toString());
			}
			NLog.d("Requesting curlHttpPost httpThread " + NWebserviceConstants.getHttpThreadAsString(httpThread), "Url: " + url);
		}
		HttpResponse response = executeHttpPatch(getHTTPPatch(url), jsonPatch, httpThread);
		int code = response.getStatusLine().getStatusCode();			
		NJSONObject json = responseToJSON(response);

		if(AUTO_LOG_ALL_CURLS && AUTO_LOG_RESPONSES) {
			NLog.d("Returning curlHttpPost " + "statusCode: " + code, json.toString(4));
		}

		if(AUTO_LOG_RESPONSE_TIME) {
			NLog.d("Response time", url + " took " + (System.currentTimeMillis()-ms) + " ms");
		}

		return new NResponse(json, code);
	}

	public NResponse curlHttpPatchAsJSON(String url,NJSONObject jsonInput) throws Exception {
		return curlHttpPatchAsJSON(url,jsonInput,NWebserviceConstants.HTTP_DEFAULT_THREAD);
	}

	public NResponse curlHttpPatchAsJSON(String url,NJSONObject jsonInput, int httpThread) throws Exception {
		return curlHttpPatchAsJSON(url, jsonInput, httpThread, false);
	}

	/**
	 * 
	 * @param url
	 * @param jsonInput
	 * @param httpThread from {@link NWebserviceConstants}
	 * @param explicitUTF8 Whether to use the header that explicitly set the charset to UTF-8
	 * @return
	 * @throws Exception
	 */
	public NResponse curlHttpPatchAsJSON(String url, NJSONObject jsonInput, int httpThread, boolean explicitUTF8) throws Exception {
		long ms = 0;
		if(AUTO_LOG_RESPONSE_TIME) {
			ms = System.currentTimeMillis();
		}

		if(AUTO_LOG_ALL_CURLS && AUTO_LOG_REQUESTS) {
			if(jsonInput!=null) {
				NLog.d("Requesting curlHttpPatch httpThread " + NWebserviceConstants.getHttpThreadAsString(httpThread), "Input: " + jsonInput.toString());
			}
			NLog.d("Requesting curlHttpPatch httpThread " + NWebserviceConstants.getHttpThreadAsString(httpThread), "Url: " + url);
		}
		HttpResponse response;
		if(explicitUTF8) {
			response = executeHttpPatchAsJSON(getHTTPPatch(url), jsonInput, httpThread, true);	
		}
		else {
			response = executeHttpPatchAsJSON(getHTTPPatch(url), jsonInput, httpThread, false);	
		}
		int code = response.getStatusLine().getStatusCode();			
		NJSONObject json = responseToJSON(response);

		if(AUTO_LOG_ALL_CURLS && AUTO_LOG_RESPONSES) {
			NLog.d("Returning curlHttpPatch " + "statusCode: " + code, json.toString(4));
		}

		if(AUTO_LOG_RESPONSE_TIME) {
			NLog.d("Response time", url + " took " + (System.currentTimeMillis()-ms) + " ms");
		}

		return new NResponse(json, code);
	}

	/**
	 * Use this patching method if you need to send files.
	 * Example on github wiki - https://github.com/nodesagency-mobile/Android-NCore/wiki/Webservice---MultiPartEntity-sample
	 * @param url
	 * @param jsonInput
	 * @param multiPartEntityList
	 * @return NResponse
	 * @throws Exception
	 * @author Christian
	 */
	public NResponse curlHttpPatchMultiPart(String url, NJSONObject jsonInput,ArrayList<NMultiPartEntityPair> multiPartEntityList) throws Exception {
		return curlHttpPatchMultiPart(url, jsonInput, multiPartEntityList, NWebserviceConstants.HTTP_DEFAULT_THREAD);
	}

	/**
	 * Use this patching method if you need to send files.
	 * Example on github wiki - https://github.com/nodesagency-mobile/Android-NCore/wiki/Webservice---MultiPartEntity-sample
	 * @param url
	 * @param jsonInput
	 * @param multiPartEntityList
	 * @param httpThread
	 * @return
	 * @throws Exception
	 * @author Christian
	 */
	public NResponse curlHttpPatchMultiPart(String url, NJSONObject jsonInput, ArrayList<NMultiPartEntityPair> multiPartEntityList, int httpThread) throws Exception {
		long ms = 0;
		if(AUTO_LOG_RESPONSE_TIME)
			ms = System.currentTimeMillis();

		if(AUTO_LOG_ALL_CURLS && AUTO_LOG_REQUESTS){
			if(jsonInput!=null)
				NLog.d("Requesting curlHttpPostMultiPart httpThread "+NWebserviceConstants.getHttpThreadAsString(httpThread),"Input: "+jsonInput.toString());
			if(multiPartEntityList!=null && multiPartEntityList.size()>0)
				NLog.d("Requesting curlHttpPostMultiPart httpThread "+NWebserviceConstants.getHttpThreadAsString(httpThread),"MultiPartEntityList size: "+multiPartEntityList.size());
			NLog.d("Requesting curlHttpPostMultiPart httpThread "+NWebserviceConstants.getHttpThreadAsString(httpThread),"Url: "+url);
		}

		MultipartEntity tempMultiPartEntity = hashMapToMultiPair(NUtils.jsonToHashMap(jsonInput));

		if(multiPartEntityList!=null){
			for(NMultiPartEntityPair item : multiPartEntityList){
				tempMultiPartEntity.addPart(item.getName().toString(),item.getByteArrayBody());
			}		
		}

		HttpResponse response = executeHttpPatchWithMultiPartEntity(getHTTPPatch(url),tempMultiPartEntity,httpThread);

		int code = response.getStatusLine().getStatusCode();			
		NJSONObject json = responseToJSON(response);

		if(AUTO_LOG_ALL_CURLS && AUTO_LOG_RESPONSES)
			NLog.d("Returning curlHttpPostMultiPart "+ "statusCode: " + code, json.toString(4));


		if(AUTO_LOG_RESPONSE_TIME)
			NLog.d("Response time",url+" took "+(System.currentTimeMillis()-ms)+" ms");

		return new NResponse(json, code);
	}

	/**
	 * Use this method to curl a http-post request without input
	 * @param url
	 * @return NResponse
	 * @throws Exception
	 */
	public NResponse curlHttpPost(String url) throws Exception {
		return curlHttpPost(url,null,NWebserviceConstants.HTTP_DEFAULT_THREAD);
	}

	/**
	 * Use this method to curl a http-post request without input, but selection of thread
	 * @param url
	 * @param httpThread (NWebserviceConstants.HTTP_DEFAULT_THREAD/HTTP_FOREGROUND_THREAD/HTTP_BACKGROUND_THREAD/HTTP_SAFE_THREAD
	 * @return NResponse
	 * @throws Exception
	 */
	public NResponse curlHttpPost(String url,int httpThread) throws Exception {
		return curlHttpPost(url,null,httpThread);
	}

	/**
	 * Use this method to curl a http-post request with input
	 * @param url
	 * @param jsonPost
	 * @return NResponse
	 * @throws Exception
	 */
	public NResponse curlHttpPost(String url,NJSONObject jsonPost) throws Exception {
		return curlHttpPost(url,jsonPost,NWebserviceConstants.HTTP_DEFAULT_THREAD);
	}

	/**
	 * Use this method to curl a http-post request with input and selection of thread
	 * @param url
	 * @param jsonPost
	 * @param httpThread (NWebserviceConstants.HTTP_DEFAULT_THREAD/HTTP_FOREGROUND_THREAD/HTTP_BACKGROUND_THREAD/HTTP_SAFE_THREAD
	 * @return NResponse
	 * @throws Exception
	 */
	public NResponse curlHttpPost(String url,NJSONObject jsonPost, int httpThread) throws Exception {
		long ms = 0;
		if(AUTO_LOG_RESPONSE_TIME)
			ms = System.currentTimeMillis();

		if(AUTO_LOG_ALL_CURLS && AUTO_LOG_REQUESTS){
			if(jsonPost!=null)
				NLog.d("Requesting curlHttpPost httpThread "+NWebserviceConstants.getHttpThreadAsString(httpThread),"Input: "+jsonPost.toString());
			NLog.d("Requesting curlHttpPost httpThread "+NWebserviceConstants.getHttpThreadAsString(httpThread),"Url: "+url);
		}
		HttpResponse response = executeHttpPost(getHTTPPost(url), jsonPost,httpThread);
		int code = response.getStatusLine().getStatusCode();			
		NJSONObject json = responseToJSON(response);

		if(AUTO_LOG_ALL_CURLS && AUTO_LOG_RESPONSES)
			NLog.d("Returning curlHttpPost "+ "statusCode: " + code, json.toString(4));

		if(AUTO_LOG_RESPONSE_TIME)
			NLog.d("Response time",url+" took "+(System.currentTimeMillis()-ms)+" ms");

		return new NResponse(json, code);
	}

	public NResponse curlHttpPostAsJSON(String url,NJSONObject jsonInput) throws Exception {
		return curlHttpPostAsJSON(url,jsonInput,NWebserviceConstants.HTTP_DEFAULT_THREAD);
	}

	public NResponse curlHttpPostAsJSON(String url,NJSONObject jsonInput, int httpThread) throws Exception {
		return curlHttpPostAsJSON(url, jsonInput, httpThread, false);
	}

	/**
	 * 
	 * @param url
	 * @param jsonInput
	 * @param httpThread from {@link NWebserviceConstants}
	 * @param explicitUTF8 Whether to use the header that explicitly set the charset to UTF-8
	 * @return
	 * @throws Exception
	 */
	public NResponse curlHttpPostAsJSON(String url, NJSONObject jsonInput, int httpThread, boolean explicitUTF8) throws Exception {
		long ms = 0;
		if(AUTO_LOG_RESPONSE_TIME)
			ms = System.currentTimeMillis();

		if(AUTO_LOG_ALL_CURLS && AUTO_LOG_REQUESTS){
			if(jsonInput!=null)
				NLog.d("Requesting curlHttpPost httpThread "+NWebserviceConstants.getHttpThreadAsString(httpThread),"Input: "+jsonInput.toString());
			NLog.d("Requesting curlHttpPost httpThread "+NWebserviceConstants.getHttpThreadAsString(httpThread),"Url: "+url);
		}
		HttpResponse response;
		if(explicitUTF8) {
			response = executeHttpPostAsJSON(getHTTPPost(url), jsonInput,httpThread, true);	
		}
		else {
			response = executeHttpPostAsJSON(getHTTPPost(url), jsonInput,httpThread, false);	
		}
		int code = response.getStatusLine().getStatusCode();			
		NJSONObject json = responseToJSON(response);

		if(AUTO_LOG_ALL_CURLS && AUTO_LOG_RESPONSES)
			NLog.d("Returning curlHttpPost "+ "statusCode: " + code, json.toString(4));

		if(AUTO_LOG_RESPONSE_TIME)
			NLog.d("Response time",url+" took "+(System.currentTimeMillis()-ms)+" ms");

		return new NResponse(json, code);
	}

	/**
	 * Use this posting method if you need to send files.
	 * Example on github wiki - https://github.com/nodesagency-mobile/Android-NCore/wiki/Webservice---MultiPartEntity-sample
	 * @param url
	 * @param jsonInput
	 * @param multiPartEntityList
	 * @return NResponse
	 * @throws Exception
	 */
	public NResponse curlHttpPostMultiPart(String url, NJSONObject jsonInput,ArrayList<NMultiPartEntityPair> multiPartEntityList) throws Exception {
		return curlHttpPostMultiPart(url,jsonInput,multiPartEntityList,NWebserviceConstants.HTTP_DEFAULT_THREAD);
	}

	/**
	 * Use this posting method if you need to send files.
	 * Example on github wiki - https://github.com/nodesagency-mobile/Android-NCore/wiki/Webservice---MultiPartEntity-sample
	 * @param url
	 * @param jsonInput
	 * @param multiPartEntityList
	 * @param httpThread
	 * @return
	 * @throws Exception
	 */
	public NResponse curlHttpPostMultiPart(String url, NJSONObject jsonInput,ArrayList<NMultiPartEntityPair> multiPartEntityList, int httpThread) throws Exception {
		long ms = 0;
		if(AUTO_LOG_RESPONSE_TIME)
			ms = System.currentTimeMillis();

		if(AUTO_LOG_ALL_CURLS && AUTO_LOG_REQUESTS){
			if(jsonInput!=null)
				NLog.d("Requesting curlHttpPostMultiPart httpThread "+NWebserviceConstants.getHttpThreadAsString(httpThread),"Input: "+jsonInput.toString());
			if(multiPartEntityList!=null && multiPartEntityList.size()>0)
				NLog.d("Requesting curlHttpPostMultiPart httpThread "+NWebserviceConstants.getHttpThreadAsString(httpThread),"MultiPartEntityList size: "+multiPartEntityList.size());
			NLog.d("Requesting curlHttpPostMultiPart httpThread "+NWebserviceConstants.getHttpThreadAsString(httpThread),"Url: "+url);
		}

		MultipartEntity tempMultiPartEntity = hashMapToMultiPair(NUtils.jsonToHashMap(jsonInput));

		if(multiPartEntityList!=null){
			for(NMultiPartEntityPair item : multiPartEntityList){
				tempMultiPartEntity.addPart(item.getName().toString(),item.getByteArrayBody());
			}		
		}

		HttpResponse response = executeHttpPostWithMultiPartEntity(getHTTPPost(url),tempMultiPartEntity,httpThread);

		int code = response.getStatusLine().getStatusCode();			
		NJSONObject json = responseToJSON(response);

		if(AUTO_LOG_ALL_CURLS && AUTO_LOG_RESPONSES)
			NLog.d("Returning curlHttpPostMultiPart "+ "statusCode: " + code, json.toString(4));


		if(AUTO_LOG_RESPONSE_TIME)
			NLog.d("Response time",url+" took "+(System.currentTimeMillis()-ms)+" ms");

		return new NResponse(json, code);
	}

	/**
	 * Can be used for uploading videos and pictures,while getting the upload progress.
	 * This can only be used if your byte is encoded as base64 and inputted in json input
	 * https://wiki.ournodes.com/display/android/Base64+encode+example
	 * @param url
	 * @param jsonInput
	 * @param mNMultiPartEnityBase64PairList
	 * @param httpThread
	 * @param progressListener
	 * @return NResponse
	 * @throws Exception
	 */
	public NResponse curlHttpPostMultiPartBase64(String url, NJSONObject jsonInput,ArrayList<NMultiPartEntityBase64Pair> mNMultiPartEnityBase64PairList,int httpThread,NProgressListener progressListener) throws Exception {
		long ms = 0;
		if(AUTO_LOG_RESPONSE_TIME)
			ms = System.currentTimeMillis();

		if(AUTO_LOG_ALL_CURLS && AUTO_LOG_REQUESTS){
			if(jsonInput!=null)
				NLog.d("Requesting curlHttpPostMultiPartBase64 httpThread "+NWebserviceConstants.getHttpThreadAsString(httpThread),"Input: "+jsonInput.toString());
			NLog.d("Requesting curlHttpPostMultiPartBase64 httpThread "+NWebserviceConstants.getHttpThreadAsString(httpThread),"Url: "+url);
		}

		HttpResponse response = executeHttpPostMultipartBase64(getHTTPPost(url), jsonInput,mNMultiPartEnityBase64PairList, progressListener,httpThread);
		int code = response.getStatusLine().getStatusCode();			
		NJSONObject json = responseToJSON(response);

		if(AUTO_LOG_ALL_CURLS && AUTO_LOG_RESPONSES)
			NLog.d("Returning curlHttpPostMultiPartBase64 "+ "statusCode: " + code, json.toString(4));

		if(AUTO_LOG_RESPONSE_TIME)
			NLog.d("Response time",url+" took "+(System.currentTimeMillis()-ms)+" ms");

		return new NResponse(json, code);
	}

	/**
	 * Can be used for uploading videos and pictures,while getting the upload progress.
	 * This can only be used if your byte is encoded as base64 and inputted in json input
	 * https://wiki.ournodes.com/display/android/Base64+encode+example
	 * @param url
	 * @param jsonInput
	 * @param mNMultiPartEnityBase64PairList
	 * @param progressListener
	 * @return NResponse
	 * @throws Exception
	 */
	public NResponse curlHttpPostMultiPartBase64(String url, NJSONObject jsonInput,ArrayList<NMultiPartEntityBase64Pair> mNMultiPartEnityBase64PairList,NProgressListener progressListener) throws Exception {
		return curlHttpPostMultiPartBase64(url,jsonInput,mNMultiPartEnityBase64PairList,NWebserviceConstants.HTTP_DEFAULT_THREAD,progressListener);
	}

	/**
	 * Use this method to curl a http-put request without input
	 * @param url
	 * @return NResponse
	 * @throws Exception
	 */
	public NResponse curlHttpPut(String url) throws Exception {
		return curlHttpPut(url,null,NWebserviceConstants.HTTP_DEFAULT_THREAD);
	}

	/**
	 * Use this method to curl a http-put request without input, but with selection of thread
	 * @param url
	 * @param httpThread (NWebserviceConstants.HTTP_DEFAULT_THREAD/HTTP_FOREGROUND_THREAD/HTTP_BACKGROUND_THREAD/HTTP_SAFE_THREAD
	 * @return
	 * @throws Exception
	 */
	public NResponse curlHttpPut(String url, int httpThread) throws Exception {
		return curlHttpPut(url,null,httpThread);
	}

	/**
	 * Use this method to curl a http-put request encoding the json as a formentity
	 * @param url
	 * @param jsonPost
	 * @return NResponse
	 * @throws Exception
	 */
	public NResponse curlHttpPut(String url,NJSONObject jsonPost) throws Exception {
		return curlHttpPut(url,jsonPost,NWebserviceConstants.HTTP_DEFAULT_THREAD);
	}

	/**
	 * Use this method to curl a http-put request with selection of thread
	 * @param url
	 * @param jsonPost
	 * @param httpThread (NWebserviceConstants.HTTP_DEFAULT_THREAD/HTTP_FOREGROUND_THREAD/HTTP_BACKGROUND_THREAD/HTTP_SAFE_THREAD
	 * @return NResponse
	 * @throws Exception
	 */
	public NResponse curlHttpPut(String url,NJSONObject jsonPost, int httpThread) throws Exception {
		long ms = 0;
		if(AUTO_LOG_RESPONSE_TIME)
			ms = System.currentTimeMillis();

		if(AUTO_LOG_ALL_CURLS && AUTO_LOG_REQUESTS){
			if(jsonPost!=null)
				NLog.d("Requesting curlHttpPut httpThread "+NWebserviceConstants.getHttpThreadAsString(httpThread),"Input: "+jsonPost.toString());
			NLog.d("Requesting curlHttpPut httpThread "+NWebserviceConstants.getHttpThreadAsString(httpThread),"Url: "+url);
		}
		HttpResponse response = executeHttpPut(getHTTPPut(url), jsonPost,httpThread);
		int code = response.getStatusLine().getStatusCode();			
		NJSONObject json = responseToJSON(response);

		if(AUTO_LOG_ALL_CURLS && AUTO_LOG_RESPONSES)
			NLog.d("Returning curlHttpPut "+ "statusCode: " + code, json.toString(4));

		if(AUTO_LOG_RESPONSE_TIME)
			NLog.d("Response time",url+" took "+(System.currentTimeMillis()-ms)+" ms");

		return new NResponse(json, code);
	}

	/**
	 * Use this putting method if you need to send files.
	 * Example on github wiki - https://github.com/nodesagency-mobile/Android-NCore/wiki/Webservice---MultiPartEntity-sample
	 * @param url
	 * @param jsonInput
	 * @param multiPartEntityList
	 * @return NResponse
	 * @throws Exception
	 */
	public NResponse curlHttpPutMultiPart(String url, NJSONObject jsonInput,ArrayList<NMultiPartEntityPair> multiPartEntityList) throws Exception {
		return curlHttpPutMultiPart(url,jsonInput,multiPartEntityList,NWebserviceConstants.HTTP_DEFAULT_THREAD);
	}

	/**
	 * Use this putting method if you need to send files.
	 * Example on github wiki - https://github.com/nodesagency-mobile/Android-NCore/wiki/Webservice---MultiPartEntity-sample
	 * @param url
	 * @param jsonInput
	 * @param multiPartEntityList
	 * @param httpThread
	 * @return
	 * @throws Exception
	 */
	public NResponse curlHttpPutMultiPart(String url, NJSONObject jsonInput, ArrayList<NMultiPartEntityPair> multiPartEntityList, int httpThread) throws Exception {
		long ms = 0;
		if(AUTO_LOG_RESPONSE_TIME)
			ms = System.currentTimeMillis();

		if(AUTO_LOG_ALL_CURLS && AUTO_LOG_REQUESTS){
			if(jsonInput!=null)
				NLog.d("Requesting curlHttpPutMultiPart httpThread "+NWebserviceConstants.getHttpThreadAsString(httpThread),"Input: "+jsonInput.toString());
			if(multiPartEntityList!=null && multiPartEntityList.size()>0)
				NLog.d("Requesting curlHttpPutMultiPart httpThread "+NWebserviceConstants.getHttpThreadAsString(httpThread),"MultiPartEntityList size: "+multiPartEntityList.size());
			NLog.d("Requesting curlHttpPutMultiPart httpThread "+NWebserviceConstants.getHttpThreadAsString(httpThread),"Url: "+url);
		}

		MultipartEntity tempMultiPartEntity = hashMapToMultiPair(NUtils.jsonToHashMap(jsonInput));

		if(multiPartEntityList!=null){
			for(NMultiPartEntityPair item : multiPartEntityList){
				tempMultiPartEntity.addPart(item.getName().toString(),item.getByteArrayBody());
			}		
		}

		HttpResponse response = executeHttpPutWithMultiPartEntity(getHTTPPut(url), tempMultiPartEntity, httpThread);

		int code = response.getStatusLine().getStatusCode();			
		NJSONObject json = responseToJSON(response);

		if(AUTO_LOG_ALL_CURLS && AUTO_LOG_RESPONSES)
			NLog.d("Returning curlHttpPutMultiPart "+ "statusCode: " + code, json.toString(4));


		if(AUTO_LOG_RESPONSE_TIME)
			NLog.d("Response time",url+" took "+(System.currentTimeMillis()-ms)+" ms");

		return new NResponse(json, code);
	}

	/**
	 * Use this method to curl a http-put request with selection of thread
	 * @param url
	 * @param jsonPost
	 * @param httpThread (NWebserviceConstants.HTTP_DEFAULT_THREAD/HTTP_FOREGROUND_THREAD/HTTP_BACKGROUND_THREAD/HTTP_SAFE_THREAD
	 * @return NResponse
	 * @throws Exception
	 */
	public NResponse curlHttpPutAsJSON(String url,NJSONObject jsonPost, int httpThread) throws Exception {
		long ms = 0;
		if(AUTO_LOG_RESPONSE_TIME)
			ms = System.currentTimeMillis();

		if(AUTO_LOG_ALL_CURLS && AUTO_LOG_REQUESTS){
			if(jsonPost!=null)
				NLog.d("Requesting curlHttpPut httpThread "+NWebserviceConstants.getHttpThreadAsString(httpThread),"Input: "+jsonPost.toString());
			NLog.d("Requesting curlHttpPut httpThread "+NWebserviceConstants.getHttpThreadAsString(httpThread),"Url: "+url);
		}
		HttpResponse response = executeHttpPutAsJSON(getHTTPPut(url), jsonPost,httpThread);
		int code = response.getStatusLine().getStatusCode();			
		NJSONObject json = responseToJSON(response);

		if(AUTO_LOG_ALL_CURLS && AUTO_LOG_RESPONSES)
			NLog.d("Returning curlHttpPut "+ "statusCode: " + code, json.toString(4));

		if(AUTO_LOG_RESPONSE_TIME)
			NLog.d("Response time",url+" took "+(System.currentTimeMillis()-ms)+" ms");

		return new NResponse(json, code);
	}

	/**
	 * Use this method to execute a http-delete request
	 * @param httpDelete
	 * @param httpThread (NWebserviceConstants.HTTP_DEFAULT_THREAD/HTTP_FOREGROUND_THREAD/HTTP_BACKGROUND_THREAD/HTTP_SAFE_THREAD
	 * @return HttpResponse
	 * @throws Exception
	 */
	public HttpResponse executeHttpDelete(HttpDelete httpDelete,int httpThread) throws Exception {
		HttpResponse response = null;

		if(AUTO_LOG_AS_CURL)
			logCurl(httpDelete.getURI().toString(),httpDelete,null,false);

		DefaultHttpClient httpClient = getHttpClient(httpThread);
		try{
			response = httpClient.execute(httpDelete);
			saveCookies(httpClient);
			return response;
		}
		catch(Exception e){
			if(mNRetryController.retryBoolean(httpDelete.toString())|| !useRetry){
				NLog.d("NWebserviceController executeHttpDelete","Throwing exception");
				throw e;
			}
			else{
				NLog.w("NWebserviceController executeHttpDelete",e.toString());
				useWait();
				NLog.d("NWebserviceController executeHttpDelete","Retrying.... "+mNRetryController.getCurrentRetryAmount(httpDelete.toString()));
				return executeHttpDelete(httpDelete,httpThread);
			}
		}	
	}

	/**
	 * Use this method to execute a http-get request with last modified String
	 * @param httpGet
	 * @param httpThread 
	 * @param lastModified
	 * @return HttpResponse(NWebserviceConstants.HTTP_DEFAULT_THREAD/HTTP_FOREGROUND_THREAD/HTTP_BACKGROUND_THREAD/HTTP_SAFE_THREAD
	 * @throws Exception
	 */
	public HttpResponse executeHttpGet(HttpGet httpGet, int httpThread, String lastModified) throws Exception {
		HttpResponse response = null;
		if(lastModified!=null && NWebserviceConstants.LAST_MODIFIED_ACTIVE)
			httpGet.addHeader(getLastModifiedToRequest(lastModified));

		if(AUTO_LOG_AS_CURL)
			logCurl(httpGet.getURI().toString(),httpGet,null,false);

		DefaultHttpClient httpClient = getHttpClient(httpThread);
		try{
			response = httpClient.execute(httpGet);
			if(NWebserviceConstants.isResponseCoudeServerRelated(response)){
				if(!mNRetryController.retryBoolean(httpGet.toString()) && useRetryOn500Errors){
					NLog.d("NWebserviceController executeHttpGet",response.getStatusLine().getStatusCode()+" error");
					useWait();
					return executeHttpGet(httpGet,httpThread,lastModified);
				}
			}
			saveCookies(httpClient);	
			mNRetryController.deleteNoLog(httpGet.toString());
			return response;

		}
		catch(Exception e){
			if(mNRetryController.retryBoolean(httpGet.toString()) || !useRetry){
				NLog.d("NWebserviceController executeHttpGet","Throwing exception");
				throw e;
			}
			else{
				NLog.d("NWebserviceController executeHttpGet",e.toString());
				useWait();
				NLog.d("NWebserviceController executeHttpGet","Retrying.... "+mNRetryController.getCurrentRetryAmount(httpGet.toString()));
				return executeHttpGet(httpGet,httpThread,lastModified);
			}
		}
	}

	/**
	 * Use this method to execute a http-patch request
	 * @param httpPatch
	 * @param input
	 * @param httpThread (NWebserviceConstants.HTTP_DEFAULT_THREAD/HTTP_FOREGROUND_THREAD/HTTP_BACKGROUND_THREAD/HTTP_SAFE_THREAD
	 * @return HttpResponse
	 * @throws Exception
	 */
	public HttpResponse executeHttpPatch(HttpPatch httpPatch, NJSONObject input, int httpThread) throws Exception {
		HttpResponse response = null;
		if(input!=null) {
			httpPatch.setEntity(jsonToUrlEncodedFormEntity(input));
		}

		if(AUTO_LOG_AS_CURL) {
			logCurl(httpPatch.getURI().toString(),httpPatch,input,false);
		}

		DefaultHttpClient httpClient = getHttpClient(httpThread);
		try {
			response = httpClient.execute(httpPatch);
			saveCookies(httpClient);
			mNRetryController.deleteNoLog(httpPatch.toString());
			return response;
		} catch(Exception e) {
			if(mNRetryController.retryBoolean(httpPatch.toString()) || !useRetry) {
				NLog.d("NWebserviceController executeHttpPost", "Throwing exception");
				throw e;
			}
			else {
				NLog.w("NWebserviceController executeHttpPost", e.toString());
				useWait();
				NLog.d("NWebserviceController executeHttpPost", "Retrying.... " + mNRetryController.getCurrentRetryAmount(httpPatch.toString()));
				return executeHttpPatch(httpPatch, input, httpThread);
			}
		}
	}

	/**
	 * Use this method to execute a http-post request
	 * @param httpPost
	 * @param input
	 * @param httpThread (NWebserviceConstants.HTTP_DEFAULT_THREAD/HTTP_FOREGROUND_THREAD/HTTP_BACKGROUND_THREAD/HTTP_SAFE_THREAD
	 * @return HttpResponse
	 * @throws Exception
	 */
	public HttpResponse executeHttpPost(HttpPost httpPost,NJSONObject input, int httpThread) throws Exception {
		HttpResponse response = null;
		if(input!=null)
			httpPost.setEntity(jsonToUrlEncodedFormEntity(input));


		if(AUTO_LOG_AS_CURL)
			logCurl(httpPost.getURI().toString(),httpPost,input,false);


		DefaultHttpClient httpClient = getHttpClient(httpThread);
		try{
			response = httpClient.execute(httpPost);
			saveCookies(httpClient);
			mNRetryController.deleteNoLog(httpPost.toString());
			return response;
		}
		catch(Exception e){
			if(mNRetryController.retryBoolean(httpPost.toString()) || !useRetry){
				NLog.d("NWebserviceController executeHttpPost","Throwing exception");
				throw e;
			}
			else{
				NLog.w("NWebserviceController executeHttpPost",e.toString());
				useWait();
				NLog.d("NWebserviceController executeHttpPost","Retrying.... "+mNRetryController.getCurrentRetryAmount(httpPost.toString()));
				return executeHttpPost(httpPost,input,httpThread);
			}
		}
	}

	private void logCurl(String url, HttpRequest mHttpRequest, NJSONObject input, boolean postAsJson) {
		String curl = "curl";
		if(login != null && password != null)
			curl+= " --user "+"\""+login+":"+password+"\"";

		if(mHttpRequest instanceof HttpPatch)
			curl+=" -X PATCH";
		else if(mHttpRequest instanceof HttpPost)
			curl+=" -X POST";
		else if(mHttpRequest instanceof HttpGet)
			curl+= " -X GET";
		else if(mHttpRequest instanceof HttpDelete)
			curl+=" -X DELETE";
		else if(mHttpRequest instanceof HttpPut)
			curl+= " -X PUT";

		curl+=" "+url;

		for(Header header : mHttpRequest.getAllHeaders()){
			curl+=" -H "+"\'"+header.getName()+":"+header.getValue()+"\'";
		}

		if(mHttpRequest instanceof HttpPost){
			if(((HttpPost)mHttpRequest).getEntity() != null){
				Header contentType = ((HttpPost)mHttpRequest).getEntity().getContentType();
				curl+=" -H "+"\'"+contentType.getName()+":"+contentType.getValue()+"\'";
			}
		}
		if(mHttpRequest instanceof HttpPut){
			if(((HttpPut)mHttpRequest).getEntity() != null){
				Header contentType = ((HttpPut)mHttpRequest).getEntity().getContentType();
				curl+=" -H "+"\'"+contentType.getName()+":"+contentType.getValue()+"\'";
			}
		}

		if( input!= null && input.length() > 0 ){
			if( !postAsJson ){
				Iterator<?> iter = input.keys();
				while ( iter.hasNext() ) {
					String key = (String)iter.next();
					String value = input.optString(key);
					curl+=" -d \""+key+"="+value+"\"";
				}
			}
			else{
				curl+=" -d "+input.toString();
			}
		}
		NLog.d("cURL send",curl);
	}

	public HttpResponse executeHttpPatchtAsJSON(HttpPatch httpPatch,NJSONObject input, int httpThread) throws Exception {
		return executeHttpPostAsJSON(httpPatch, input, httpThread, false);
	}

	public HttpResponse executeHttpPatchAsJSON(HttpPatch httpPatch, NJSONObject input, int httpThread, boolean explicitUTF8) throws Exception {
		HttpResponse response = null;
		if(input!=null) {
			if(explicitUTF8) {
				httpPatch.setEntity(jsonToHttpEntityUTF8(input));
			}
			else {
				httpPatch.setEntity(jsonToHttpEntityNoEncoding(input));
			}
		}

		if(AUTO_LOG_AS_CURL) {
			logCurl(httpPatch.getURI().toString(), httpPatch, input, true);
		}

		DefaultHttpClient httpClient = getHttpClient(httpThread);

		try {
			response = httpClient.execute(httpPatch);
			saveCookies(httpClient);
			mNRetryController.deleteNoLog(httpPatch.toString());
			return response;
		} catch(Exception e) {
			if(mNRetryController.retryBoolean(httpPatch.toString()) || !useRetry) {
				NLog.d("NWebserviceController httpPatch","Throwing exception");
				throw e;
			}
			else {
				NLog.w("NWebserviceController executeHttpPost", e.toString());
				useWait();
				NLog.d("NWebserviceController executeHttpPost", "Retrying.... " + mNRetryController.getCurrentRetryAmount(httpPatch.toString()));
				return executeHttpPost(httpPatch, input, httpThread);
			}
		}
	}

	public HttpResponse executeHttpPostAsJSON(HttpPost httpPost,NJSONObject input, int httpThread) throws Exception {
		return executeHttpPostAsJSON(httpPost, input, httpThread, false);
	}

	public HttpResponse executeHttpPostAsJSON(HttpPost httpPost,NJSONObject input, int httpThread, boolean explicitUTF8) throws Exception {
		HttpResponse response = null;
		if(input!=null) {
			if(explicitUTF8) {
				httpPost.setEntity(jsonToHttpEntityUTF8(input));
			}
			else {
				httpPost.setEntity(jsonToHttpEntityNoEncoding(input));
			}
		}

		if(AUTO_LOG_AS_CURL)
			logCurl(httpPost.getURI().toString(),httpPost,input,true);

		DefaultHttpClient httpClient = getHttpClient(httpThread);

		try{
			response = httpClient.execute(httpPost);
			saveCookies(httpClient);
			mNRetryController.deleteNoLog(httpPost.toString());
			return response;
		}
		catch(Exception e){
			if(mNRetryController.retryBoolean(httpPost.toString()) || !useRetry){
				NLog.d("NWebserviceController executeHttpPost","Throwing exception");
				throw e;
			}
			else{
				NLog.w("NWebserviceController executeHttpPost",e.toString());
				useWait();
				NLog.d("NWebserviceController executeHttpPost","Retrying.... "+mNRetryController.getCurrentRetryAmount(httpPost.toString()));
				return executeHttpPost(httpPost,input,httpThread);
			}
		}
	}

	/**
	 * This method can be used for uploading videos and pictures,
	 * while getting the upload progress. This can only be used if your byte is encoded as base64 and inputted in json input
	 * https://github.com/nodesagency-mobile/Android-NCore/wiki/Webservice---Base64-Multipart-entity-httpPost-sample
	 * @author Thomas Nielsen
	 * @param httpPost
	 * @param input
	 *            A NJSONObject which can, for example, include a ContentBody cb
	 *            = new StringBody(base64), where base64 is a video encoded as a
	 *            base64 String.
	 * @param mNMultiPartEnityBase64PairList 
	 * @param progressListener
	 *            A ProgressListener with a callback that sends the number of
	 *            bytes that has been sent. Compare this to f.x.
	 *            cb.getContentLength();, and you will get a progress in
	 *            percent.
	 * @param httpThread
	 * @return
	 * @throws Exception
	 */
	public HttpResponse executeHttpPostMultipartBase64(HttpPost httpPost, NJSONObject input, ArrayList<NMultiPartEntityBase64Pair> mNMultiPartEnityBase64PairList, NProgressListener progressListener, int httpThread) throws Exception {
		HttpResponse response = null;

		if(AUTO_LOG_AS_CURL)
			logCurl(httpPost.getURI().toString(),httpPost,input,false);

		if (input != null) {
			httpPost.setEntity(hashMapToNMultiPair(NUtils.jsonToHashMap(input), progressListener,mNMultiPartEnityBase64PairList));
		}

		DefaultHttpClient httpClient = getHttpClient(httpThread);
		try{
			response = httpClient.execute(httpPost);
			saveCookies(httpClient);
			mNRetryController.deleteNoLog(httpPost.toString());
			return response;
		}
		catch(Exception e) {
			if(mNRetryController.retryBoolean(httpPost.toString())|| !useRetry){
				NLog.d("NWebserviceController executeHttpPostMultipartBase64","Throwing exception");
				throw e;
			}
			else{
				NLog.w("NWebserviceController executeHttpPostMultipartBase64",e.toString());
				useWait();
				NLog.d("NWebserviceController executeHttpPostMultipartBase64","Retrying.... "+mNRetryController.getCurrentRetryAmount(httpPost.toString()));
				return executeHttpPostMultipartBase64(httpPost,input,mNMultiPartEnityBase64PairList,progressListener,httpThread);
			}
		}
	}

	/**
	 * This method is to execute multiPartEntity post
	 * @param httpPost
	 * @param multipartEntity
	 * @param httpThread
	 * @return HttpResponse
	 * @throws Exception
	 */
	public HttpResponse executeHttpPostWithMultiPartEntity(HttpPost httpPost,MultipartEntity multipartEntity, int httpThread) throws Exception {
		HttpResponse response = null;
		if(multipartEntity!=null)
			httpPost.setEntity(multipartEntity);

		if(AUTO_LOG_AS_CURL)
			logCurl(httpPost.getURI().toString(),httpPost,null,false);

		DefaultHttpClient httpClient = getHttpClient(httpThread);
		try{
			response = httpClient.execute(httpPost);
			saveCookies(httpClient);
			mNRetryController.deleteNoLog(httpPost.toString());
			return response;
		}
		catch(Exception e){
			if(mNRetryController.retryBoolean(httpPost.toString()) || !useRetry){
				NLog.d("NWebserviceController executeHttpPostWithMultiPartEntity","Throwing exception");
				throw e;
			}
			else{
				NLog.w("NWebserviceController executeHttpPostWithMultiPartEntity",e.toString());
				useWait();
				NLog.d("NWebserviceController executeHttpPostWithMultiPartEntity","Retrying.... "+mNRetryController.getCurrentRetryAmount(httpPost.toString()));
				return executeHttpPostWithMultiPartEntity(httpPost,multipartEntity,httpThread);
			}
		}
	}

	/**
	 * This method is to execute multiPartEntity patch
	 * @param httpPatch
	 * @param multipartEntity
	 * @param httpThread
	 * @return HttpResponse
	 * @throws Exception
	 */
	public HttpResponse executeHttpPatchWithMultiPartEntity(HttpPatch httpPatch, MultipartEntity multipartEntity, int httpThread) throws Exception {
		HttpResponse response = null;
		if(multipartEntity!=null)
			httpPatch.setEntity(multipartEntity);

		if(AUTO_LOG_AS_CURL)
			logCurl(httpPatch.getURI().toString(),httpPatch,null,false);

		DefaultHttpClient httpClient = getHttpClient(httpThread);
		try{
			response = httpClient.execute(httpPatch);
			saveCookies(httpClient);
			mNRetryController.deleteNoLog(httpPatch.toString());
			return response;
		}
		catch(Exception e){
			if(mNRetryController.retryBoolean(httpPatch.toString()) || !useRetry){
				NLog.d("NWebserviceController executeHttpPatchWithMultiPartEntity", "Throwing exception");
				throw e;
			}
			else{
				NLog.w("NWebserviceController executeHttpPatchWithMultiPartEntity",e.toString());
				useWait();
				NLog.d("NWebserviceController executeHttpPatchWithMultiPartEntity","Retrying.... " + mNRetryController.getCurrentRetryAmount(httpPatch.toString()));
				return executeHttpPatchWithMultiPartEntity(httpPatch, multipartEntity, httpThread);
			}
		}
	}

	/**
	 * This method is to execute multiPartEntity put
	 * @param httpPut
	 * @param multipartEntity
	 * @param httpThread
	 * @return HttpResponse
	 * @throws Exception
	 */
	public HttpResponse executeHttpPutWithMultiPartEntity(HttpPut httpPut, MultipartEntity multipartEntity, int httpThread) throws Exception {
		HttpResponse response = null;
		if(multipartEntity!=null)
			httpPut.setEntity(multipartEntity);

		if(AUTO_LOG_AS_CURL)
			logCurl(httpPut.getURI().toString(),httpPut,null,false);

		DefaultHttpClient httpClient = getHttpClient(httpThread);
		try{
			response = httpClient.execute(httpPut);
			saveCookies(httpClient);
			mNRetryController.deleteNoLog(httpPut.toString());
			return response;
		}
		catch(Exception e){
			if(mNRetryController.retryBoolean(httpPut.toString()) || !useRetry){
				NLog.d("NWebserviceController executeHttpPutWithMultiPartEntity", "Throwing exception");
				throw e;
			}
			else{
				NLog.w("NWebserviceController executeHttpPutWithMultiPartEntity",e.toString());
				useWait();
				NLog.d("NWebserviceController executeHttpPutWithMultiPartEntity","Retrying.... " + mNRetryController.getCurrentRetryAmount(httpPut.toString()));
				return executeHttpPutWithMultiPartEntity(httpPut, multipartEntity, httpThread);
			}
		}
	}

	/**
	 * Use this method to execute a http-put request
	 * @param httpPut
	 * @param input
	 * @param httpThread (NWebserviceConstants.HTTP_DEFAULT_THREAD/HTTP_FOREGROUND_THREAD/HTTP_BACKGROUND_THREAD/HTTP_SAFE_THREAD
	 * @return HttpResponse
	 * @throws Exception
	 */
	public HttpResponse executeHttpPut(HttpPut httpPut,NJSONObject input, int httpThread) throws Exception {
		HttpResponse response = null;
		if(input!=null)
			httpPut.setEntity(jsonToUrlEncodedFormEntity(input));

		if(AUTO_LOG_AS_CURL)
			logCurl(httpPut.getURI().toString(),httpPut,input,false);

		DefaultHttpClient httpClient = getHttpClient(httpThread);
		try{
			response= httpClient.execute(httpPut);
			saveCookies(httpClient);
			mNRetryController.deleteNoLog(httpPut.toString());
			return response;
		}
		catch(Exception e){
			if(mNRetryController.retryBoolean(httpPut.toString())|| !useRetry){
				NLog.d("NWebserviceController executeHttpPut","Throwing exception");
				throw e;
			}
			else{
				NLog.w("NWebserviceController executeHttpPut",e.toString());
				useWait();
				NLog.d("NWebserviceController executeHttpPut","Retrying.... "+mNRetryController.getCurrentRetryAmount(httpPut.toString()));
				return executeHttpPut(httpPut,input,httpThread);
			}
		}
	}

	/**
	 * Use this method to execute a http-put request
	 * @param httpPut
	 * @param input
	 * @param httpThread (NWebserviceConstants.HTTP_DEFAULT_THREAD/HTTP_FOREGROUND_THREAD/HTTP_BACKGROUND_THREAD/HTTP_SAFE_THREAD
	 * @return HttpResponse
	 * @throws Exception
	 */
	public HttpResponse executeHttpPutAsJSON(HttpPut httpPut, NJSONObject input, int httpThread) throws Exception {
		HttpResponse response = null;
		if(input!=null)
			httpPut.setEntity(jsonToHttpEntityNoEncoding(input));

		if(AUTO_LOG_AS_CURL)
			logCurl(httpPut.getURI().toString(),httpPut,input,false);

		DefaultHttpClient httpClient = getHttpClient(httpThread);
		try{
			response= httpClient.execute(httpPut);
			saveCookies(httpClient);
			mNRetryController.deleteNoLog(httpPut.toString());
			return response;
		}
		catch(Exception e){
			if(mNRetryController.retryBoolean(httpPut.toString())|| !useRetry){
				NLog.d("NWebserviceController executeHttpPut","Throwing exception");
				throw e;
			}
			else{
				NLog.w("NWebserviceController executeHttpPut",e.toString());
				useWait();
				NLog.d("NWebserviceController executeHttpPut","Retrying.... "+mNRetryController.getCurrentRetryAmount(httpPut.toString()));
				return executeHttpPut(httpPut,input,httpThread);
			}
		}
	}

	/**
	 * Use this method to do a System.gc() and sleep the input ms
	 * @param ms
	 * @throws Exception
	 */
	public void freeMemory(int ms) throws Exception {
		NLog.d("Cooldown",ms+" ms");
		System.gc();
		Thread.sleep(ms);
	}

	private DefaultHttpClient getBackgroundHttpClient()  {
		return httpClientBackground;		
	}

	private DefaultHttpClient getForegroundHttpClient()  {
		return httpClientForeground;
	}

	/**
	 * This method will return a Header[] with all the given headers from setHeaders() & setUsernamePasswordCredentials; 
	 * But also "Accept-Encoding", "gzip" & "Accept", "application/json"
	 * @return
	 */
	private Header[] getHeaders(){
		ArrayList<Header> headerArrayList = new ArrayList<Header>();
		if(ACCEPT_GZIP)
			headerArrayList.add(new BasicHeader("Accept-Encoding", "gzip"));
		if(ACCEPT_JSON)
			headerArrayList.add(new BasicHeader("Accept", "application/json"));

		for(Header header : myHeaderArrayList)
			headerArrayList.add(header);

		return 	headerArrayList.toArray(new Header[headerArrayList.size()]);
	}

	/**
	 * Use this method to get a httpClient fitting the API level and the input (httpThread)
	 * This method will free memory if isEnoughMemory is overrided and used 
	 * @param httpThread
	 * @return
	 */
	public DefaultHttpClient getHttpClient(int httpThread){
		try{
			if(!isEnoughMemory())
				freeMemory(2500);
		}
		catch(Exception e){
            NLog.e("NWebserviceController getHttpClient",e.toString());
		}

		switch(httpThread){
		case 0:
			if(Build.VERSION.SDK_INT >=13)
				return getForegroundHttpClient();
			else
				return getThreadSafeClient();	
		case 1:
			return getForegroundHttpClient();	
		case 2: 
			return getBackgroundHttpClient();
		case 3:
			return getThreadSafeClient();	
		default:
			return getThreadSafeClient();
		}
	}

	/**
	 * Use this method to get a HttpDelete object with the headers set from setHeaders
	 * @param url
	 * @return HttpDelete
	 * @throws Exception
	 */
	public HttpDelete getHTTPDelete(String url) throws Exception {

		HttpDelete httpDelete = new HttpDelete();
		httpDelete.setURI(new URI(url));

		for(Header header : getHeaders())
			httpDelete.addHeader(header);

		applyUsernamePasswordCredentialsIfSet(httpDelete);

		if(AUTO_LOG_HEADERS){
			for(Header item : httpDelete.getAllHeaders())
				NLog.d(item.getName(),item.getValue());
		}
		return httpDelete;
	}

	/**
	 * Use this method to get a HttpGet object with the headers set in setHeaders
	 * @param url
	 * @return HttpGet
	 * @throws Exception
	 */
	public HttpGet getHTTPGet(String url) throws Exception {
		HttpGet httpGet = new HttpGet();
		httpGet.setURI(new URI(url));

		applyUsernamePasswordCredentialsIfSet(httpGet);

		for(Header header : getHeaders())
			httpGet.addHeader(header);


		if(AUTO_LOG_HEADERS){
			for(Header item : httpGet.getAllHeaders())
				NLog.d(item.getName(),item.getValue());
		}
		return httpGet;
	}

	/**
	 * Use this method to get a HttpGet obhect with the headers set from setHeaders and lastmodified input
	 * @param url
	 * @param lastModifiedInput
	 * @return HttpGet
	 * @throws Exception
	 */
	public HttpGet getHTTPGet(String url,String lastModifiedInput) throws Exception {
		HttpGet httpGet = new HttpGet();
		httpGet.setURI(new URI(url));

		for(Header header : getHeaders())
			httpGet.addHeader(header);

		applyUsernamePasswordCredentialsIfSet(httpGet);
		if(lastModifiedInput != null)
			httpGet.addHeader(getLastModifiedToRequest(lastModifiedInput));
		if(AUTO_LOG_HEADERS){
			for(Header item : httpGet.getAllHeaders())
				NLog.d(item.getName(), item.getValue());
		}
		return httpGet;
	}

	/**
	 * Use this method to get a HttpPatch object with the header set from setHeaders
	 * @param url
	 * @return HttpPatch
	 * @throws Exception
	 * @author Christian Thomsen 2014
	 */

	public HttpPatch getHTTPPatch(String url) throws Exception {

		HttpPatch httpPatch = new HttpPatch();
		httpPatch.setURI(new URI(url));

		for(Header header : getHeaders())
			httpPatch.addHeader(header);

		applyUsernamePasswordCredentialsIfSet(httpPatch);
		if(AUTO_LOG_HEADERS){
			for(Header item : httpPatch.getAllHeaders())
				NLog.d(item.getName(),item.getValue());
		}
		return httpPatch;
	}

	/**
	 * Use this method to get a HttpPost object with the header set from setHeaders
	 * @param url
	 * @return HttpPost
	 * @throws Exception
	 */

	public HttpPost getHTTPPost(String url) throws Exception {

		HttpPost httpPost = new HttpPost();
		httpPost.setURI(new URI(url));

		for(Header header : getHeaders())
			httpPost.addHeader(header);

		applyUsernamePasswordCredentialsIfSet(httpPost);
		if(AUTO_LOG_HEADERS){
			for(Header item : httpPost.getAllHeaders())
				NLog.d(item.getName(),item.getValue());
		}
		return httpPost;
	}

	/**
	 * Use this method to get a HttpPut object with the headers set from setHeaders
	 * @param url
	 * @return HttpPut
	 * @throws Exception
	 */
	public HttpPut getHTTPPut(String url) throws Exception {

		HttpPut httpPut = new HttpPut();
		httpPut.setURI(new URI(url));

		for(Header header : getHeaders())
			httpPut.addHeader(header);

		applyUsernamePasswordCredentialsIfSet(httpPut);
		if(AUTO_LOG_HEADERS){
			for(Header item : httpPut.getAllHeaders())
				NLog.d(item.getName(),item.getValue());
		}
		return httpPut;
	}
	
	/**
	 * Will look up if there is any header Last-Modifed 
	 * @param response
	 * @return String or null
	 */
	private String getLastModifiedFromResponse(HttpResponse response){
		Header lastModifiedHeader = response.getFirstHeader("Last-Modified");
		if(lastModifiedHeader != null)
			return lastModifiedHeader.getValue();
		else
			return null;
	}

	/**
	 * Create If-Modified-Since header
	 * @param lastModifiedInput
	 * @return
	 */
	private Header getLastModifiedToRequest(String lastModifiedInput){
		return new BasicHeader("If-Modified-Since", lastModifiedInput);
	}

	/**
	 * Get current headers used
	 * @return list of headers
	 */
	public ArrayList<BasicHeader> getMyHeaderArrayList(){
		return myHeaderArrayList;
	}

	public DefaultHttpClient getThreadSafeClient()  {

		DefaultHttpClient client = new DefaultHttpClient();
		ClientConnectionManager mgr = client.getConnectionManager();

		client = new DefaultHttpClient(new ThreadSafeClientConnManager(httpParameters,  mgr.getSchemeRegistry()), httpParameters);

		if(NWebserviceConstants.COOKIES_CONTROLL_ACTIVE){
			CookieStore cookieStore = loadCookies();
			applyCookies(client, cookieStore);
		}

		return client;
	}

	public MultipartEntity hashMapToMultiPair(HashMap<String, String> hashmap) throws Exception {
		MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
		Iterator<Map.Entry<String, String>> entries = hashmap.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry<String, String> entry = entries.next();
			reqEntity.addPart(entry.getKey(), new StringBody(entry.getValue(), Charset.defaultCharset()));
		}
		return reqEntity;
	}

	/**
	 * Use this method to parse a hashMap<String,String> to a NProgressMultipartEntity with a progressListener. This is used if u have to upload files and track progress on-the-fly
	 * @param hashmap
	 * @param progressListener
	 * @param mNMultiPartEnityBase64PairList 
	 * @return NProgressMultipartEntity
	 * @throws Exception
	 */
	public NProgressMultipartEntity hashMapToNMultiPair(HashMap<String, String> hashmap, NProgressListener progressListener, ArrayList<NMultiPartEntityBase64Pair> mNMultiPartEnityBase64PairList) throws Exception {
		NProgressMultipartEntity pairs = new NProgressMultipartEntity(progressListener);
		Iterator<Map.Entry<String, String>> entries = hashmap.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry<String, String> entry = entries.next();
			pairs.addPart(entry.getKey(), new StringBody(entry.getValue()));
		}
		if(mNMultiPartEnityBase64PairList!=null){
			for(NMultiPartEntityBase64Pair item : mNMultiPartEnityBase64PairList)
				pairs.addPart(item.getName(), item.getContentBody());
		}
		return pairs;
	}

	/**
	 * Use this method to parse a hashMap<String,String to UrlEncodedFormEntity (pairs) which can get setted to httpPost/httpPut object before exucting
	 * @param hashmap
	 * @return UrlEncodedFormEntity
	 * @throws Exception
	 */
	public UrlEncodedFormEntity hashMapToPair(HashMap<String,String> hashmap) throws Exception {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		Iterator<Map.Entry<String, String>> entries = hashmap.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry<String, String> entry = entries.next();
			pairs.add(new BasicNameValuePair(entry.getKey(),entry.getValue()));
		}    	
		return new UrlEncodedFormEntity((pairs), HTTP.UTF_8);
	}

	/**
	 * Override this method and do the calculation on heap and return false if you need a cooldown with System.gc() before continuing the call
	 * @return
	 */
	public boolean isEnoughMemory(){
		return true;
	}



	/**
	 * Use this method to parse a NJSONObject to a UrlEncodedFormEntity. The NJSONObject have be only key & values in one main object
	 * @param object
	 * @return UrlEncodedFormEntity
	 * @throws Exception
	 */
	public UrlEncodedFormEntity jsonToUrlEncodedFormEntity(NJSONObject object) throws Exception {
		if (object == null)
			return null;
		return hashMapToPair(NUtils.jsonToHashMap(object));
	}

	public HttpEntity jsonToHttpEntity(NJSONObject object) throws Exception {
		HttpEntity entity;
		StringEntity se = new StringEntity(object.toString(),"UTF-8");
		se.setContentType("application/json");
		se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,"application/json"));
		entity = se;
		return entity;
	}

	public HttpEntity jsonToHttpEntityNoEncoding(NJSONObject object) throws Exception {
		HttpEntity entity;
		StringEntity se = new StringEntity(object.toString(),"UTF-8");
		se.setContentType("application/json");
		entity = se;
		return entity;
	}

	/**
	 * Make a json entity explicitly using content-type utf-8 cahrset
	 * @param object
	 * @return HttpEntity with content-type: application/json;charset=utf-8
	 * @throws Exception
	 */
	public HttpEntity jsonToHttpEntityUTF8(NJSONObject object) throws Exception {
		HttpEntity entity;
		StringEntity se = new StringEntity(object.toString(),"UTF-8");
		se.setContentType("application/json;charset=utf-8");
		se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,"application/json;charset=utf-8"));
		entity = se;
		return entity;
	}

	private CookieStore loadCookies(){
		if(cookieData==null || cookieData.isEmpty())
			return null;

		List<Cookie> list = NWebserviceCookieController.getCookieList(cookieData.getCookieList());

		if(list!=null && list.size()>0){
			List<Cookie> cookieList=list;
			CookieStore store = new BasicCookieStore();
			for(int a = 0 ; a <cookieList.size() ; a++){
				store.addCookie(cookieList.get(a));
			}
			return store;
		}

		return null;
	}

	/**
	 * Use this method to create a JSON object from a HttpResponse, GZIP & Normal encoding is supported
	 * @param response
	 * @return NJSONObject
	 * @throws IOException
	 */
	public NJSONObject responseToJSON(HttpResponse response) throws IOException {
		if(response == null || response.getEntity() == null)
			return new NJSONObject();
		
		InputStream instream = null;

		try{
			if(NWebserviceConstants.COOKIES_CONTROLL_ACTIVE)
				saveCookies();

			instream = response.getEntity().getContent();

			Header contentEncoding = response.getFirstHeader("Content-Encoding");
			if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
				instream = new GZIPInputStream(instream);
				new BufferedReader(new InputStreamReader(instream));
			}	
			else{
				new BufferedReader(new InputStreamReader(instream, "UTF-8"));
			}

			String output = NStringController.convertStreamToString(instream);
			
			NJSONObject jsonResponse = new NJSONObject();

			if(output == null || output.length() == 0){
				NLog.e("responseToJSON", "Output was null, retutning empty object");
				return jsonResponse;
			}
				
			try{
				jsonResponse = new NJSONObject(output);
			}
			catch(Exception e){
				NLog.d("responseToJSON", "Failed creating a JSONObject from response, trying JSONArray");
				try{
					jsonResponse.put("array", new NJSONArray(output));
				}
				catch(Exception ee){
					NLog.d("responseToJSON", "Failed creating a JSONArray from response, trying adding response to a JSONObject in field: output");
					jsonResponse.put("output", output);
					NLog.d("response responseToJSON",output);
				}
			}
			instream.close();

			if(NWebserviceConstants.LAST_MODIFIED_ACTIVE){
				String lastModified = getLastModifiedFromResponse(response);
				if(lastModified != null)
					jsonResponse.put("last_modified", lastModified);
			}

			return jsonResponse;
		}
		catch(Exception e){
			NLog.e("responseToJSON",e);

			if(instream!=null)
				instream.close();
			return null;
		}	
	}

	public static void copyStream(InputStream is, OutputStream os){
		final int buffer_size = 1024;
		try
		{
			byte[] bytes=new byte[buffer_size];
			for(;;)
			{
				int count=is.read(bytes, 0, buffer_size);
				if(count==-1)
					break;
				os.write(bytes, 0, count);
			}
		}
		catch(Exception ex){}
	}

	public static String convertStreamToString(InputStream is) {
		ByteArrayOutputStream oas = new ByteArrayOutputStream();
		copyStream(is, oas);
		String t = oas.toString();
		try {
			oas.close();
			oas = null;
		} catch (IOException e) {
			NLog.e("NStringController convertStreamToString", e);
		}
		return t;
	}

	/**
	 * Can be used if the return is not a NJSONObject
	 * @param response
	 * @return
	 * @throws IOException
	 */
	public String responseToString(HttpResponse response) throws IOException {
		InputStream instream = null;

		try{
			if(NWebserviceConstants.COOKIES_CONTROLL_ACTIVE)
				saveCookies();

			instream= response.getEntity().getContent();

			Header contentEncoding = response.getFirstHeader("Content-Encoding");
			if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
				instream = new GZIPInputStream(instream);
				new BufferedReader(new InputStreamReader(instream));
			}	
			else{
				new BufferedReader(new InputStreamReader(instream, "UTF-8"));
			}

			String output = NStringController.convertStreamToString(instream);

			instream.close();

			return output;

		}
		catch(Exception e){
			NLog.e("output",e);

			if(instream!=null)
				instream.close();
			return null;
		}	
	}

	private void saveCookies(){
		try{
			List<Cookie> cookieList1 = httpClientForeground.getCookieStore().getCookies();
			List<Cookie> cookieList2 = httpClientBackground.getCookieStore().getCookies();

			NWebserviceCookieController.setCookieList(NWebserviceCookieController.mergeCookieLists(cookieList1, cookieList2));
		}
		catch(Exception e){
			Log.e("saveCookies", e.toString());
		}
	}

	/**
	 * Use this method to save cookies from a httpClient objejct, should be used after a execute and only if curl is'nt used
	 * @param httpClient
	 */
	public void saveCookies(DefaultHttpClient httpClient){
		try{
			List<Cookie> cookieList1 = httpClient.getCookieStore().getCookies();
			NWebserviceCookieController.setCookieList(cookieList1);
		}
		catch(Exception e){
			Log.e("saveCookies", e.toString());
		}
	}

	/**
	 * Use this method to set headers, these headers will be used in all call until clear or setting again
	 * @param myHeaderArrayList
	 */
	public void setMyHeaderArrayList(ArrayList<BasicHeader> myHeaderArrayList){
		this.myHeaderArrayList = myHeaderArrayList;
	}

	public void setUseRetry(boolean useRetry){
		this.useRetry = useRetry;
	}

	/**
	 * This method will UsernamePasswordCredentials as header on the HttpRequest added
	 * @param mHttpRequest
	 * @param login
	 * @param password
	 * @throws Exception
	 */
	private void setUsernamePasswordCredentials(HttpRequest mHttpRequest, String login, String password) throws Exception {
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(login, password);
		BasicScheme scheme = new BasicScheme();
		Header authorizationHeader = scheme.authenticate(credentials, mHttpRequest);
		mHttpRequest.addHeader(authorizationHeader);		
	}

	/**
	 * Use this method to set a UsernamePasswordCredentials to each call. Set login and password, and they will get used on each call if !=null
	 * @param login
	 * @param password
	 */
	public void setUsernamePasswordCredentials(String login,String password){
		this.login = login;
		this.password = password;
	}

	/**
	 * This method will call useWait(250), which will sleep the current thread for 1000ms if possible
	 */
	private void useWait() {
		useWait(250);
	}

	/**
	 * Use this method to sleep the thread if possible
	 * @param ms
	 */
	private void useWait(int ms){
		NLog.d("NWebserviceController UseWait", "Waiting.... "+ms+"ms");
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {}
	}


	@SuppressWarnings("deprecation")
	public static Drawable getDrawableFromURL(String url,int outputWidth,int outputHeight) {
		try {
			URL urlSource = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) urlSource.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inTempStorage = new byte[16*1024];        
			Bitmap myBitmap = BitmapFactory.decodeStream(input, null, options);

			int oldWidth = myBitmap.getWidth();
			int oldHeight = myBitmap.getHeight();

			float scaleWidth = ((float) outputWidth) / oldWidth;
			float scaleHeight = ((float) outputHeight) / oldHeight;

			// createa matrix for the manipulation
			Matrix matrix = new Matrix();
			// resize the bit map
			matrix.postScale(scaleWidth, scaleHeight);

			// recreate the new Bitmap
			Bitmap resizedBitmap = Bitmap.createBitmap(myBitmap, 0, 0, oldWidth, oldHeight, matrix, true);

			return new BitmapDrawable(resizedBitmap);

		} catch (Exception e) {
			NLog.e("NWebserviceController getDrawableFromURL",e);
			return null;
		}
	}	

	public void killAllConnections(){
		httpClientForeground.getConnectionManager().shutdown();
		httpClientBackground.getConnectionManager().shutdown();
		init();
	}

	public void logHeaders(){
		for(BasicHeader item : myHeaderArrayList)
			NLog.d("Header",item.getName()+" "+item.getValue());
	}
	
	/**
	 * From
	 * http://stackoverflow.com/questions/12207373/http-patch-request-from-android
	 */
	public class HttpPatch extends HttpPost {
	    public static final String METHOD_PATCH = "PATCH";

	    public HttpPatch() {
			super();
		}

		public HttpPatch(URI uri) {
			super(uri);
		}

		public HttpPatch(final String url) {
	        super(url);
	    }

	    @Override
	    public String getMethod() {
	        return METHOD_PATCH;
	    }
	}
}

