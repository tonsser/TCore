package dk.tonsser.base.splashactivity;

import android.content.res.Configuration;
import android.graphics.Color;
import android.view.View;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.tonsser.core.R;

import dk.tonsser.base.NBaseApplication;
import dk.tonsser.base.NBaseFragmentActivity;
import dk.tonsser.utils.NBuild;
import dk.tonsser.utils.NLog;

/**
 * For testing with networkr - this activity cannot show support dialogs and the other activities cannot show normal dialogs
 */
public abstract class NBaseSplashSupportActivity extends NBaseFragmentActivity {
	
	private static String TAG = NBaseSplashSupportActivity.class.getSimpleName();
	private TextView splashDebugTv;
	private TextView splashOfficecloudTv;
	private TextView splashVersionTv;
	protected int debugTextColor = Color.BLACK;
	private TextView splashKeyTv;
	protected boolean clickToContinueInDebug;
	
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
			NLog.d("WEBSERVICE_URL", NBaseApplication.getInstance().WEBSERVICE_URL);
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
	protected void onStart() {
		super.onStart();
			if(NBaseApplication.getInstance().DEBUG && clickToContinueInDebug){
				View debugView = getLayoutInflater().inflate(R.layout.splash_debug_text,null,true);
				addContentView(debugView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
				initDebugOverlay();
				setDebugOverlay();
		}
	}

}
