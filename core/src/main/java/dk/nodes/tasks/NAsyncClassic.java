package dk.nodes.tasks;

import android.os.AsyncTask;

import dk.nodes.webservice.NWebserviceConstants;
import dk.nodes.webservice.models.NApiAsyncListener;

public abstract class NAsyncClassic extends AsyncTask<String, Void, Integer> {
	protected NApiAsyncListener mNApiAsyncListener;
	
	public NAsyncClassic(NApiAsyncListener mNApiAsyncListener){
		this.mNApiAsyncListener = mNApiAsyncListener;
	}
	protected abstract Integer doInBackground(String... params);
	@Override
	protected void onPostExecute(Integer code) {
		if(mNApiAsyncListener == null)
			return;
		
		if(NWebserviceConstants.isApiSuccess(code))
			mNApiAsyncListener.onSuccess(code);
		else if(code == NWebserviceConstants.API_CONNECTION_ERROR)
			mNApiAsyncListener.onConnectionError(code);
		else
			mNApiAsyncListener.onError(code);
		
		mNApiAsyncListener.onAlways();
	}
}
