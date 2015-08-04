package dk.tonsser.filehandler;

import android.content.Context;

import dk.tonsser.base.NBaseApplication;

public class NDataHandler {

	private static NFileHandler instance;
	
	public static NFileHandler getInstance( Context context ) {
		if( instance == null ) {
			instance = new NFileHandler( context.getApplicationContext(), NBaseApplication.FILE_NAME );
		}
		
		return instance;
	}
	
	
	
}
