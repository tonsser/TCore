package dk.nodes.webservice.models;

import org.apache.http.entity.mime.content.ByteArrayBody;

public class NMultiPartEntityPair {

	private String name;
	private ByteArrayBody mByteArrayBody;
	
	public NMultiPartEntityPair(String name,ByteArrayBody mByteArrayBody){
		this.name = name;
		this.mByteArrayBody = mByteArrayBody;
	}
	
	public String getName(){
		return name;
	}
	
	public ByteArrayBody getByteArrayBody(){
		return mByteArrayBody;
	}
}
