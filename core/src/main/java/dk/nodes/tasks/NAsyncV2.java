package dk.nodes.tasks;

import android.os.AsyncTask;

import dk.nodes.tasks.NAsyncV2.Holder;
import dk.nodes.utils.NLog;
import dk.nodes.webservice.NWebserviceConstants;
import dk.nodes.webservice.models.NResponse;

public abstract class NAsyncV2<T> extends AsyncTask<Void, Void, Holder<T>> {

	private NAsyncListener<T>	mNAsyncListener;
	private Holder<T> mHolder;
	private boolean mIsFetching = false;
	protected abstract NResponse doWebserviceCall() throws Exception;
	protected abstract T doParsing( NResponse response ) throws Exception;
	protected abstract void setData( T t ) throws Exception;
	
	public NAsyncV2( NAsyncListener<T> listener ) {
		mNAsyncListener = listener;
		mHolder = new Holder<T>();
	}

	@Override
	protected Holder<T> doInBackground( Void... params ) {
		mIsFetching = true;
		
		mHolder.response = null;
		mHolder.obj = null;
		
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
		
		if( mNAsyncListener == null )
			return;
		if( NWebserviceConstants.isApiSuccess( mHolder.response ) )
			mNAsyncListener.onSuccess( mHolder.obj );
		else if( mHolder.response.getResponseCode() == NWebserviceConstants.API_CONNECTION_ERROR ) 
			mNAsyncListener.onConnectionError( mHolder.response.getResponseCode() );
		else 
			mNAsyncListener.onError( mHolder.response.getResponseCode() );
		
		mNAsyncListener.onAlways();
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

	public interface NAsyncListener<T> {
		public void onSuccess(T result);
		public void onConnectionError(int code);
		public void onError(int code);
		public void onAlways();
	}
}
