package dk.nodes.map.v2;

import android.app.Activity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.Timer;
import java.util.TimerTask;

public class NMapV2StandStillController {
	private static Timer timer;
	private static LatLng oldPos;

	public static void startStandStillSequence(final GoogleMap mMap,final Activity mActivity, final NMapV2StandStillListener mNMapV2StandStillListener) {
		if(mMap == null || mActivity == null)
			return;
		
		if(timer != null)
			timer.cancel();
		timer = new Timer();

		oldPos = mMap.getCameraPosition().target;	
		
		TimerTask timerTask = new TimerTask(){
			public void run()
			{			  
				if(mActivity != null){
					mActivity.runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							LatLng newPos = mMap.getCameraPosition().target;
							if(NLocationCalculatorV2.isLatLngsEqual(newPos, oldPos)){
								if(mNMapV2StandStillListener!=null)
									mNMapV2StandStillListener.onMapStandingStill();
								timer.cancel();
							}
							else
								oldPos = newPos;
						}
					});
				}
			}
		};	
		timer.scheduleAtFixedRate(timerTask, 150, 100);
	}

	public interface NMapV2StandStillListener{
		public void onMapStandingStill();
	}
}
