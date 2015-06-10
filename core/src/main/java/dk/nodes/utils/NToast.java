package dk.nodes.utils;

import android.content.Context;
import android.widget.Toast;

public class NToast {
	private static final String TAG = NToast.class.getName();

	public static void execute(Context mContext, String msg){
		if(mContext != null)
			Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
		else
			NLog.e(TAG+" execute", "Context is null, can't make the toast");
	}
	
	public static void executeShort(Context mContext, String msg){
		if(mContext != null)
			Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
		else
			NLog.e(TAG+" execute", "Context is null, can't make the toast");
	}
}
