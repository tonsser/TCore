package dk.nodes.controllers.dialogqueuing;

public interface NDialogQueueInterface {
	public void show();
	public void cancel();
	public void setQueueListener(NDialogQueueListener listener);
	public String getQueueTag();
}
