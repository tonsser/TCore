package dk.tonsser.base.splashactivity;

import android.content.res.Configuration;
import android.graphics.Color;
import android.view.View;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.tonsser.core.R;

import dk.tonsser.base.TBaseApplication;
import dk.tonsser.base.TBaseFragmentActivity;
import dk.tonsser.utils.TBuild;
import dk.tonsser.utils.TLog;

/**
 * For testing with networkr - this activity cannot show support dialogs and the other activities cannot show normal dialogs
 */
public abstract class TBaseSplashSupportActivity extends TBaseFragmentActivity {

    private static String TAG = TBaseSplashSupportActivity.class.getSimpleName();
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

    private void setTextColor(int color, TextView... tv) {
        for (TextView item : tv)
            item.setTextColor(color);
    }

    private void setDebugOverlay() {
        if (TBaseApplication.getInstance().DEBUG) {
            splashDebugTv.setVisibility(View.VISIBLE);
            splashVersionTv.setVisibility(View.VISIBLE);
            splashVersionTv.setText("Version: " + TBuild.getVersionName(getBaseContext()));
        } else {
            splashDebugTv.setVisibility(View.GONE);
            splashVersionTv.setVisibility(View.GONE);
        }

        if (TBaseApplication.getInstance().WEBSERVICE_URL == null) {
            splashOfficecloudTv.setVisibility(View.VISIBLE);
            splashOfficecloudTv.setText("WEBSERVICE_URL is null");
        } else if (TBaseApplication.getInstance().WEBSERVICE_URL_DEBUG != null && TBaseApplication.getInstance().WEBSERVICE_URL.equals(TBaseApplication.getInstance().WEBSERVICE_URL_DEBUG)) {
            splashOfficecloudTv.setVisibility(View.VISIBLE);
            TLog.d("WEBSERVICE_URL", TBaseApplication.getInstance().WEBSERVICE_URL);
            TLog.d("WEBSERVICE_URL_DEBUG", TBaseApplication.getInstance().WEBSERVICE_URL_DEBUG);
            TLog.d("WEBSERVICE_URL_LIVE", TBaseApplication.getInstance().WEBSERVICE_URL_LIVE);
            TLog.d("WEBSERVICE_TRANSLATION_URL", TBaseApplication.getInstance().WEBSERVICE_TRANSLATION_URL);
        } else {
            splashOfficecloudTv.setVisibility(View.GONE);
        }
        String keyBuilder = "";
        if (TBaseApplication.getInstance().GOOGLE_ANALYTICS_API == null) {
            if (TBaseApplication.getInstance().GOOGLE_ANALYTICS_API == null)
                keyBuilder += " FLURRY_API GOOGLE_ANALYTICS_API ";
        }

        if (keyBuilder.length() == 0)
            splashKeyTv.setVisibility(View.GONE);
        else
            splashKeyTv.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (TBaseApplication.getInstance().DEBUG && clickToContinueInDebug) {
            View debugView = getLayoutInflater().inflate(R.layout.splash_debug_text, null, true);
            addContentView(debugView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            initDebugOverlay();
            setDebugOverlay();
        }
    }

}
