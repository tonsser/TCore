package dk.nodes.webservice.models;

@Deprecated
public interface NApiAsyncListener {
	public void onSuccess(int code);
	public void onConnectionError(int code);
	public void onError(int code);
	public void onAlways();
}

