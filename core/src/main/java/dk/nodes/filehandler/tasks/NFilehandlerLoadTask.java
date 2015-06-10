package dk.nodes.filehandler.tasks;

import android.content.Context;
import android.os.AsyncTask;

import java.io.FileInputStream;
import java.io.ObjectInputStream;

import dk.nodes.utils.NLog;

public class NFilehandlerLoadTask extends AsyncTask<Void, Object, Object> {

	private String mFileName;
	private Context mContext;
	
	public NFilehandlerLoadTask( Context context, String data ) {
		mFileName = data;
		mContext = context;
	}
	
	@Override
	protected Object doInBackground( Void... params ) {
		Object loadedData = loadData();
		return loadedData;
	}
	
	private Object loadData() {
		try {
			FileInputStream fis = mContext.openFileInput( mFileName );
			ObjectInputStream is = new ObjectInputStream( fis );
			Object output = is.readObject();
			is.close();
			NLog.d( "NAsyncFileLoadTask", "File Loaded [" + mFileName + "]" );
			return output;
		} catch (Exception e) {
			NLog.e( "NAsyncFileLoadTask loadData", e );
			return null;
		}
	}

}
