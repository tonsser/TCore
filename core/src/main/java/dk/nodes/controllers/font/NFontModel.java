package dk.nodes.controllers.font;
/**
 * @author Casper Rasmussen - 2012
 */

import android.graphics.Typeface;

import java.io.Serializable;

public class NFontModel implements Serializable {

	private String type;
	private Typeface font;
	private int sizeInPixels;
	private boolean sizeApplied;
	
	public NFontModel(char type, Typeface font, int sizeInPixels){
		this.type = String.valueOf(type);
		this.font = font;
		this.sizeInPixels = sizeInPixels;
		sizeApplied = true;			
	}
	
	public NFontModel(char type, Typeface font){
		this.type = String.valueOf(type);
		this.font = font;
		sizeApplied = false;			
	}
	
	public NFontModel(String type, Typeface font, int sizeInPixels){
		this.type = type;
		this.font = font;
		this.sizeInPixels = sizeInPixels;
		sizeApplied = true;			
	}
	
	public NFontModel(String type, Typeface font){
		this.type = type;
		this.font = font;
		sizeApplied = false;			
	}
	
	public boolean isSizeApplied(){
		return sizeApplied;
	}
	
	public int getSizeInPixels(){
		return sizeInPixels;
	}
	public String getType(){
		return type;
	}
	
	public Typeface getFont(){
		return font;
	}
}
