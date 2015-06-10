package dk.nodes.tasks;

import android.os.AsyncTask;

import dk.nodes.tasks.NAsyncAuthV2.Holder;
import dk.nodes.utils.NLog;
import dk.nodes.webservice.NWebserviceConstants;
import dk.nodes.webservice.models.NResponse;

/**
 * @author Casper / Johnny 2014
 * @param <T>
 */
public abstract class NAsyncAuthV2<T> extends AsyncTask<Void, Void, Holder<T>> {

	private NAsyncWithAuthenticationListener<T>	mAsyncWithAuthenticationListener;
	private Holder<T> mHolder;
	private boolean mIsFetching = false;
	protected abstract NResponse doWebserviceCall() throws Exception;
	protected abstract T doParsing( NResponse response ) throws Exception;
	protected abstract void setData( T t ) throws Exception;
	
	public NAsyncAuthV2( NAsyncWithAuthenticationListener<T> listener ) {
		mAsyncWithAuthenticationListener = listener;
		mHolder = new Holder<T>();
	}

	@Override
	protected Holder<T> doInBackground( Void... params ) {
		mIsFetching = true;
		
		mHolder.obj = null;
		mHolder.response = null;
		
		try {
			mHolder.response = doWebserviceCall();
			if(mHolder.response == null)
				mHolder.response = new NResponse(NWebserviceConstants.API_CONNECTION_ERROR);
		} 
		catch( Exception e ) {
			NLog.e( e );
			mHolder.response = new NResponse(NWebserviceConstants.API_CONNECTION_ERROR);
		}
		 
		if( NWebserviceConstants.isApiSuccess( mHolder.response ) ) {
			try {
				T temp = doParsing( mHolder.response );
				mHolder.obj = temp;
				setData( temp );
			} 
			catch( Exception e ) {
				NLog.e( e );
				mHolder.response.setResponseCode(NWebserviceConstants.API_PARSE_ERROR);
			}
		}
		return mHolder;
	}

	@Override
	protected void onPostExecute( Holder<T> mHolder ) {
		mIsFetching = false;
		
		if( mAsyncWithAuthenticationListener == null )
			return;
		if( NWebserviceConstants.isApiSuccess( mHolder.response ) )
			mAsyncWithAuthenticationListener.onSuccess( mHolder.obj );
		else if( mHolder.response.getResponseCode() == NWebserviceConstants.API_UNAUTHORIZED || mHolder.response.getResponseCode() == NWebserviceConstants.API_FORBIDDEN )
			mAsyncWithAuthenticationListener.onUnAuthorized( mHolder.response.getResponseCode() );
		else if( mHolder.response.getResponseCode() == NWebserviceConstants.API_CONNECTION_ERROR ) 
			mAsyncWithAuthenticationListener.onConnectionError( mHolder.response.getResponseCode() );
		else 
			mAsyncWithAuthenticationListener.onError( mHolder.response.getResponseCode() );

		mAsyncWithAuthenticationListener.onAlways();
	}

	/**
	 * Will return true if the task is still in the state "doInBackground"
	 * @return boolean
	 */
	public boolean isFetching() {
		return mIsFetching;
	}

	public static class Holder<T> {
		T obj;
		NResponse response;
	}

	public interface NAsyncWithAuthenticationListener<T> {
		public void onSuccess(T result);
		public void onConnectionError(int code);
		public void onError(int code);
		public void onUnAuthorized(int code);
		public void onAlways();
	}
}
