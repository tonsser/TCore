package dk.nodes.filehandler.tasks;

/**
 * @author Johnny Sørensen 2012
 */
import android.content.Context;

public class NFilehandlerTask {

	private Context mContext;
	private String mFileName;

	/**
	 * https://github.com/nodesagency-mobile/Android-NCore/wiki/Filehandler
	 * 
	 * @param mContext
	 * @param fileName
	 */
	public NFilehandlerTask( Context context, String fileName ) {
		this.mContext = context;
		this.mFileName = fileName;
	}
	
	public void load( final FileHandlerLoadCallback callback ) {
		new NFilehandlerLoadTask( mContext, mFileName ) {
			@Override
			protected void onPostExecute( Object data ) {
				if( data != null ) {
					callback.onFinished( data );
				} else {
					callback.onError();
				}
			}
		}.execute();
	}
	
	public void save( Object data, final FileHandlerSaveCallback callback ) {
		new NFilehandlerSaveTask( mContext, mFileName, data ) {
			@Override
			protected void onPostExecute( Boolean result ) {
				if( result ) {
					callback.onFinished();
				} else {
					callback.onError();
				}
			}
		}.execute();
	}
	
	public interface FileHandlerLoadCallback {
		public void onFinished(Object obj);
		public void onError();
	}
	
	public interface FileHandlerSaveCallback {
		public void onFinished();
		public void onError();
	}
}