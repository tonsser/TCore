package com.tonsser.filehandler;
/**
 * @author Casper Rasmussen 2012
 */

import android.content.Context;

import com.tonsser.filehandler.tasks.TFilehandlerSaveTask;
import com.tonsser.utils.TLog;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class NFileHandler {

    private String TAG = NFileHandler.class.getName();
    private Context mContext;
    private String fileName;
    private static NFileHandler instance;


    /**
     * Call this method to get instance, if instance is not set, it will return null.
     * Call setInstance() to create an instance
     *
     * @return NFileHandler or null
     */
    public static NFileHandler getInstance() {
        if (instance == null)
            return null;
        else
            return instance;
    }

    /**
     * Setting instance of NFileHandler
     *
     * @param instance
     */
    public static void setInstance(NFileHandler instance) {
        NFileHandler.instance = instance;
    }

    /**
     * Setting instance of NFilehandler
     *
     * @param mContext
     * @param fileName
     */
    public static void setInstance(Context mContext, String fileName) {
        instance = new NFileHandler(mContext, fileName);
    }

    /**
     * Saves data async, so we dont block anything
     *
     * @param data Main data object from Application
     */
    public void saveDataAsync(Object data) {
        if (instance != null)
            new TFilehandlerSaveTask(instance, data).execute();
    }

    /**
     * https://github.com/nodesagency-mobile/Android-NCore/wiki/Filehandler
     *
     * @param mContext
     * @param fileName
     */
    public NFileHandler(Context mContext, String fileName) {
        this.mContext = mContext;
        this.fileName = fileName;
    }

    public void saveData(Object data) {
        if (data == null || mContext == null || fileName == null) {
            TLog.e(TAG + " saveData", "data || context || fileName is null -  returning....");
            return;
        }
        try {
            FileOutputStream fos = mContext.openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(data);
            os.close();
            TLog.d("NFileHandler", "File Saved [" + fileName + "]");
        } catch (Exception e) {
            TLog.e("NFileHandler saveData", e);
        }
    }

    public Object loadData() {
        try {
            FileInputStream fis = mContext.openFileInput(fileName);
            ObjectInputStream is = new ObjectInputStream(fis);
            Object output = is.readObject();
            is.close();
            TLog.d("NFileHandler", "File Loaded [" + fileName + "]");
            return output;
        } catch (Exception e) {
            TLog.e("NFileHandler loadData", e);
            return null;
        }
    }
}