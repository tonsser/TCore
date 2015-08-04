package dk.tonsser.filehandler;

import android.content.Context;

import dk.tonsser.base.TBaseApplication;

public class TDataHandler {

    private static NFileHandler instance;

    public static NFileHandler getInstance(Context context) {
        if (instance == null) {
            instance = new NFileHandler(context.getApplicationContext(), TBaseApplication.FILE_NAME);
        }

        return instance;
    }


}
