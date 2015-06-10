package dk.nodes.base.splashactivity;

import dk.nodes.tasks.NAsync;
import dk.nodes.webservice.NWebserviceConstants;
import dk.nodes.webservice.models.NApiAsyncListener;

public class NBaseSplashTask extends NAsync {
	private NApiAsyncListener mNApiAsyncListener;
	private NBaseSplashTaskDoInBackground mNBaseSplashTaskDoInBackground;

	public NBaseSplashTask(NApiAsyncListener mNApiAsyncListener, NBaseSplashTaskDoInBackground mNBaseSplashTaskDoInBackground){
		this.mNApiAsyncListener = mNApiAsyncListener;
		this.mNBaseSplashTaskDoInBackground = mNBaseSplashTaskDoInBackground;
	}

	@Override
	protected void onPreExecute() {
	}

	@Override
	protected Integer doInBackground(String... params) {
		return mNBaseSplashTaskDoInBackground.doInBackground();
	}

	@Override
	protected void onPostExecute(Integer code) {
		if(NWebserviceConstants.isApiSuccess(code) || code == 0)
			mNApiAsyncListener.onSuccess(code);
		else if(code == NWebserviceConstants.API_CONNECTION_ERROR)
			mNApiAsyncListener.onConnectionError(code);
		else 
			mNApiAsyncListener.onError(code);
	}

	@Override
	protected void onCancelled() {
	}

	@Override
	protected void onProgressUpdate(Void... values) {
		
	}
	public interface NBaseSplashTaskDoInBackground{
		public int doInBackground();
	}
}
