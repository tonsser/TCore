package dk.tonsser.controllers.dialogqueuing;

public interface NDialogQueueInterface {
	void show();
	void cancel();
	void setQueueListener(NDialogQueueListener listener);
	String getQueueTag();
}
