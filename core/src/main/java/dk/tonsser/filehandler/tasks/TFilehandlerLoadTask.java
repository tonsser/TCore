package dk.tonsser.filehandler.tasks;

import android.content.Context;
import android.os.AsyncTask;

import java.io.FileInputStream;
import java.io.ObjectInputStream;

import dk.tonsser.utils.TLog;

public class TFilehandlerLoadTask extends AsyncTask<Void, Object, Object> {

    private String mFileName;
    private Context mContext;

    public TFilehandlerLoadTask(Context context, String data) {
        mFileName = data;
        mContext = context;
    }

    @Override
    protected Object doInBackground(Void... params) {
        Object loadedData = loadData();
        return loadedData;
    }

    private Object loadData() {
        try {
            FileInputStream fis = mContext.openFileInput(mFileName);
            ObjectInputStream is = new ObjectInputStream(fis);
            Object output = is.readObject();
            is.close();
            TLog.d("NAsyncFileLoadTask", "File Loaded [" + mFileName + "]");
            return output;
        } catch (Exception e) {
            TLog.e("NAsyncFileLoadTask loadData", e);
            return null;
        }
    }

}
