package dk.tonsser.controllers.heap;
/**
 * @author Casper Rasmussen 2012
 */

import android.os.AsyncTask;

@Deprecated
public class NLowHeapASync extends AsyncTask<String, Void, Boolean> {

    private int coolDownMs;
    private LowHeapAsyncListener mLowHeapAsyncListener;


    public NLowHeapASync(int coolDownMs, LowHeapAsyncListener mLowHeapAsyncListener) {
        this.coolDownMs = coolDownMs;
        this.mLowHeapAsyncListener = mLowHeapAsyncListener;
    }

    protected void onPreExecute() {
        if (mLowHeapAsyncListener != null)
            mLowHeapAsyncListener.onProgressDialogShow();
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (mLowHeapAsyncListener != null) {
            mLowHeapAsyncListener.onProgressDialogCancel();
        }
    }

    @Override
    protected Boolean doInBackground(String... params) {
        System.gc();

        try {
            Thread.sleep((long) (coolDownMs));
        } catch (InterruptedException e) {
        }

        System.gc();
        return true;
    }

    public interface LowHeapAsyncListener {
        void onProgressDialogShow();

        void onProgressDialogCancel();
    }
}