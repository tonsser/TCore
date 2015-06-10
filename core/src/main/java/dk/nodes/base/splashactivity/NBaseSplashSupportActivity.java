package dk.nodes.base.splashactivity;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import dk.nodes.base.NBaseApplication;
import dk.nodes.base.NBaseFragmentActivity;
import dk.nodes.base.splashactivity.NBaseSplashTask.NBaseSplashTaskDoInBackground;
import dk.nodes.ncore.R;
import dk.nodes.utils.NBuild;
import dk.nodes.utils.NLog;
import dk.nodes.utils.NToast;
import dk.nodes.webservice.NWebserviceConstants;
import dk.nodes.webservice.models.NApiAsyncListener;

/**
 * For testing with networkr - this activity cannot show support dialogs and the other activities cannot show normal dialogs
 */
public abstract class NBaseSplashSupportActivity extends NBaseFragmentActivity implements NApiAsyncListener, NBaseSplashTaskDoInBackground{
	
	private static String TAG = NBaseSplashActivity.class.getSimpleName();
	protected boolean allowTouchToContinue = true;
	private boolean loaded = false;
	private Timer timer;
	private long startTime;
	private TextView splashDebugTv;
	private TextView splashOfficecloudTv;
	private TextView splashVersionTv;
	protected int debugTextColor = Color.BLACK;
	private TextView splashKeyTv;
	private AsyncTask<String, Void, Integer> mNBaseSplashTask;
	private boolean isWaitingForResumeToContinue;
	private Integer storedResponseCode;
	protected boolean clickToContinueInDebug;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		SHAKE_FEEDBACK_ENABLED = false;
		super.onCreate(savedInstanceState);
		
		loadData();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	private void initDebugOverlay() {
		splashDebugTv = (TextView) findViewById(R.id.splash_debug_tv);
		splashOfficecloudTv = (TextView) findViewById(R.id.splash_officecloud_tv);
		splashVersionTv = (TextView) findViewById(R.id.splash_version_tv);
		splashKeyTv = (TextView) findViewById(R.id.splash_keys_tv);
		setTextColor(debugTextColor, splashDebugTv, splashOfficecloudTv, splashVersionTv, splashDebugTv);
	}

	private void setTextColor(int color,TextView...tv){
		for(TextView item : tv)
			item.setTextColor(color);
	}

	private void setDebugOverlay() {
		if(NBaseApplication.getInstance().DEBUG){
			splashDebugTv.setVisibility(View.VISIBLE);
			splashVersionTv.setVisibility(View.VISIBLE);
			splashVersionTv.setText("Version: " + NBuild.getVersionName(getBaseContext()));
		}
		else{
			splashDebugTv.setVisibility(View.GONE);
			splashVersionTv.setVisibility(View.GONE);
		}

		if(NBaseApplication.getInstance().WEBSERVICE_URL == null){
			splashOfficecloudTv.setVisibility(View.VISIBLE);
			splashOfficecloudTv.setText("WEBSERVICE_URL is null");
		}
		else if(NBaseApplication.getInstance().WEBSERVICE_URL_DEBUG != null && NBaseApplication.getInstance().WEBSERVICE_URL.equals(NBaseApplication.getInstance().WEBSERVICE_URL_DEBUG)){
			splashOfficecloudTv.setVisibility(View.VISIBLE);
			NLog.d("WEBSERVICE_URL",NBaseApplication.getInstance().WEBSERVICE_URL);
			NLog.d("WEBSERVICE_URL_DEBUG",NBaseApplication.getInstance().WEBSERVICE_URL_DEBUG);
			NLog.d("WEBSERVICE_URL_LIVE",NBaseApplication.getInstance().WEBSERVICE_URL_LIVE);
			NLog.d("WEBSERVICE_TRANSLATION_URL",NBaseApplication.getInstance().WEBSERVICE_TRANSLATION_URL);
		}			
		else{
			splashOfficecloudTv.setVisibility(View.GONE);
		}
		String keyBuilder = "";
		if(NBaseApplication.getInstance().GOOGLE_ANALYTICS_API == null){
			if(NBaseApplication.getInstance().GOOGLE_ANALYTICS_API == null)
				keyBuilder+=" FLURRY_API GOOGLE_ANALYTICS_API ";
		}

		if(keyBuilder.length() == 0)
			splashKeyTv.setVisibility(View.GONE);
		else
			splashKeyTv.setVisibility(View.VISIBLE);
	}		

	@Override
	protected void onResume() {
		super.onResume();
		if(isWaitingForResumeToContinue)
			handleResponseCode(storedResponseCode);
			
	}

	private void handleResponseCode(int code){
		if(NWebserviceConstants.isApiSuccess(code) || code == 0)
			onSuccess(code);
		else if(code == NWebserviceConstants.API_CONNECTION_ERROR)
			onConnectionError(code);
		else 
			onError(code);
	}
	protected void loadData() {
		mNBaseSplashTask = new NBaseSplashTask(this,this){

			@Override
			protected void onPostExecute(Integer code) {
				if(isResumedOnly())
					super.onPostExecute(code);
				else{
					isWaitingForResumeToContinue = true;
					storedResponseCode = code;
				}
			}
		}.execute();	
		
		startTime = System.currentTimeMillis();
	}

	@Override
	public void onSuccess(int code) {
		long usedTime = System.currentTimeMillis() - startTime;
		long deltaTime = Math.max(0, NBaseApplication.getInstance().SPLASH_SCREEN_DELAY - usedTime);
		startTimeForSplashScreenOver(deltaTime);
	}

	protected void startTimeForSplashScreenOver(long msDelay){
		if(timer!=null)
			timer.cancel();

		timer = new Timer();

		TimerTask timerTask = new TimerTask(){
			public void run(){			  
				onSplashScreenOver();
			}
		};	
		timer.schedule(timerTask, new Date(System.currentTimeMillis() + msDelay));
	}

	/**
	 * This will get called when the splash screen have been showed the right amount of time
	 */
	public abstract void onSplashScreenOver();

	@Override
	protected void onStart() {
		super.onStart();
		if(!loaded){
			if(NBaseApplication.getInstance().DEBUG && clickToContinueInDebug){
				View debugView = getLayoutInflater().inflate(R.layout.splash_debug_text,null,true);
				addContentView(debugView,new LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
				initDebugOverlay();
				setDebugOverlay();
				loaded = true;
				debugView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if(allowTouchToContinue){
							if(timer!=null)
								timer.cancel();
							NToast.executeShort(getBaseContext(), "Only works in debug mode");
							onSplashScreenOver();
						}
					}
				});
			}
		}
	}
	
	@Override
	public void onAlways() {
	}
}
