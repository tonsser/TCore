package com.tonsser.filehandler.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.tonsser.filehandler.NFileHandler;
import com.tonsser.utils.TLog;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

public class TFilehandlerSaveTask extends AsyncTask<Void, Boolean, Boolean> {

    private Object mData;
    private Context mContext;
    private String mFileName;
    private NFileHandler mNFileHandler;
    private String TAG = TFilehandlerSaveTask.class.getName();

    public TFilehandlerSaveTask(Context context, String filename, Object data) {
        mData = data;
        mContext = context;
        mFileName = filename;
    }

    public TFilehandlerSaveTask(NFileHandler mNFileHandler, Object data) {
        this.mNFileHandler = mNFileHandler;
        mData = data;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        boolean result = saveData();
        return result;
    }

    public boolean saveData() {
        if (mData == null) {
            TLog.e(TAG + " saveData", "mData is null. Filename: " + mFileName);
            return false;
        }

        if (mNFileHandler == null) {
            try {
                FileOutputStream fos = mContext.openFileOutput(mFileName, Context.MODE_PRIVATE);
                ObjectOutputStream os = new ObjectOutputStream(fos);
                os.writeObject(mData);
                os.close();
                TLog.d("NAsyncFileSaveTask", "File Saved [" + mData + "], Filename: " + mFileName);
                return true;
            } catch (Exception e) {
                TLog.e("NAsyncFileSaveTask saveData. Filename: " + mFileName, e);
                return false;
            }
        } else {
            mNFileHandler.saveData(mData);
            return true;
        }
    }

}
