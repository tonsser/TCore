package dk.tonsser.controllers.dialogqueuing;

import java.util.concurrent.ConcurrentLinkedQueue;

import dk.tonsser.utils.NLog;

/**
 * @author Johnny Sørensen - 2013
 */

public class NDialogQueueController {
    private String TAG = NDialogQueueController.class.getName();

    private static NDialogQueueController instance;
    private ConcurrentLinkedQueue<NDialogQueueInterface> mQueue = new ConcurrentLinkedQueue<NDialogQueueInterface>();
    private boolean mShowingDialog = false;

    private NDialogQueueInterface currentlyShowingInterface;

    /**
     * @return
     * @author Casper Rasmussen - 2013
     */
    public static NDialogQueueController getInstance() {
        if (instance == null)
            instance = new NDialogQueueController();

        return instance;
    }

    /**
     * Adds a dialog to the internal queue and shows it as soon as it can.
     *
     * @param NCore dialog
     */
    public void show(final NDialogQueueInterface mNDialogQueueInterface) {
        mQueue.add(mNDialogQueueInterface);

        mNDialogQueueInterface.setQueueListener(new NDialogQueueListener() {
            @Override
            public void onGone() {
                mShowingDialog = false;
                NDialogQueueController.this.dequeue();
            }
        });

        dequeue();
    }

    private void dequeue() {
        if (mShowingDialog)
            return;

        mShowingDialog = true;
        currentlyShowingInterface = mQueue.poll();

        try {
            if (currentlyShowingInterface == null) {
                mShowingDialog = false;
                return;
            }

            currentlyShowingInterface.show();

            // This could happen if activity reference is no longer able to show the dialog, so we empty the queue
        } catch (Exception e) {
            mQueue.clear();
        }
    }

    /**
     * @author Casper Rasmussen 2013
     * Will clear the queue instantly
     */
    public void clear() {
        mQueue.clear();
    }

    /**
     * @param queueTag
     * @author Casper Rasmussen 2013
     * Will clear all queued NDialogQueueInterface with a queueTag equalsIgnoreCase input queueTag
     */
    public void clear(String queueTag) {
        if (queueTag == null) {
            NLog.d(TAG + " clear", "Input queue was null, returning");
            return;
        }

        ConcurrentLinkedQueue<NDialogQueueInterface> tempQueue = new ConcurrentLinkedQueue<NDialogQueueInterface>(mQueue);
        for (NDialogQueueInterface item : mQueue) {
            if (item.getQueueTag() == null || !item.getQueueTag().equalsIgnoreCase(item.getQueueTag())) {
                tempQueue.add(item);
            }
        }
        mQueue = tempQueue;
    }

    /**
     * @param mNDialogQueueInterface
     * @author Casper Rasmussen 2013
     * Will add the NDialogQueueInterface to the queue, if there is not a NDialogQueueInterface added with same queueTag queued
     * And checking if the currently showing dialog through NDialogQueueController got same QueueTag
     */

    public void showIfNotInQueueAndNotShowing(final NDialogQueueInterface mNDialogQueueInterface) {
        if (currentlyShowingInterface == null || mNDialogQueueInterface.getQueueTag() == null)
            showIfNotInQueue(mNDialogQueueInterface);
        else {
            if (currentlyShowingInterface.getQueueTag().equalsIgnoreCase(mNDialogQueueInterface.getQueueTag()))
                return;
            else
                showIfNotInQueue(mNDialogQueueInterface);
        }
    }

    /**
     * @param mNDialogQueueInterface
     * @param queueTag
     * @author Casper Rasmussen 2013
     * Will add the NDialogQueueInterface to the queue, if there is not a NDialogQueueInterface added with same queueTag queued
     */
    public void showIfNotInQueue(final NDialogQueueInterface mNDialogQueueInterface) {
        if (mNDialogQueueInterface.getQueueTag() == null) {
            NLog.d(TAG + " showIfNotInQueue", "Input tag was null, adding the to queue");
            show(mNDialogQueueInterface);
            return;
        }

        boolean addToQueue = true;
        for (NDialogQueueInterface item : mQueue) {
            if (mNDialogQueueInterface.getQueueTag().equalsIgnoreCase(item.getQueueTag())) {
                addToQueue = false;
            }
        }
        if (addToQueue)
            show(mNDialogQueueInterface);
    }

    /**
     * @return String
     * @author Casper Rasmussen 2013
     * Can return null
     */
    public String getCurrentlyShowingQueueTag() {
        if (currentlyShowingInterface == null)
            return null;
        else
            return currentlyShowingInterface.getQueueTag();
    }
}
