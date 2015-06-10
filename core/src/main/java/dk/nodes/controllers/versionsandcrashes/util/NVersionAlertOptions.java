package dk.nodes.controllers.versionsandcrashes.util;

public class NVersionAlertOptions {
	private String header;
	private String message;
	private String modified;

	public NVersionAlertOptions(String header, String message, String modified) {
		this.header = header;
		this.message = message;
		this.modified = modified;
	}
	
	public String getHeader() {
		return header;
	}
	public String getMessage() {
		return message;
	}
	public String getModified() {
		return modified;
	}
}
