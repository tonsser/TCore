package dk.tonsser.utils;

import android.view.View;

public class NViewPadding {

	private int top;
	private int bottom;
	private int left;
	private int right;
	
	public NViewPadding(int top, int bottom, int left, int right) {
		this.top = top;
		this.bottom = bottom;
		this.left = left;
		this.right = right;
	}
	public int getTop() {
		return top;
	}
	public int getBottom() {
		return bottom;
	}
	public int getLeft() {
		return left;
	}
	public int getRight() {
		return right;
	}
	public void setTop(int top) {
		this.top = top;
	}
	public void setBottom(int bottom) {
		this.bottom = bottom;
	}
	public void setLeft(int left) {
		this.left = left;
	}
	public void setRight(int right) {
		this.right = right;
	}
	
	public void setPadding(View v){
		v.setPadding(left, top, right, bottom);
	}
}
