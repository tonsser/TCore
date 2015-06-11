package dk.nodes.webservice.models;

import org.apache.http.entity.mime.content.ContentBody;

@Deprecated
public class NMultiPartEntityBase64Pair {

	private String name;
	private ContentBody mContentBody;
	
	public NMultiPartEntityBase64Pair(String name,ContentBody mContentBody){
		this.name = name;
		this.mContentBody = mContentBody;
	}
	
	public String getName(){
		return name;
	}
	
	public ContentBody getContentBody(){
		return mContentBody;
	}
}
