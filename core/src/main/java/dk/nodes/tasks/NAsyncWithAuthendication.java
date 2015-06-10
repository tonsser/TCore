package dk.nodes.tasks;

import dk.nodes.webservice.NWebserviceConstants;

public abstract class NAsyncWithAuthendication extends NAsync{

	private NAsyncWithAuthendicationListener mNAsyncWithAuthendicationListener;


	public NAsyncWithAuthendication(NAsyncWithAuthendicationListener mNAsyncWithAuthendicationListener){
		this.mNAsyncWithAuthendicationListener = mNAsyncWithAuthendicationListener;
	}
	
	@Override
	protected void onPreExecute() {
	}

	@Override
	protected abstract Integer doInBackground(String... params);

	@Override
	protected void onPostExecute(Integer code) {
		if(mNAsyncWithAuthendicationListener == null)
			return;
		
		if(NWebserviceConstants.isApiSuccess(code))
			mNAsyncWithAuthendicationListener.onSuccess(code);
		
		else if(code == NWebserviceConstants.API_UNAUTHORIZED || code == NWebserviceConstants.API_FORBIDDEN){
			mNAsyncWithAuthendicationListener.onUnAutherized(code);
		}
		else if(code == NWebserviceConstants.API_CONNECTION_ERROR){
			mNAsyncWithAuthendicationListener.onConnectionError(code);
		}
		else{
			mNAsyncWithAuthendicationListener.onError(code);
		}
		
		mNAsyncWithAuthendicationListener.onAlways();
	}

	@Override
	protected void onCancelled() {
	}

	@Override
	protected void onProgressUpdate(Void... values) {
	}


	public interface NAsyncWithAuthendicationListener{
		public void onSuccess(int core);
		public void onConnectionError(int code);
		public void onError(int code);
		public void onUnAutherized(int code);
		public void onAlways();
	}
}
