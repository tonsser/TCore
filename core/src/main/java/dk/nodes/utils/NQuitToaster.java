package dk.nodes.utils;

import android.content.Context;

import dk.nodes.base.NBaseApplication;

public class NQuitToaster {

	private static NQuitToaster instance;
	private long lastClickedBackUnix;
	private String msg = "Press back again to exit";
	
	
	public static NQuitToaster getInstance(){
		if(instance == null)
			instance = new NQuitToaster();
		
		return instance;
	}
	
	public void setMsg(String msg){
		this.msg = msg;
	}
	
	public void onBackClicked(Context mContext){
		if(NUtils.isOutdated(2000, lastClickedBackUnix)){
			NToast.executeShort(mContext, msg);
			lastClickedBackUnix = System.currentTimeMillis();
		}
		else
			NBaseApplication.broadcastFinishAll(mContext);
	}
}
