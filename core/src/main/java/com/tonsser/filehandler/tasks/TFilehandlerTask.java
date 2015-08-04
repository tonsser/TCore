package com.tonsser.filehandler.tasks;

/**
 * @author Johnny Sørensen 2012
 */

import android.content.Context;

public class TFilehandlerTask {

    private Context mContext;
    private String mFileName;

    /**
     * https://github.com/nodesagency-mobile/Android-NCore/wiki/Filehandler
     *
     * @param mContext
     * @param fileName
     */
    public TFilehandlerTask(Context context, String fileName) {
        this.mContext = context;
        this.mFileName = fileName;
    }

    public void load(final FileHandlerLoadCallback callback) {
        new TFilehandlerLoadTask(mContext, mFileName) {
            @Override
            protected void onPostExecute(Object data) {
                if (data != null) {
                    callback.onFinished(data);
                } else {
                    callback.onError();
                }
            }
        }.execute();
    }

    public void save(Object data, final FileHandlerSaveCallback callback) {
        new TFilehandlerSaveTask(mContext, mFileName, data) {
            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {
                    callback.onFinished();
                } else {
                    callback.onError();
                }
            }
        }.execute();
    }

    public interface FileHandlerLoadCallback {
        void onFinished(Object obj);

        void onError();
    }

    public interface FileHandlerSaveCallback {
        void onFinished();

        void onError();
    }
}